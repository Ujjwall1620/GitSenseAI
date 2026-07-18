package com.GitSenseAI.Retriever.RAG.service;

import com.GitSenseAI.Retriever.RAG.config.RagProperties;
import com.GitSenseAI.Retriever.RAG.dto.RagQueryResponse;
import com.GitSenseAI.Retriever.RAG.dto.SourceReference;
import com.GitSenseAI.Retriever.RAG.exception.InvalidQueryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Answers questions about the analyzed repository by retrieving relevant
 * code context from the vector store and passing it to a chat model.
 * Does not modify the graph, embeddings, or vector store — read-only.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RagQueryService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final RagProperties ragProperties;

    public RagQueryResponse query(String question, Integer topK) {
        if (question == null || question.isBlank()) {
            throw new InvalidQueryException("Question must not be blank");
        }

        int effectiveTopK = topK != null ? topK : ragProperties.getDefaultTopK();

        log.info("Retrieving top {} relevant nodes for query...", effectiveTopK);

        List<Document> retrievedDocuments = vectorStore.similaritySearch(
                SearchRequest.builder().query(question).topK(effectiveTopK).build()
        );

        if (retrievedDocuments.isEmpty()) {
            log.warn("No relevant documents found in vector store for query.");
            return new RagQueryResponse(
                    "I couldn't find any relevant code in the analyzed repository to answer this question.",
                    List.of()
            );
        }

        String context = buildContext(retrievedDocuments);

        log.info("Sending query with {} context documents to chat model...", retrievedDocuments.size());

        String answer = chatClient.prompt()
                .system(ragProperties.getSystemPromptTemplate())
                .user(buildUserPrompt(question, context))
                .call()
                .content();

        List<SourceReference> sources = retrievedDocuments.stream()
                .map(this::toSourceReference)
                .toList();

        return new RagQueryResponse(answer, sources);
    }

    private String buildContext(List<Document> documents) {
        StringBuilder sb = new StringBuilder();

        for (Document document : documents) {
            sb.append("---\n").append(document.getText()).append("\n");
        }

        return sb.toString();
    }

    private String buildUserPrompt(String question, String context) {
        return "Context from the repository:\n" + context + "\nQuestion: " + question;
    }

    private SourceReference toSourceReference(Document document) {
        Object nodeId = document.getMetadata().get("nodeId");
        Object nodeType = document.getMetadata().get("nodeType");

        return new SourceReference(
                nodeId != null ? nodeId.toString() : document.getId(),
                nodeType != null ? nodeType.toString() : "UNKNOWN",
                document.getScore()
        );
    }
}
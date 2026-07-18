package com.GitSenseAI.Retriever.GRAPH.service;
import com.GitSenseAI.Retriever.PARSER.DTO.ParseResponse;
import com.GitSenseAI.Retriever.GRAPH.builder.KnowledgeGraphBuilder;
import com.GitSenseAI.Retriever.GRAPH.exception.NullParserOutputException;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.model.ProjectGraph;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KnowledgeGraphService {

    private final KnowledgeGraphBuilder knowledgeGraphBuilder;

    public KnowledgeGraphIndex buildKnowledgeGraph(ParseResponse parseResponse) {
        if (parseResponse == null) {
            throw new NullParserOutputException("Parser Module response must not be null");
        }

        ProjectGraph graph = knowledgeGraphBuilder.build(parseResponse.parseResults());

        return new KnowledgeGraphIndex(graph);
    }
}
package com.GitSenseAI.knowledgegraph.service;
import com.GitSenseAI.PARSER.DTO.ParseResponse;
import com.GitSenseAI.knowledgegraph.builder.KnowledgeGraphBuilder;
import com.GitSenseAI.knowledgegraph.exception.NullParserOutputException;
import com.GitSenseAI.knowledgegraph.index.KnowledgeGraphIndex;
import com.GitSenseAI.knowledgegraph.model.ProjectGraph;
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
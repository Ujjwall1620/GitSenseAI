package com.GitSenseAI.Bughunter.staticanalysis.rule;


import com.GitSenseAI.Bughunter.staticanalysis.dto.Finding;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;

import java.util.List;

/** A single, independent static analysis check over the Knowledge Graph. */
public interface StaticAnalysisRule {

    String getName();

    List<Finding> analyze(KnowledgeGraphIndex index);
}
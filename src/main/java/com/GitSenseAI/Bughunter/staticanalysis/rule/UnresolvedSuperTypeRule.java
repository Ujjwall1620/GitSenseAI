package com.GitSenseAI.Bughunter.staticanalysis.rule;

import com.GitSenseAI.Bughunter.staticanalysis.dto.Finding;
import com.GitSenseAI.Bughunter.staticanalysis.model.Severity;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.model.EdgeType;
import com.GitSenseAI.Retriever.GRAPH.model.GraphEdge;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists EXTENDS/IMPLEMENTS references pointing outside the scanned
 * repository. This is informational, not a bug detector — most hits are
 * completely normal (e.g. extending a JDK exception class). Useful only for
 * skimming, not for treating every entry as a problem.
 */
@Component
public class UnresolvedSuperTypeRule implements StaticAnalysisRule {

    @Override
    public String getName() {
        return "UnresolvedSuperTypeRule";
    }

    @Override
    public List<Finding> analyze(KnowledgeGraphIndex index) {
        List<Finding> findings = new ArrayList<>();

        for (GraphEdge edge : index.getGraph().getEdges()) {
            boolean isInheritanceEdge = edge.type() == EdgeType.EXTENDS || edge.type() == EdgeType.IMPLEMENTS;

            if (edge.resolved() || !isInheritanceEdge) {
                continue;
            }

            index.findNode(edge.source()).ifPresent(sourceNode ->
                    index.findNode(edge.target()).ifPresent(targetNode ->
                            findings.add(new Finding(
                                    getName(), sourceNode.id(), sourceNode.name(), sourceNode.type().name(), Severity.LOW,
                                    "References " + (edge.type() == EdgeType.EXTENDS ? "superclass" : "interface")
                                            + " '" + targetNode.name() + "', not declared in this repository. "
                                            + "Usually a JDK/library/framework type — expected in most cases.",
                                    sourceNode.filePath(), sourceNode.lineNumber()
                            ))
                    )
            );
        }

        return findings;
    }
}
package com.GitSenseAI.Bughunter.staticanalysis.rule;

import com.GitSenseAI.Bughunter.staticanalysis.dto.Finding;
import com.GitSenseAI.Bughunter.staticanalysis.model.Severity;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.model.EdgeType;
import com.GitSenseAI.Retriever.GRAPH.model.GraphNode;
import com.GitSenseAI.Retriever.GRAPH.model.NodeType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Flags methods with zero incoming CALLS edges within the scanned repository.
 *
 * Constructors are deliberately excluded: object instantiation (`new X()`)
 * was never modeled as a CALLS edge in the Knowledge Graph builder, so every
 * constructor would always appear "dead" regardless of actual usage — that
 * would be a false signal, not a real one.
 *
 * Common framework entry-point annotations are also excluded, since those
 * methods are legitimately invoked by a framework rather than by other code
 * in this repository.
 */
@Component
public class DeadCodeRule implements StaticAnalysisRule {

    private static final Set<String> ENTRY_POINT_ANNOTATIONS = Set.of(
            "Test", "GetMapping", "PostMapping", "PutMapping", "DeleteMapping", "RequestMapping",
            "Bean", "Override", "EventListener", "Scheduled", "PostConstruct"
    );

    @Override
    public String getName() {
        return "DeadCodeRule";
    }

    @Override
    public List<Finding> analyze(KnowledgeGraphIndex index) {
        List<Finding> findings = new ArrayList<>();

        for (GraphNode node : index.getGraph().getNodes()) {
            if (node.type() != NodeType.METHOD || "main".equals(node.name())) {
                continue;
            }

            boolean isEntryPoint = index.outgoingNeighbors(node.id(), EdgeType.ANNOTATED_WITH).stream()
                    .anyMatch(annotation -> ENTRY_POINT_ANNOTATIONS.contains(annotation.name()));

            if (isEntryPoint) {
                continue;
            }

            boolean hasIncomingCalls = !index.incomingNeighbors(node.id(), EdgeType.CALLS).isEmpty();

            if (hasIncomingCalls) {
                continue;
            }

            findings.add(new Finding(
                    getName(), node.id(), node.name(), node.type().name(), Severity.LOW,
                    "No calls to this method found within the scanned repository. May be unused, or a public API "
                            + "called externally, via reflection, or via framework wiring not tracked here.",
                    node.filePath(), node.lineNumber()
            ));
        }

        return findings;
    }
}
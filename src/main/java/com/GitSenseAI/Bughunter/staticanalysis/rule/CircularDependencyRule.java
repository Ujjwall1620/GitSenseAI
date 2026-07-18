package com.GitSenseAI.Bughunter.staticanalysis.rule;

import com.GitSenseAI.Bughunter.staticanalysis.dto.Finding;
import com.GitSenseAI.Bughunter.staticanalysis.model.Severity;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.model.EdgeType;
import com.GitSenseAI.Retriever.GRAPH.model.GraphNode;
import com.GitSenseAI.Retriever.GRAPH.model.NodeType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Detects circular dependencies among classes/interfaces/enums, following
 * only DEPENDS_ON edges between resolved type nodes (field-type
 * dependencies). Each distinct cycle is reported once, regardless of which
 * node the search started from.
 */
@Component
public class CircularDependencyRule implements StaticAnalysisRule {

    @Override
    public String getName() {
        return "CircularDependencyRule";
    }

    @Override
    public List<Finding> analyze(KnowledgeGraphIndex index) {
        List<Finding> findings = new ArrayList<>();
        Set<String> reportedCycles = new HashSet<>();

        for (GraphNode node : index.getGraph().getNodes()) {
            if (!isTypeNode(node)) {
                continue;
            }

            List<String> cycle = findCycleFrom(index, node.id());

            if (cycle == null) {
                continue;
            }

            String cycleKey = normalizeCycleKey(cycle);

            if (!reportedCycles.add(cycleKey)) {
                continue;
            }

            String path = String.join(" -> ", cycleNames(index, cycle));

            findings.add(new Finding(
                    getName(), node.id(), node.name(), node.type().name(),
                    cycle.size() > 3 ? Severity.HIGH : Severity.MEDIUM,
                    "Circular dependency detected: " + path,
                    node.filePath(), node.lineNumber()
            ));
        }

        return findings;
    }

    private List<String> findCycleFrom(KnowledgeGraphIndex index, String startId) {
        List<String> path = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        return dfs(index, startId, startId, path, visited) ? path : null;
    }

    private boolean dfs(KnowledgeGraphIndex index, String startId, String currentId, List<String> path, Set<String> visited) {
        path.add(currentId);
        visited.add(currentId);

        for (GraphNode neighbor : classDependsOnNeighbors(index, currentId)) {
            if (neighbor.id().equals(startId) && path.size() > 1) {
                path.add(neighbor.id());
                return true;
            }

            if (!visited.contains(neighbor.id()) && dfs(index, startId, neighbor.id(), path, visited)) {
                return true;
            }
        }

        path.remove(path.size() - 1);
        return false;
    }

    private List<GraphNode> classDependsOnNeighbors(KnowledgeGraphIndex index, String nodeId) {
        return index.outgoingNeighbors(nodeId, EdgeType.DEPENDS_ON).stream()
                .filter(this::isTypeNode)
                .toList();
    }

    private boolean isTypeNode(GraphNode node) {
        return node.type() == NodeType.CLASS || node.type() == NodeType.INTERFACE || node.type() == NodeType.ENUM;
    }

    private String normalizeCycleKey(List<String> cycle) {
        return new TreeSet<>(cycle).toString();
    }

    private List<String> cycleNames(KnowledgeGraphIndex index, List<String> cycle) {
        return cycle.stream()
                .map(id -> index.findNode(id).map(GraphNode::name).orElse(id))
                .toList();
    }
}
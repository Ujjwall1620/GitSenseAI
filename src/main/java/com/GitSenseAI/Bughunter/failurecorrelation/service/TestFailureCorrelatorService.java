package com.GitSenseAI.Bughunter.failurecorrelation.service;

import com.GitSenseAI.Bughunter.TEST.dto.TestFailureDetail;
import com.GitSenseAI.Bughunter.failurecorrelation.dto.CorrelatedFailure;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.model.GraphNode;
import com.GitSenseAI.Retriever.GRAPH.model.NodeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Matches a test failure's file/line back to the exact method in the
 * Knowledge Graph that likely caused it, so only that method's code —
 * not the full console output — needs to be sent for AI review.
 *
 * Matching is best-effort: GraphNode only stores a method's starting line,
 * not where it ends, so "enclosing method" is approximated as the closest
 * method declared at-or-before the failure line, within the same file.
 * This can occasionally attribute a failure to the wrong method if two
 * methods are declared close together — flagged rather than presented as
 * exact.
 */
@Slf4j
@Service
public class TestFailureCorrelatorService {

    public List<CorrelatedFailure> correlate(List<TestFailureDetail> failures, KnowledgeGraphIndex index) {
        List<CorrelatedFailure> results = new ArrayList<>();

        for (TestFailureDetail failure : failures) {
            results.add(correlateOne(failure, index));
        }

        log.info("Correlated {} of {} test failures to specific methods.",
                results.stream().filter(CorrelatedFailure::matched).count(), failures.size());

        return results;
    }

    private CorrelatedFailure correlateOne(TestFailureDetail failure, KnowledgeGraphIndex index) {
        if (failure.filePath() == null || failure.lineNumber() == null) {
            return unmatched(failure);
        }

        Optional<GraphNode> matchedMethod = findEnclosingMethod(index, failure.filePath(), failure.lineNumber());

        if (matchedMethod.isEmpty()) {
            return unmatched(failure);
        }

        GraphNode node = matchedMethod.get();

        return new CorrelatedFailure(
                failure.testName(), failure.exceptionType(), failure.message(),
                node.id(), node.name(), node.filePath(), node.lineNumber(),
                node.metadata().get("sourceCode"), true
        );
    }

    private Optional<GraphNode> findEnclosingMethod(KnowledgeGraphIndex index, String stackTraceFileName, int lineNumber) {
        return index.getGraph().getNodes().stream()
                .filter(node -> node.type() == NodeType.METHOD || node.type() == NodeType.CONSTRUCTOR)
                .filter(node -> node.filePath() != null && node.filePath().endsWith(stackTraceFileName))
                .filter(node -> node.lineNumber() <= lineNumber)
                .max(Comparator.comparingInt(GraphNode::lineNumber));
    }

    private CorrelatedFailure unmatched(TestFailureDetail failure) {
        return new CorrelatedFailure(
                failure.testName(), failure.exceptionType(), failure.message(),
                null, null, failure.filePath(), failure.lineNumber(), null, false
        );
    }
}
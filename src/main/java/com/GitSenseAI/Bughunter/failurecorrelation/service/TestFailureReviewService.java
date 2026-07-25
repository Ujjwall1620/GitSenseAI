package com.GitSenseAI.Bughunter.failurecorrelation.service;

import com.GitSenseAI.Bughunter.BugDetection.client.BugDetectionChatClient;
import com.GitSenseAI.Bughunter.BugDetection.dto.BugFinding;
import com.GitSenseAI.Bughunter.failurecorrelation.dto.CorrelatedFailure;
import com.GitSenseAI.Bughunter.failurecorrelation.dto.FailureAnalysisResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Sends each correlated test failure — exact exception, plus only the one
 * method that caused it — to the chat model for a targeted root-cause
 * review. This is the actual "console output reaches AI" boundary: never
 * raw logs, always this small, specific prompt.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestFailureReviewService {

    private final BugDetectionChatClient bugDetectionChatClient;

    public List<FailureAnalysisResult> reviewFailures(List<CorrelatedFailure> correlatedFailures) {
        List<FailureAnalysisResult> results = new ArrayList<>();

        for (CorrelatedFailure failure : correlatedFailures) {
            if (!failure.matched() || failure.methodSourceCode() == null) {
                log.warn("Could not match test failure '{}' to a specific method — skipping AI review for it.",
                        failure.testName());
                results.add(new FailureAnalysisResult(failure, List.of()));
                continue;
            }

            String context = buildFailureContext(failure);
            List<BugFinding> findings = bugDetectionChatClient.reviewMethod(context);

            results.add(new FailureAnalysisResult(failure, findings));
        }

        return results;
    }

    private String buildFailureContext(CorrelatedFailure failure) {
        return "A test named '" + failure.testName() + "' failed with "
                + failure.exceptionType() + ": " + failure.message() + "\n\n"
                + "The method believed to have caused this (" + failure.matchedMethodName() + "):\n"
                + failure.methodSourceCode() + "\n\n"
                + "Explain the likely root cause and how to fix it.";
    }
}
package com.GitSenseAI.Bughunter.Report.mapper;

import com.GitSenseAI.Bughunter.Report.Model.Severity;
import com.GitSenseAI.Bughunter.Report.dto.BugReportItem;
import com.GitSenseAI.Bughunter.failurecorrelation.dto.FailureAnalysisResult;
import org.springframework.stereotype.Component;

import java.util.List;

/** Converts test-failure-driven AI findings into the unified report item shape. */
@Component
public class FailureFindingMapper {

    public List<BugReportItem> toReportItems(FailureAnalysisResult result) {
        var failure = result.failure();

        if (!failure.matched()) {
            return List.of(new BugReportItem(
                    null, "TEST_FAILURE", "UnresolvedFailure", Severity.MEDIUM,
                    failure.testName(), "TEST",
                    failure.matchedFilePath(), failure.matchedLineNumber() != null ? failure.matchedLineNumber() : 0,
                    "Test '" + failure.testName() + "' failed with " + failure.exceptionType()
                            + ", but could not be traced to a specific method in the codebase.",
                    null
            ));
        }

        return result.aiFindings().stream()
                .map(finding -> new BugReportItem(
                        failure.matchedNodeId(), "TEST_FAILURE", finding.category(),
                        mapSeverity(finding.severity()),
                        failure.matchedMethodName(), "METHOD",
                        failure.matchedFilePath(), failure.matchedLineNumber(),
                        "Test '" + failure.testName() + "' failed (" + failure.exceptionType() + "). " + finding.description(),
                        finding.suggestedFix()
                ))
                .toList();
    }

    private Severity mapSeverity(com.GitSenseAI.Bughunter.staticanalysis.model.Severity severity) {
        return switch (severity) {
            case HIGH -> Severity.HIGH;
            case MEDIUM -> Severity.MEDIUM;
            case LOW -> Severity.LOW;
        };
    }
}
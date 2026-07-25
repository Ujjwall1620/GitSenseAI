package com.GitSenseAI.Bughunter.Report.mapper;

import com.GitSenseAI.Bughunter.BugDetection.dto.BugFinding;
import com.GitSenseAI.Bughunter.BugDetection.dto.MethodReviewResult;
import com.GitSenseAI.Bughunter.Report.Model.Severity;
import com.GitSenseAI.Bughunter.Report.dto.BugReportItem;
import org.springframework.stereotype.Component;

import java.util.List;

/** Converts AI-detected findings (per-method) into the unified report item shape. */
@Component
public class AiFindingMapper {

    public List<BugReportItem> toReportItems(MethodReviewResult methodReviewResult) {
        return methodReviewResult.findings().stream()
                .map(finding -> toReportItem(methodReviewResult, finding))
                .toList();
    }

    private BugReportItem toReportItem(MethodReviewResult methodReviewResult, BugFinding finding) {
        // AiFindingMapper.toReportItem — add methodReviewResult.nodeId() as first arg
        return new BugReportItem(
                methodReviewResult.nodeId(),
                "AI_REVIEW",
                finding.category(),
                mapSeverity(finding.severity()),
                methodReviewResult.methodName(),
                "METHOD",
                methodReviewResult.filePath(),
                methodReviewResult.lineNumber(),
                finding.description(),
                finding.suggestedFix()
        );
    }

    private Severity mapSeverity(com.GitSenseAI.Bughunter.staticanalysis.model.Severity severity) {
        return switch (severity) {
            case HIGH -> Severity.HIGH;
            case MEDIUM -> Severity.MEDIUM;
            case LOW -> Severity.LOW;
        };
    }
}
package com.GitSenseAI.Bughunter.Report.mapper;

import com.GitSenseAI.Bughunter.Report.Model.Severity;
import com.GitSenseAI.Bughunter.Report.dto.BugReportItem;
import com.GitSenseAI.Bughunter.staticanalysis.dto.Finding;
import org.springframework.stereotype.Component;

/** Converts a deterministic Static Analysis Finding into the unified report item shape. */
@Component
public class StaticFindingMapper {

    public BugReportItem toReportItem(Finding finding) {
        // StaticFindingMapper.toReportItem — add finding.nodeId() as first arg
        return new BugReportItem(
                finding.nodeId(),
                "STATIC_ANALYSIS",
                finding.ruleName(),
                mapSeverity(finding.severity()),
                finding.nodeName(),
                finding.nodeType(),
                finding.filePath(),
                finding.lineNumber(),
                finding.message(),
                null
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
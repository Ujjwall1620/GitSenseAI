package com.GitSenseAI.Bughunter.TEST.util;

import com.GitSenseAI.Bughunter.TEST.dto.TestFailureDetail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Best-effort text parser over raw build tool output (Maven Surefire-style,
 * Gradle, npm/jest, pytest). Different tools format output differently and
 * this is not a structured parser (e.g. no Surefire XML report parsing) —
 * it extracts a reasonable summary and failure list from stdout/stderr text,
 * not a guaranteed-precise breakdown. Good enough for a first-pass signal;
 * revisit with per-tool structured report parsing if precision matters later.
 */
@Component
public class TestOutputParser {

    private static final Pattern MAVEN_SUMMARY =
            Pattern.compile("Tests run: (\\d+), Failures: (\\d+), Errors: (\\d+), Skipped: (\\d+)");

    private static final Pattern FAILED_TEST_LINE =
            Pattern.compile("(?:FAIL(?:ED)?|✕)\\s*[:\\-]?\\s*(.+)");

    public ParsedSummary parse(List<String> outputLines) {
        int total = 0;
        int failed = 0;
        List<TestFailureDetail> failures = new ArrayList<>();

        for (String line : outputLines) {
            Matcher mavenMatcher = MAVEN_SUMMARY.matcher(line);
            if (mavenMatcher.find()) {
                total += Integer.parseInt(mavenMatcher.group(1));
                failed += Integer.parseInt(mavenMatcher.group(2)) + Integer.parseInt(mavenMatcher.group(3));
                continue;
            }

            Matcher failMatcher = FAILED_TEST_LINE.matcher(line);
            if (failMatcher.find()) {
                failures.add(new TestFailureDetail(failMatcher.group(1).trim(), line.trim()));
            }
        }

        return new ParsedSummary(total, Math.max(0, total - failed), failed, failures);
    }

    public record ParsedSummary(int total, int passed, int failed, List<TestFailureDetail> failures) {
    }
}
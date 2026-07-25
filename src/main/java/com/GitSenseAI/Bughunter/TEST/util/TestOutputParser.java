package com.GitSenseAI.Bughunter.TEST.util;

import com.GitSenseAI.Bughunter.TEST.dto.TestFailureDetail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Best-effort text parser over raw build tool output. Extracts a summary
 * count, plus — for each failure — the exception type and the first
 * project-owned stack frame (skipping JDK/framework internals), so
 * downstream AI review gets a precise pointer into the codebase instead
 * of the full noisy console output.
 */
@Component
public class TestOutputParser {

    private static final Pattern MAVEN_SUMMARY =
            Pattern.compile("Tests run: (\\d+), Failures: (\\d+), Errors: (\\d+), Skipped: (\\d+)");

    private static final Pattern FAILED_TEST_LINE =
            Pattern.compile("(?:FAIL(?:ED)?|✕)\\s*[:\\-]?\\s*(.+)");

    private static final Pattern EXCEPTION_LINE =
            Pattern.compile("^(?:Caused by:\\s*)?([\\w.$]+(?:Exception|Error))(?::\\s*(.*))?$");

    private static final Pattern STACK_FRAME =
            Pattern.compile("^\\s*at\\s+[\\w.$]+\\.\\w+\\(([\\w]+\\.java):(\\d+)\\)");

    private static final List<String> NOISE_PREFIXES = List.of(
            "java.", "javax.", "jdk.", "sun.", "org.junit", "org.apache.maven",
            "org.gradle", "org.testng", "kotlin.", "org.springframework.test"
    );

    public ParsedSummary parse(List<String> outputLines) {
        int total = 0;
        int failed = 0;
        List<TestFailureDetail> failures = new ArrayList<>();

        for (int i = 0; i < outputLines.size(); i++) {
            String line = outputLines.get(i);

            Matcher mavenMatcher = MAVEN_SUMMARY.matcher(line);
            if (mavenMatcher.find()) {
                total += Integer.parseInt(mavenMatcher.group(1));
                failed += Integer.parseInt(mavenMatcher.group(2)) + Integer.parseInt(mavenMatcher.group(3));
                continue;
            }

            Matcher failMatcher = FAILED_TEST_LINE.matcher(line);
            if (failMatcher.find()) {
                failures.add(extractFailureDetail(failMatcher.group(1).trim(), outputLines, i));
            }
        }

        return new ParsedSummary(total, Math.max(0, total - failed), failed, failures);
    }

    private TestFailureDetail extractFailureDetail(String testName, List<String> outputLines, int startIndex) {
        String exceptionType = null;
        String message = testName;
        String filePath = null;
        Integer lineNumber = null;

        int windowEnd = Math.min(outputLines.size(), startIndex + 30);

        for (int i = startIndex; i < windowEnd; i++) {
            String line = outputLines.get(i);

            if (exceptionType == null) {
                Matcher exceptionMatcher = EXCEPTION_LINE.matcher(line.trim());
                if (exceptionMatcher.find()) {
                    exceptionType = exceptionMatcher.group(1);
                    if (exceptionMatcher.group(2) != null) {
                        message = exceptionMatcher.group(2);
                    }
                }
            }

            if (filePath == null && isProjectFrame(line)) {
                Matcher frameMatcher = STACK_FRAME.matcher(line);
                if (frameMatcher.find()) {
                    filePath = frameMatcher.group(1);
                    lineNumber = Integer.parseInt(frameMatcher.group(2));
                }
            }

            if (exceptionType != null && filePath != null) {
                break;
            }
        }

        return new TestFailureDetail(testName, message, exceptionType, filePath, lineNumber);
    }

    private boolean isProjectFrame(String line) {
        String trimmed = line.trim();
        if (!trimmed.startsWith("at ")) {
            return false;
        }

        String fullyQualifiedCall = trimmed.substring(3);
        return NOISE_PREFIXES.stream().noneMatch(fullyQualifiedCall::startsWith);
    }

    public record ParsedSummary(int total, int passed, int failed, List<TestFailureDetail> failures) {
    }
}
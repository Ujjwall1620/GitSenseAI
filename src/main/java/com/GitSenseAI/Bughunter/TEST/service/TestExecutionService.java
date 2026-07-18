package com.GitSenseAI.Bughunter.TEST.service;

import com.GitSenseAI.Retriever.REPOSITORY.Entity.enums.BuildTools;
import com.GitSenseAI.Bughunter.TEST.config.TestExecutionProperties;
import com.GitSenseAI.Bughunter.TEST.dto.TestExecutionResult;
import com.GitSenseAI.Bughunter.TEST.enums.TestExecutionStatus;
import com.GitSenseAI.Bughunter.TEST.util.BuildToolCommandResolver;
import com.GitSenseAI.Bughunter.TEST.util.CommandAvailabilityChecker;
import com.GitSenseAI.Bughunter.TEST.util.TestOutputParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestExecutionService {

    private final CommandAvailabilityChecker commandAvailabilityChecker;
    private final BuildToolCommandResolver buildToolCommandResolver;
    private final TestOutputParser testOutputParser;
    private final TestExecutionProperties testExecutionProperties;

    public TestExecutionResult runTests(String workspacePath, BuildTools buildTool) {
        Path workspace = Path.of(workspacePath);

        if (buildTool == null || buildTool == BuildTools.UNKNOWN) {
            log.warn("No recognized build tool for workspace [{}]. Skipping test execution.", workspacePath);
            return TestExecutionResult.skipped("No recognized build tool — cannot determine test command");
        }

        List<String> command = buildToolCommandResolver.resolveCommand(buildTool, workspace);
        String primaryCommand = buildToolCommandResolver.primaryCommandName(buildTool, workspace);

        if (!commandAvailabilityChecker.isAvailable(primaryCommand, workspace)) {
            log.warn("Required tool '{}' not available for workspace [{}]. Skipping test execution.", primaryCommand, workspacePath);
            return TestExecutionResult.skipped("Required tool not available: " + primaryCommand);
        }

        try {
            return executeCommand(command, workspace);
        } catch (Exception ex) {
            log.error("Unexpected failure running tests for workspace [{}]", workspacePath, ex);
            return TestExecutionResult.error("Test execution failed unexpectedly: " + ex.getMessage());
        }
    }

    private TestExecutionResult executeCommand(List<String> command, Path workspace) throws IOException, InterruptedException {
        log.info("Running test command {} in workspace [{}]...", command, workspace);

        Process process = new ProcessBuilder(command)
                .directory(workspace.toFile())
                .redirectErrorStream(true)
                .start();

        List<String> outputLines = readOutput(process);

        boolean finished = process.waitFor(testExecutionProperties.getTimeoutSeconds(), TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly();
            log.warn("Test execution timed out after {}s for workspace [{}]", testExecutionProperties.getTimeoutSeconds(), workspace);
            return TestExecutionResult.error("Test execution timed out after " + testExecutionProperties.getTimeoutSeconds() + " seconds");
        }

        TestOutputParser.ParsedSummary summary = testOutputParser.parse(outputLines);
        int exitCode = process.exitValue();

        TestExecutionStatus status = determineStatus(exitCode, summary);
        List<String> outputTail = tail(outputLines, testExecutionProperties.getMaxOutputLines());

        log.info("Test execution completed with status {} ({} total, {} failed).", status, summary.total(), summary.failed());

        return new TestExecutionResult(
                status,
                buildSummaryText(status, summary, exitCode),
                summary.total(),
                summary.passed(),
                summary.failed(),
                summary.failures(),
                outputTail
        );
    }

    private List<String> readOutput(Process process) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    private TestExecutionStatus determineStatus(int exitCode, TestOutputParser.ParsedSummary summary) {
        if (exitCode == 0 && summary.failed() == 0) {
            return TestExecutionStatus.PASSED;
        }

        return TestExecutionStatus.FAILED;
    }

    private String buildSummaryText(TestExecutionStatus status, TestOutputParser.ParsedSummary summary, int exitCode) {
        return switch (status) {
            case PASSED -> "All " + summary.total() + " tests passed.";
            case FAILED -> summary.total() > 0
                    ? summary.failed() + " of " + summary.total() + " tests failed."
                    : "Build or test execution failed (exit code " + exitCode + ") before any tests could run.";
            default -> "Unexpected status.";
        };
    }

    private List<String> tail(List<String> lines, int maxLines) {
        if (lines.size() <= maxLines) {
            return lines;
        }

        return lines.subList(lines.size() - maxLines, lines.size());
    }
}
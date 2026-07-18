package com.GitSenseAI.Bughunter.TEST.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CommandAvailabilityChecker {

    public boolean isAvailable(String command, Path workspacePath) {
        if (command == null || command.isBlank()) {
            return false;
        }

        if (isPathShaped(command)) {
            return Files.exists(workspacePath.resolve(command));
        }

        return isOnSystemPath(command);
    }

    private boolean isPathShaped(String command) {
        return command.contains("/") || command.contains("\\");
    }

    private boolean isOnSystemPath(String command) {
        try {
            String checkerCommand = isWindows() ? "where" : "which";

            Process process = new ProcessBuilder(checkerCommand, command)
                    .redirectErrorStream(true)
                    .start();

            boolean finished = process.waitFor(5, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                return false;
            }

            return process.exitValue() == 0;
        } catch (Exception ex) {
            log.warn("Could not determine availability of command '{}': {}", command, ex.getMessage());
            return false;
        }
    }

    private boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }
}
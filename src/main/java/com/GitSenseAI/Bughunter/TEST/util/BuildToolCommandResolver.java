package com.GitSenseAI.Bughunter.TEST.util;

import com.GitSenseAI.Retriever.REPOSITORY.Entity.enums.BuildTools;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Maps a detected BuildTool to the actual executable and arguments needed
 * to run its test suite. For Ruby and PHP, the exact command depends on
 * which convention the repo actually uses — resolved by checking for
 * specific config files, not assumed.
 */
@Component
public class BuildToolCommandResolver {

    public List<String> resolveCommand(BuildTools buildTool, Path workspacePath) {
        boolean isWindows = isWindows();

        return switch (buildTool) {
            case MAVEN -> List.of(isWindows ? "mvn.cmd" : "mvn", "test");
            case GRADLE -> List.of(isWindows ? "gradlew.bat" : "./gradlew", "test");
            case NPM -> List.of(isWindows ? "npm.cmd" : "npm", "test");
            case YARN -> List.of(isWindows ? "yarn.cmd" : "yarn", "test");
            case PIP -> List.of("pytest");
            case GO -> List.of("go", "test", "./...");
            case DOTNET -> List.of("dotnet", "test");
            case BUNDLER -> resolveRubyCommand(workspacePath);
            case PHPUNIT -> resolvePhpCommand(workspacePath, isWindows);
            case CMAKE -> List.of("ctest");
            case MAKE -> List.of("make", "test");
            case UNKNOWN -> List.of();
        };
    }

    public String primaryCommandName(BuildTools buildTool, Path workspacePath) {
        List<String> resolved = resolveCommand(buildTool, workspacePath);
        return resolved.isEmpty() ? null : resolved.get(0);
    }

    /** rspec is the dominant Ruby test convention when configured; Rake is the fallback default. */
    private List<String> resolveRubyCommand(Path workspacePath) {
        if (Files.exists(workspacePath.resolve(".rspec"))) {
            return List.of("bundle", "exec", "rspec");
        }

        return List.of("bundle", "exec", "rake", "test");
    }

    /** Direct phpunit.xml means PHPUnit is configured directly; otherwise fall back to composer's test script. */
    private List<String> resolvePhpCommand(Path workspacePath, boolean isWindows) {
        boolean hasPhpUnitConfig = Files.exists(workspacePath.resolve("phpunit.xml"))
                || Files.exists(workspacePath.resolve("phpunit.xml.dist"));

        if (hasPhpUnitConfig) {
            return List.of(isWindows ? "vendor\\bin\\phpunit.bat" : "vendor/bin/phpunit");
        }

        return List.of(isWindows ? "composer.bat" : "composer", "test");
    }

    private boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }
}
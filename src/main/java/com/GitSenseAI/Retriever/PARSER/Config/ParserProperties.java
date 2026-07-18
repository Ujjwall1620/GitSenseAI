package com.GitSenseAI.Retriever.PARSER.Config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "parser")
public class ParserProperties {

    private long maxFileSizeBytes = 2_000_000L;

    private int maxFiles = 5000;

    private Set<String> ignoredDirectories = new HashSet<>(Set.of(
            ".git", "target", "build", "node_modules", "dist", ".idea", ".gradle", "out", "vendor"
    ));

    private Set<String> supportedExtensions = new HashSet<>(Set.of(
            "java", "kt", "kts", "py", "js", "jsx", "ts", "tsx",
            "go", "c", "h", "cpp", "cc", "cxx", "hpp", "hh", "cs", "rb", "php"
    ));
}
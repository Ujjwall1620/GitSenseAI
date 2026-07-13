package com.GitSenseAI.REPOSITORY.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "repository")
    public class RepositoryProperties {

        private String workspaceBaseDir = System.getProperty("java.io.tmpdir") + "/GitSenseAI-workspaces";

        private String githubApiBaseUrl = "https://api.github.com";

        private String githubToken;

        private int cloneDepth = 1;

        private long maxRepositorySizeKb = 512_000L;
    }

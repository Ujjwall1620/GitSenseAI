package com.GitSenseAI.Retriever.REPOSITORY.Util;

import com.GitSenseAI.Retriever.REPOSITORY.exception.InvalidRepositoryURLException;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GitHubUrlParser {

    private static final Pattern GITHUB_URL_PATTERN = Pattern.compile(
            "^https://github\\.com/(?<owner>[A-Za-z0-9_.-]+)/(?<repo>[A-Za-z0-9_.-]+?)(\\.git)?/?$"
    );

    public String[] parse(String repositoryUrl) {
        if (repositoryUrl == null || repositoryUrl.isBlank()) {
            throw new InvalidRepositoryURLException("Repository URL must not be null or blank");
        }

        Matcher matcher = GITHUB_URL_PATTERN.matcher(repositoryUrl.trim());

        if (!matcher.matches()) {
            throw new InvalidRepositoryURLException("Repository URL is not a valid GitHub URL: " + repositoryUrl);
        }

        String owner = matcher.group("owner");
        String repo = matcher.group("repo");

        return new String[] { owner, repo };
    }
}
package com.GitSenseAI.Retriever.REPOSITORY.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient githubRestClient(RepositoryProperties properties) {

        return RestClient.builder()
                .baseUrl(properties.getGithubApiBaseUrl())
                .defaultHeader("Accept", "application/vnd.github+json")
                .build();
    }
}
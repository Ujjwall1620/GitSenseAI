package com.GitSenseAI.Retriever.RAG.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.client.ChatClient;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "rag")
public class RagProperties {

    private int defaultTopK = 5;

    private String systemPromptTemplate =
            "You are a code analysis assistant for a software repository. " +
                    "Answer questions using ONLY the provided context from the codebase. " +
                    "If the context doesn't contain enough information to answer confidently, " +
                    "say so clearly rather than guessing or inventing details.";

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.build();
    }
}
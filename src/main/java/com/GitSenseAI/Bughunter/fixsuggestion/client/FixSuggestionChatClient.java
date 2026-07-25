package com.GitSenseAI.Bughunter.fixsuggestion.client;

import com.GitSenseAI.Bughunter.fixsuggestion.config.FixSuggestionProperties;
import com.GitSenseAI.Bughunter.fixsuggestion.dto.AiFixResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Reuses the shared ChatClient bean (defined once in RagProperties) rather
 * than declaring its own — a second @Bean of the same type would conflict
 * with Spring's bean resolution at startup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FixSuggestionChatClient {

    private final ChatClient chatClient;
    private final FixSuggestionProperties fixSuggestionProperties;

    public AiFixResponse generateFix(String originalCode, String bugDescription) {
        try {
            String userPrompt = "Bug description:\n" + bugDescription + "\n\nOriginal method:\n" + originalCode;

            return chatClient.prompt()
                    .system(fixSuggestionProperties.getSystemPrompt())
                    .user(userPrompt)
                    .call()
                    .entity(AiFixResponse.class);
        } catch (Exception ex) {
            log.warn("Fix generation failed for a method, skipping: {}", ex.getMessage());
            return null;
        }
    }
}
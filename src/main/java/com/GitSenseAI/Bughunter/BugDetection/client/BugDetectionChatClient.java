package com.GitSenseAI.Bughunter.BugDetection.client;

import com.GitSenseAI.Bughunter.BugDetection.config.BugDetectionProperties;
import com.GitSenseAI.Bughunter.BugDetection.dto.BugFinding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Thin wrapper around the shared ChatClient bean for structured bug-review
 * calls. One method's review failing (timeout, malformed response) never
 * throws past this boundary as an unhandled crash into the batch loop —
 * callers get an empty list and a logged warning instead, so one bad
 * response can't abort review of the rest of the codebase.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BugDetectionChatClient {

    private final ChatClient chatClient;
    private final BugDetectionProperties bugDetectionProperties;

    public List<BugFinding> reviewMethod(String context) {
        try {
            List<BugFinding> findings = chatClient.prompt()
                    .system(bugDetectionProperties.getSystemPrompt())
                    .user(context)
                    .call()
                    .entity(new ParameterizedTypeReference<List<BugFinding>>() {});

            return findings != null ? findings : List.of();
        } catch (Exception ex) {
            log.warn("Chat model review failed for a method, skipping: {}", ex.getMessage());
            return List.of();
        }
    }
}
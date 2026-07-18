package com.GitSenseAI.Retriever.EMBEDDING.cinfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * No EmbeddingModel bean is defined in this module — Spring AI auto-configures
 * it based on whichever provider starter is on the classpath (Ollama, OpenAI,
 * Vertex AI Gemini, Azure OpenAI, etc.) plus the matching spring.ai.<provider>.*
 * properties. This is what keeps the module provider-independent: switching
 * providers is a dependency + properties change only, with zero code changes here.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "embedding")
public class EmbeddingProperties {

    /** Maximum number of texts sent to the embedding model in a single batch call. */
    private int maxBatchSize = 50;
}
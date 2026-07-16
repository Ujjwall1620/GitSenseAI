package com.GitSenseAI.VECTOR.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides a working VectorStore out of the box (in-memory, no external
 * database required) so the app runs immediately. If a provider-specific
 * starter (e.g. spring-ai-starter-vector-store-mariadb) is later added
 * along with a real datasource, Spring Boot's own auto-configured
 * VectorStore bean takes priority automatically and this fallback backs off.
 */
@Configuration
public class VectorStoreConfig {

    @Bean
    @ConditionalOnMissingBean(VectorStore.class)
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
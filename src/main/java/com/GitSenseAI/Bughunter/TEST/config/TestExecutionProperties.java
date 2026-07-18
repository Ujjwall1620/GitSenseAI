package com.GitSenseAI.Bughunter.TEST.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "test-execution")
public class TestExecutionProperties {

    /** Hard timeout for a single test run — prevents a hung/looping test from blocking the app indefinitely. */
    private int timeoutSeconds = 300;

    /** Cap on how many lines of raw process output we retain in memory/response. */
    private int maxOutputLines = 200;
}
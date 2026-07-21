package com.GitSenseAI.Bughunter.BugDetection.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "bug-detection")
public class BugDetectionProperties {

    /** Hard cap on methods reviewed per request — LLM review is the most expensive step in the whole pipeline. */
    private int maxMethodsPerRun = 50;

    private boolean skipDeadCode = true;

    private String systemPrompt =
            "You are a meticulous code reviewer. Given a Java method's source code and its call "
                    + "context (callers, callees), identify concrete defects only — null pointer risks, resource "
                    + "leaks, off-by-one errors, incorrect boolean logic, unhandled exceptions, thread-safety issues. "
                    + "Do not report style preferences or purely subjective suggestions. If you find nothing concrete, "
                    + "return an empty list. Respond ONLY with a JSON array matching the requested schema.";
}
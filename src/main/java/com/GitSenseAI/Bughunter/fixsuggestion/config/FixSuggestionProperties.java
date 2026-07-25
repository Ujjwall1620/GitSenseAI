package com.GitSenseAI.Bughunter.fixsuggestion.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "fix-suggestion")
public class FixSuggestionProperties {

    private int maxFixesPerRun = 20;

    private String systemPrompt =
            "You are a senior software engineer fixing a specific, already-identified bug. "
                    + "Given the original method's source code and a description of the bug, produce a corrected "
                    + "version of the ENTIRE method — not a snippet or diff syntax — plus a short explanation of "
                    + "what changed and why. Preserve the method's original signature and formatting style. "
                    + "If you cannot confidently fix it, return an empty fixedCode field rather than guessing. "
                    + "Respond ONLY with JSON matching the requested schema.";
}
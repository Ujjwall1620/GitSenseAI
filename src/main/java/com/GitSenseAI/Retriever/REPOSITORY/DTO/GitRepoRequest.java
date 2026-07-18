package com.GitSenseAI.Retriever.REPOSITORY.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GitRepoRequest(@NotBlank(message = "Repository URL must not be blank")
                             @Pattern(
                                     regexp = "^https://github\\.com/[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+(\\.git)?/?$",
                                     message = "Repository URL must be a valid GitHub repository URL"
                             )
                             String repositoryUrl) {
}

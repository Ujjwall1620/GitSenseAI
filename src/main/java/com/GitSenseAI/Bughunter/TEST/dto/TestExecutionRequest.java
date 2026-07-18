package com.GitSenseAI.Bughunter.TEST.dto;

import com.GitSenseAI.Retriever.REPOSITORY.Entity.enums.BuildTools;

public record TestExecutionRequest(
        String workspacePath,
        BuildTools buildTool
) {
}
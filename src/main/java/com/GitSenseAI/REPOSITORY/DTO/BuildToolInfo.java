package com.GitSenseAI.REPOSITORY.DTO;


import com.GitSenseAI.REPOSITORY.Entity.enums.BuildTools;

public record BuildToolInfo(
        BuildTools buildTool,
        String buildFilePath
) {
}
package com.GitSenseAI.Retriever.REPOSITORY.DTO;


import com.GitSenseAI.Retriever.REPOSITORY.Entity.enums.BuildTools;

public record BuildToolInfo(
        BuildTools buildTool,
        String buildFilePath
) {
}
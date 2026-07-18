package com.GitSenseAI.Retriever.PARSER.Model;

public record ParsedMethodCall(
        String calledMethodName,
        String calledOnType,
        int lineNumber
) {
}
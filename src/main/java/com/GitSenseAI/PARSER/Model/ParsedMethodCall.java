package com.GitSenseAI.PARSER.Model;

public record ParsedMethodCall(
        String calledMethodName,
        String calledOnType,
        int lineNumber
) {
}
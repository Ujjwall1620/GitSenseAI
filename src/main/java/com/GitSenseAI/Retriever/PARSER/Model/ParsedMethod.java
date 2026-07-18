package com.GitSenseAI.Retriever.PARSER.Model;

import java.util.List;

public record ParsedMethod(
        String name,
        String returnType,
        List<String> parameterTypes,
        List<ParsedMethodCall> methodCalls,
        List<String> annotations,
        boolean isConstructor,
        int lineNumber
) {
}
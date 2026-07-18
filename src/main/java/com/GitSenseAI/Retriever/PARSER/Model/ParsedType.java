package com.GitSenseAI.Retriever.PARSER.Model;


import java.util.List;

public record ParsedType(
        String name,
        NodeType nodeType,
        String packageName,
        List<String> extendedTypes,
        List<String> implementedInterfaces,
        List<ParsedField> fields,
        List<ParsedMethod> methods,
        List<String> annotations,
        int lineNumber
) {
}
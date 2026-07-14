package com.GitSenseAI.knowledgegraph.builder;

import com.GitSenseAI.PARSER.DTO.ParseResult;
import com.GitSenseAI.PARSER.Model.ParsedField;
import com.GitSenseAI.PARSER.Model.ParsedMethod;
import com.GitSenseAI.PARSER.Model.ParsedType;
import com.GitSenseAI.knowledgegraph.model.GraphNode;
import com.GitSenseAI.knowledgegraph.model.NodeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/** Converts Parser Module extraction results into GraphNode instances. No edge logic here. */
@Component
@RequiredArgsConstructor
public class NodeFactory {

    private final GraphIdBuilder graphIdBuilder;

    public GraphNode packageNode(String packageName, String filePath) {
        String id = graphIdBuilder.packageId(packageName);
        return new GraphNode(id, packageName, packageName, NodeType.PACKAGE, filePath, 0, Map.of());
    }

    public GraphNode typeNode(ParseResult parseResult, ParsedType parsedType) {
        String id = graphIdBuilder.typeId(parseResult.packageName(), parsedType.name());
        String fqn = graphIdBuilder.fullyQualifiedName(parseResult.packageName(), parsedType.name());

        NodeType nodeType = switch (parsedType.nodeType()) {
            case INTERFACE -> NodeType.INTERFACE;
            case ENUM -> NodeType.ENUM;
            default -> NodeType.CLASS;
        };

        return new GraphNode(id, parsedType.name(), fqn, nodeType, parseResult.filePath(), parsedType.lineNumber(), Map.of());
    }

    public GraphNode fieldNode(ParseResult parseResult, ParsedType parsedType, ParsedField field) {
        String id = graphIdBuilder.fieldId(parseResult.packageName(), parsedType.name(), field.name());
        String fqn = graphIdBuilder.fullyQualifiedName(parseResult.packageName(), parsedType.name()) + "#" + field.name();

        Map<String, String> metadata = new HashMap<>();
        metadata.put("fieldType", field.type());

        return new GraphNode(id, field.name(), fqn, NodeType.FIELD, parseResult.filePath(), field.lineNumber(), metadata);
    }

    public GraphNode methodNode(ParseResult parseResult, ParsedType parsedType, ParsedMethod method) {
        String id = graphIdBuilder.methodId(parseResult.packageName(), parsedType.name(), method);
        String fqn = graphIdBuilder.methodFullyQualifiedName(parseResult.packageName(), parsedType.name(), method);

        NodeType nodeType = method.isConstructor() ? NodeType.CONSTRUCTOR : NodeType.METHOD;

        Map<String, String> metadata = new HashMap<>();
        metadata.put("returnType", method.returnType());
        metadata.put("parameterTypes", String.join(",", method.parameterTypes()));

        return new GraphNode(id, method.name(), fqn, nodeType, parseResult.filePath(), method.lineNumber(), metadata);
    }

    public GraphNode annotationNode(String annotationName) {
        String id = graphIdBuilder.annotationId(annotationName);
        return new GraphNode(id, annotationName, annotationName, NodeType.ANNOTATION, null, 0, Map.of());
    }

    /** Placeholder for a type reference that could not be resolved to a node declared in the repository. */
    public GraphNode externalTypeNode(String typeName) {
        String id = graphIdBuilder.externalTypeId(typeName);
        return new GraphNode(id, typeName, typeName, NodeType.CLASS, null, 0, Map.of("resolved", "false"));
    }

    /** Placeholder for a method call that could not be confidently resolved (no match, or ambiguous simple name). */
    public GraphNode unresolvedMethodNode(String methodName) {
        String id = graphIdBuilder.unresolvedMethodId(methodName);
        return new GraphNode(id, methodName, methodName, NodeType.METHOD, null, 0, Map.of("resolved", "false"));
    }
}
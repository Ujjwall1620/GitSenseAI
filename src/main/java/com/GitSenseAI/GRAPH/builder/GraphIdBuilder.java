package com.GitSenseAI.GRAPH.builder;

import com.GitSenseAI.PARSER.Model.ParsedMethod;
import org.springframework.stereotype.Component;

/** Generates stable, deterministic identifiers for GraphNode instances. */
@Component
public class GraphIdBuilder {

    public String packageId(String packageName) {
        return "package:" + packageName;
    }

    public String typeId(String packageName, String typeName) {
        return "type:" + fullyQualifiedName(packageName, typeName);
    }

    public String fieldId(String packageName, String typeName, String fieldName) {
        return typeId(packageName, typeName) + "#field:" + fieldName;
    }

    public String methodId(String packageName, String typeName, ParsedMethod method) {
        String signature = method.name() + "(" + String.join(",", method.parameterTypes()) + ")";
        return typeId(packageName, typeName) + "#method:" + signature;
    }

    public String annotationId(String annotationName) {
        return "annotation:" + annotationName;
    }

    public String externalTypeId(String typeName) {
        return "external-type:" + typeName;
    }

    public String unresolvedMethodId(String methodName) {
        return "unresolved-method:" + methodName;
    }

    public String fullyQualifiedName(String packageName, String simpleName) {
        return (packageName == null || packageName.isBlank())
                ? simpleName
                : packageName + "." + simpleName;
    }

    public String methodFullyQualifiedName(String packageName, String typeName, ParsedMethod method) {
        String signature = method.name() + "(" + String.join(",", method.parameterTypes()) + ")";
        return fullyQualifiedName(packageName, typeName) + "#" + signature;
    }
}
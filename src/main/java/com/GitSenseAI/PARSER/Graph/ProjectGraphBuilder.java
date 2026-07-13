package com.GitSenseAI.PARSER.Graph;



import com.GitSenseAI.PARSER.DTO.ParseResult;
import com.GitSenseAI.PARSER.Exception.GraphBuildException;
import com.GitSenseAI.PARSER.Model.*;
import com.GitSenseAI.PARSER.Symbol.SymbolTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProjectGraphBuilder {

    public ProjectGraph build(List<ParseResult> parseResults, SymbolTable symbolTable) {
        log.info("Building project graph...");

        ProjectGraph graph = new ProjectGraph();

        for (ParseResult parseResult : parseResults) {
            try {
                String packageNodeId = buildPackageNode(graph, parseResult);

                for (ParsedType type : parseResult.types()) {
                    addTypeToGraph(graph, parseResult, type, packageNodeId, symbolTable);
                }
            } catch (Exception ex) {
                log.error("Failed to add file to project graph: {}", parseResult.filePath(), ex);
                throw new GraphBuildException("Failed to build graph for file: " + parseResult.filePath(), ex);
            }
        }

        log.info("Project graph built with {} nodes and {} edges.", graph.getNodes().size(), graph.getEdges().size());

        return graph;
    }

    private String buildPackageNode(ProjectGraph graph, ParseResult parseResult) {
        String packageName = parseResult.packageName() == null || parseResult.packageName().isBlank()
                ? "default"
                : parseResult.packageName();

        String packageNodeId = "package:" + packageName;

        graph.addNode(new GraphNode(packageNodeId, parseResult.filePath(), 0, packageName, NodeType.PACKAGE));

        for (String importName : parseResult.imports()) {
            graph.addEdge(new GraphEdge(newEdgeId(), packageNodeId, "type:" + importName, EdgeType.IMPORTS));
        }

        return packageNodeId;
    }

    private void addTypeToGraph(ProjectGraph graph, ParseResult parseResult, ParsedType type,
                                String packageNodeId, SymbolTable symbolTable) {
        String typeNodeId = "type:" + fullyQualifiedName(type);

        graph.addNode(new GraphNode(typeNodeId, parseResult.filePath(), type.lineNumber(), type.name(), type.nodeType()));
        graph.addEdge(new GraphEdge(newEdgeId(), packageNodeId, typeNodeId, EdgeType.CONTAINS));

        for (String extendedType : type.extendedTypes()) {
            graph.addEdge(new GraphEdge(newEdgeId(), typeNodeId, "type:" + extendedType, EdgeType.EXTENDS));
        }

        for (String implementedInterface : type.implementedInterfaces()) {
            graph.addEdge(new GraphEdge(newEdgeId(), typeNodeId, "type:" + implementedInterface, EdgeType.IMPLEMENTS));
        }

        for (String annotation : type.annotations()) {
            graph.addEdge(new GraphEdge(newEdgeId(), typeNodeId, "annotation:" + annotation, EdgeType.ANNOTATED_WITH));
        }

        for (ParsedField field : type.fields()) {
            addFieldToGraph(graph, parseResult, typeNodeId, field);
        }

        for (ParsedMethod method : type.methods()) {
            addMethodToGraph(graph, parseResult, typeNodeId, type, method, symbolTable);
        }
    }

    private void addFieldToGraph(ProjectGraph graph, ParseResult parseResult, String typeNodeId, ParsedField field) {
        String fieldNodeId = typeNodeId + "#field:" + field.name();

        graph.addNode(new GraphNode(fieldNodeId, parseResult.filePath(), field.lineNumber(), field.name(), NodeType.FIELD));
        graph.addEdge(new GraphEdge(newEdgeId(), typeNodeId, fieldNodeId, EdgeType.CONTAINS));
        graph.addEdge(new GraphEdge(newEdgeId(), fieldNodeId, "type:" + field.type(), EdgeType.DEPENDS_ON));

        for (String annotation : field.annotations()) {
            graph.addEdge(new GraphEdge(newEdgeId(), fieldNodeId, "annotation:" + annotation, EdgeType.ANNOTATED_WITH));
        }
    }

    private void addMethodToGraph(ProjectGraph graph, ParseResult parseResult, String typeNodeId,
                                  ParsedType type, ParsedMethod method, SymbolTable symbolTable) {
        String methodNodeId = typeNodeId + "#method:" + method.name();

        graph.addNode(new GraphNode(methodNodeId, parseResult.filePath(), method.lineNumber(), method.name(), NodeType.METHOD));
        graph.addEdge(new GraphEdge(newEdgeId(), typeNodeId, methodNodeId, EdgeType.CONTAINS));

        if (!method.isConstructor()) {
            graph.addEdge(new GraphEdge(newEdgeId(), methodNodeId, "type:" + method.returnType(), EdgeType.RETURNS));
        }

        for (String annotation : method.annotations()) {
            graph.addEdge(new GraphEdge(newEdgeId(), methodNodeId, "annotation:" + annotation, EdgeType.ANNOTATED_WITH));
        }

        for (ParsedMethodCall methodCall : method.methodCalls()) {
            String targetId = symbolTable.lookup(methodCall.calledMethodName())
                    .map(symbol -> "type:" + symbol.filePath() + "#method:" + symbol.name())
                    .orElse("unresolved:" + methodCall.calledMethodName());

            graph.addEdge(new GraphEdge(newEdgeId(), methodNodeId, targetId, EdgeType.CALLS));
        }
    }

    private String fullyQualifiedName(ParsedType type) {
        return (type.packageName() == null || type.packageName().isBlank())
                ? type.name()
                : type.packageName() + "." + type.name();
    }

    private String newEdgeId() {
        return UUID.randomUUID().toString();
    }
}
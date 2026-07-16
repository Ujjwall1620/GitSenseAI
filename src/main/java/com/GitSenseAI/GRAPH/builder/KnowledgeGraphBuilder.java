package com.GitSenseAI.GRAPH.builder;

import com.GitSenseAI.GRAPH.exception.NullParserOutputException;
import com.GitSenseAI.GRAPH.model.EdgeType;
import com.GitSenseAI.GRAPH.model.GraphEdge;
import com.GitSenseAI.GRAPH.model.GraphNode;
import com.GitSenseAI.GRAPH.model.NodeType;
import com.GitSenseAI.GRAPH.model.ProjectGraph;
import com.GitSenseAI.GRAPH.symbol.ResolvedSymbol;
import com.GitSenseAI.GRAPH.symbol.SymbolIndex;
import com.GitSenseAI.PARSER.DTO.ParseResult;
import com.GitSenseAI.PARSER.Model.ParsedField;
import com.GitSenseAI.PARSER.Model.ParsedMethod;
import com.GitSenseAI.PARSER.Model.ParsedMethodCall;
import com.GitSenseAI.PARSER.Model.ParsedType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Builds the Knowledge Graph from Parser Module output in two passes.
 *
 * Pass 1 (buildSymbolIndex): registers every declared class, interface,
 * enum, method, and constructor across the entire repository, keyed by
 * fully-qualified name, before any edges exist.
 *
 * Pass 2 (processFile...): walks the same data again and creates nodes and
 * edges, resolving references against the now-complete SymbolIndex. This
 * is what fixes cross-file resolution and simple-name collisions that the
 * previous single-pass implementation had.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeGraphBuilder {

    private final NodeFactory nodeFactory;
    private final GraphIdBuilder graphIdBuilder;

    public ProjectGraph build(List<ParseResult> parseResults) {
        if (parseResults == null) {
            throw new NullParserOutputException("Parser Module output must not be null");
        }

        log.info("Pass 1: building symbol index from {} parsed files...", parseResults.size());
        SymbolIndex symbolIndex = buildSymbolIndex(parseResults);

        log.info("Pass 2: building knowledge graph nodes and edges...");
        ProjectGraph graph = new ProjectGraph();
        Set<String> seenNodeIds = new HashSet<>();
        Set<String> seenEdgeKeys = new HashSet<>();

        for (ParseResult parseResult : parseResults) {
            processFile(graph, seenNodeIds, seenEdgeKeys, parseResult, symbolIndex);
        }

        log.info("Knowledge graph built with {} nodes and {} edges.", graph.getNodes().size(), graph.getEdges().size());

        return graph;
    }

    // ---------- Pass 1: symbol registration ----------

    private SymbolIndex buildSymbolIndex(List<ParseResult> parseResults) {
        SymbolIndex symbolIndex = new SymbolIndex();

        for (ParseResult parseResult : parseResults) {
            if (parseResult.types() == null) {
                continue;
            }

            for (ParsedType type : parseResult.types()) {
                registerType(symbolIndex, parseResult, type);
            }
        }

        return symbolIndex;
    }

    private void registerType(SymbolIndex symbolIndex, ParseResult parseResult, ParsedType type) {
        String typeId = graphIdBuilder.typeId(parseResult.packageName(), type.name());
        String typeFqn = graphIdBuilder.fullyQualifiedName(parseResult.packageName(), type.name());
        NodeType nodeType = mapNodeType(type.nodeType());

        symbolIndex.register(new ResolvedSymbol(typeId, typeFqn, type.name(), nodeType, parseResult.filePath(), type.lineNumber()));

        for (ParsedMethod method : type.methods()) {
            String methodId = graphIdBuilder.methodId(parseResult.packageName(), type.name(), method);
            String methodFqn = graphIdBuilder.methodFullyQualifiedName(parseResult.packageName(), type.name(), method);
            NodeType methodNodeType = method.isConstructor() ? NodeType.CONSTRUCTOR : NodeType.METHOD;

            symbolIndex.register(new ResolvedSymbol(methodId, methodFqn, method.name(), methodNodeType, parseResult.filePath(), method.lineNumber()));
        }
    }

    private NodeType mapNodeType(com.GitSenseAI.PARSER.Model.NodeType parserNodeType) {
        return switch (parserNodeType) {
            case INTERFACE -> NodeType.INTERFACE;
            case ENUM -> NodeType.ENUM;
            default -> NodeType.CLASS;
        };
    }

    // ---------- Pass 2: node and edge construction ----------

    private void processFile(ProjectGraph graph, Set<String> seenNodeIds, Set<String> seenEdgeKeys,
                             ParseResult parseResult, SymbolIndex symbolIndex) {
        GraphNode packageNode = nodeFactory.packageNode(normalizePackage(parseResult.packageName()), parseResult.filePath());
        addNode(graph, seenNodeIds, packageNode);

        if (parseResult.imports() != null) {
            for (String importName : parseResult.imports()) {
                GraphNode importedNode = resolveOrExternal(symbolIndex, importName);
                addNode(graph, seenNodeIds, importedNode);
                addEdge(graph, seenEdgeKeys, packageNode.id(), importedNode.id(), EdgeType.IMPORTS, isResolved(importedNode));
            }
        }

        if (parseResult.types() == null) {
            return;
        }

        for (ParsedType type : parseResult.types()) {
            processType(graph, seenNodeIds, seenEdgeKeys, parseResult, packageNode, type, symbolIndex);
        }
    }

    private void processType(ProjectGraph graph, Set<String> seenNodeIds, Set<String> seenEdgeKeys,
                             ParseResult parseResult, GraphNode packageNode, ParsedType type, SymbolIndex symbolIndex) {
        GraphNode typeNode = nodeFactory.typeNode(parseResult, type);
        addNode(graph, seenNodeIds, typeNode);
        addEdge(graph, seenEdgeKeys, packageNode.id(), typeNode.id(), EdgeType.CONTAINS, true);

        for (String superType : type.extendedTypes()) {
            GraphNode superTypeNode = resolveOrExternal(symbolIndex, superType);
            addNode(graph, seenNodeIds, superTypeNode);
            addEdge(graph, seenEdgeKeys, typeNode.id(), superTypeNode.id(), EdgeType.EXTENDS, isResolved(superTypeNode));
        }

        for (String interfaceType : type.implementedInterfaces()) {
            GraphNode interfaceNode = resolveOrExternal(symbolIndex, interfaceType);
            addNode(graph, seenNodeIds, interfaceNode);
            addEdge(graph, seenEdgeKeys, typeNode.id(), interfaceNode.id(), EdgeType.IMPLEMENTS, isResolved(interfaceNode));
        }

        for (String annotation : type.annotations()) {
            GraphNode annotationNode = nodeFactory.annotationNode(annotation);
            addNode(graph, seenNodeIds, annotationNode);
            addEdge(graph, seenEdgeKeys, typeNode.id(), annotationNode.id(), EdgeType.ANNOTATED_WITH, true);
        }

        for (ParsedField field : type.fields()) {
            processField(graph, seenNodeIds, seenEdgeKeys, parseResult, type, typeNode, field, symbolIndex);
        }

        for (ParsedMethod method : type.methods()) {
            processMethod(graph, seenNodeIds, seenEdgeKeys, parseResult, type, typeNode, method, symbolIndex);
        }
    }

    private void processField(ProjectGraph graph, Set<String> seenNodeIds, Set<String> seenEdgeKeys,
                              ParseResult parseResult, ParsedType type, GraphNode typeNode, ParsedField field,
                              SymbolIndex symbolIndex) {
        GraphNode fieldNode = nodeFactory.fieldNode(parseResult, type, field);
        addNode(graph, seenNodeIds, fieldNode);
        addEdge(graph, seenEdgeKeys, typeNode.id(), fieldNode.id(), EdgeType.CONTAINS, true);

        GraphNode fieldTypeNode = resolveOrExternal(symbolIndex, field.type());
        addNode(graph, seenNodeIds, fieldTypeNode);
        addEdge(graph, seenEdgeKeys, typeNode.id(), fieldTypeNode.id(), EdgeType.DEPENDS_ON, isResolved(fieldTypeNode));

        for (String annotation : field.annotations()) {
            GraphNode annotationNode = nodeFactory.annotationNode(annotation);
            addNode(graph, seenNodeIds, annotationNode);
            addEdge(graph, seenEdgeKeys, fieldNode.id(), annotationNode.id(), EdgeType.ANNOTATED_WITH, true);
        }
    }

    private void processMethod(ProjectGraph graph, Set<String> seenNodeIds, Set<String> seenEdgeKeys,
                               ParseResult parseResult, ParsedType type, GraphNode typeNode, ParsedMethod method,
                               SymbolIndex symbolIndex) {
        GraphNode methodNode = nodeFactory.methodNode(parseResult, type, method);
        addNode(graph, seenNodeIds, methodNode);
        addEdge(graph, seenEdgeKeys, typeNode.id(), methodNode.id(), EdgeType.CONTAINS, true);

        if (!method.isConstructor()) {
            GraphNode returnTypeNode = resolveOrExternal(symbolIndex, method.returnType());
            addNode(graph, seenNodeIds, returnTypeNode);
            addEdge(graph, seenEdgeKeys, methodNode.id(), returnTypeNode.id(), EdgeType.RETURNS, isResolved(returnTypeNode));
        }

        for (String parameterType : method.parameterTypes()) {
            GraphNode parameterTypeNode = resolveOrExternal(symbolIndex, parameterType);
            addNode(graph, seenNodeIds, parameterTypeNode);
            addEdge(graph, seenEdgeKeys, methodNode.id(), parameterTypeNode.id(), EdgeType.DEPENDS_ON, isResolved(parameterTypeNode));
        }

        for (String annotation : method.annotations()) {
            GraphNode annotationNode = nodeFactory.annotationNode(annotation);
            addNode(graph, seenNodeIds, annotationNode);
            addEdge(graph, seenEdgeKeys, methodNode.id(), annotationNode.id(), EdgeType.ANNOTATED_WITH, true);
        }

        for (ParsedMethodCall call : method.methodCalls()) {
            GraphNode calleeNode = resolveMethodCallOrPlaceholder(symbolIndex, call.calledMethodName());
            addNode(graph, seenNodeIds, calleeNode);
            addEdge(graph, seenEdgeKeys, methodNode.id(), calleeNode.id(), EdgeType.CALLS, isResolved(calleeNode));
        }
    }

    // ---------- Resolution helpers ----------

    private GraphNode resolveOrExternal(SymbolIndex symbolIndex, String typeName) {
        String simpleName = simpleNameOf(typeName);
        Optional<ResolvedSymbol> resolved = symbolIndex.resolveUnambiguousSimpleName(simpleName);

        return resolved
                .map(this::toResolvedGraphNode)
                .orElseGet(() -> nodeFactory.externalTypeNode(typeName));
    }

    private GraphNode resolveMethodCallOrPlaceholder(SymbolIndex symbolIndex, String methodName) {
        Optional<ResolvedSymbol> resolved = symbolIndex.resolveUnambiguousSimpleName(methodName);

        return resolved
                .filter(symbol -> symbol.nodeType() == NodeType.METHOD || symbol.nodeType() == NodeType.CONSTRUCTOR)
                .map(this::toResolvedGraphNode)
                .orElseGet(() -> nodeFactory.unresolvedMethodNode(methodName));
    }

    private GraphNode toResolvedGraphNode(ResolvedSymbol symbol) {
        return new GraphNode(symbol.id(), symbol.simpleName(), symbol.fullyQualifiedName(),
                symbol.nodeType(), symbol.filePath(), symbol.lineNumber(), Map.of("resolved", "true"));
    }

    private boolean isResolved(GraphNode node) {
        return !"false".equals(node.metadata().get("resolved"));
    }

    private String simpleNameOf(String typeName) {
        if (typeName == null) {
            return "";
        }

        int genericsStart = typeName.indexOf('<');
        String base = genericsStart >= 0 ? typeName.substring(0, genericsStart) : typeName;
        int lastDot = base.lastIndexOf('.');

        return lastDot >= 0 ? base.substring(lastDot + 1) : base;
    }

    private String normalizePackage(String packageName) {
        return (packageName == null || packageName.isBlank()) ? "default" : packageName;
    }

    // ---------- Duplicate avoidance ----------

    private void addNode(ProjectGraph graph, Set<String> seenNodeIds, GraphNode node) {
        if (seenNodeIds.add(node.id())) {
            graph.addNode(node);
        }
    }

    private void addEdge(ProjectGraph graph, Set<String> seenEdgeKeys, String sourceId, String targetId,
                         EdgeType type, boolean resolved) {
        String key = sourceId + "->" + targetId + ":" + type;

        if (seenEdgeKeys.add(key)) {
            graph.addEdge(new GraphEdge(sourceId, targetId, type, resolved));
        }
    }
}
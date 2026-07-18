package com.GitSenseAI.Retriever.EMBEDDING.mapper;

import com.GitSenseAI.Retriever.EMBEDDING.model.NodeDocument;
import com.GitSenseAI.Retriever.EMBEDDING.util.GraphIndex;
import com.GitSenseAI.Retriever.GRAPH.model.EdgeType;
import com.GitSenseAI.Retriever.GRAPH.model.GraphNode;
import com.GitSenseAI.Retriever.GRAPH.model.NodeType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Converts a GraphNode, together with its relationships in the wider
 * ProjectGraph, into an AI-readable text document suitable for embedding.
 * One method per node type keeps each template small and independently
 * adjustable, per single-responsibility.
 */
@Component
public class NodeToTextMapper {

    public NodeDocument toDocument(GraphNode node, GraphIndex index) {
        String text = switch (node.type()) {
            case CLASS, INTERFACE, ENUM -> buildTypeDocument(node, index);
            case METHOD, CONSTRUCTOR -> buildMethodDocument(node, index);
            case FIELD -> buildFieldDocument(node);
            case PACKAGE -> buildPackageDocument(node, index);
            case ANNOTATION -> buildAnnotationDocument(node);
        };

        return new NodeDocument(node.id(), node.type().name(), text);
    }

    private String buildTypeDocument(GraphNode node, GraphIndex index) {
        StringBuilder sb = new StringBuilder();
        sb.append(capitalize(node.type().name())).append("\n\n");
        sb.append(node.name()).append("\n");

        appendSection(sb, "Methods", index.outgoing(node.id(), EdgeType.CONTAINS).stream()
                .filter(n -> n.type() == NodeType.METHOD || n.type() == NodeType.CONSTRUCTOR)
                .map(n -> n.name() + "()")
                .toList());

        appendSection(sb, "Extends", index.outgoing(node.id(), EdgeType.EXTENDS).stream()
                .map(GraphNode::name).toList());

        appendSection(sb, "Implements", index.outgoing(node.id(), EdgeType.IMPLEMENTS).stream()
                .map(GraphNode::name).toList());

        appendSection(sb, "Called By", index.incoming(node.id(), EdgeType.CALLS).stream()
                .map(GraphNode::name).toList());

        appendSection(sb, "Depends On", index.outgoing(node.id(), EdgeType.DEPENDS_ON).stream()
                .map(GraphNode::name).toList());

        return sb.toString().trim();
    }

    private String buildMethodDocument(GraphNode node, GraphIndex index) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.type() == NodeType.CONSTRUCTOR ? "Constructor" : "Method").append("\n\n");
        sb.append(node.name()).append("(").append(node.metadata().getOrDefault("parameterTypes", "")).append(")\n");

        String returnType = node.metadata().get("returnType");
        if (returnType != null && !returnType.isBlank()) {
            appendSection(sb, "Returns", List.of(returnType));
        }

        appendSection(sb, "Calls", index.outgoing(node.id(), EdgeType.CALLS).stream()
                .map(GraphNode::name).toList());

        appendSection(sb, "Annotations", index.outgoing(node.id(), EdgeType.ANNOTATED_WITH).stream()
                .map(n -> "@" + n.name()).toList());

        return sb.toString().trim();
    }

    private String buildFieldDocument(GraphNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("Field\n\n");
        sb.append(node.name()).append("\n");

        String fieldType = node.metadata().get("fieldType");
        if (fieldType != null && !fieldType.isBlank()) {
            appendSection(sb, "Type", List.of(fieldType));
        }

        return sb.toString().trim();
    }

    private String buildPackageDocument(GraphNode node, GraphIndex index) {
        StringBuilder sb = new StringBuilder();
        sb.append("Package\n\n");
        sb.append(node.name()).append("\n");

        appendSection(sb, "Contains", index.outgoing(node.id(), EdgeType.CONTAINS).stream()
                .map(GraphNode::name).toList());

        return sb.toString().trim();
    }

    private String buildAnnotationDocument(GraphNode node) {
        return "Annotation\n\n@" + node.name();
    }

    private void appendSection(StringBuilder sb, String title, List<String> items) {
        if (items.isEmpty()) {
            return;
        }

        sb.append("\n").append(title).append(":\n");
        items.forEach(item -> sb.append(item).append("\n"));
    }

    private String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }
}
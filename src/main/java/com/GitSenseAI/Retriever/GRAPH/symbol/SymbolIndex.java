package com.GitSenseAI.Retriever.GRAPH.symbol;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Index of every symbol declared across the entire scanned repository,
 * built in a dedicated first pass before any edges are created. This is
 * what makes cross-file resolution possible — a class referencing a type
 * declared in a file parsed later still resolves correctly, because the
 * whole index exists before edge-building begins.
 *
 * Resolution by simple name is deliberately conservative: if a simple
 * name (e.g. "save") matches more than one declared symbol across the
 * codebase, it is treated as ambiguous and left unresolved rather than
 * guessing which one — this fixes the accuracy issue in the previous
 * name-only lookup implementation.
 */
public class SymbolIndex {

    private final Map<String, ResolvedSymbol> byFullyQualifiedName = new HashMap<>();
    private final Map<String, List<ResolvedSymbol>> bySimpleName = new HashMap<>();

    public void register(ResolvedSymbol symbol) {
        byFullyQualifiedName.putIfAbsent(symbol.fullyQualifiedName(), symbol);
        bySimpleName.computeIfAbsent(symbol.simpleName(), key -> new ArrayList<>()).add(symbol);
    }

    public Optional<ResolvedSymbol> resolveByFullyQualifiedName(String fullyQualifiedName) {
        return Optional.ofNullable(byFullyQualifiedName.get(fullyQualifiedName));
    }

    public Optional<ResolvedSymbol> resolveUnambiguousSimpleName(String simpleName) {
        List<ResolvedSymbol> candidates = bySimpleName.get(simpleName);

        if (candidates == null || candidates.size() != 1) {
            return Optional.empty();
        }

        return Optional.of(candidates.get(0));
    }
}
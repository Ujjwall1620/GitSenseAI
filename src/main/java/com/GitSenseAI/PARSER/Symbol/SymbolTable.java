package com.GitSenseAI.PARSER.Symbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SymbolTable {

    private final Map<String, Symbol> symbolsByName = new ConcurrentHashMap<>();
    private final Map<String, List<Symbol>> symbolsByFile = new ConcurrentHashMap<>();

    public void addSymbol(Symbol symbol) {
        symbolsByName.put(symbol.name(), symbol);
        symbolsByFile.computeIfAbsent(symbol.filePath(), key -> new ArrayList<>()).add(symbol);
    }

    public Optional<Symbol> lookup(String name) {
        return Optional.ofNullable(symbolsByName.get(name));
    }

    public List<Symbol> getSymbolsForFile(String filePath) {
        return symbolsByFile.getOrDefault(filePath, List.of());
    }

    public Collection<Symbol> getAllSymbols() {
        return symbolsByName.values();
    }
}
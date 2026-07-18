package com.GitSenseAI.Retriever.PARSER.Model;

public enum Languages {

    JAVA,
    KOTLIN,
    PYTHON,
    JAVASCRIPT,
    TYPESCRIPT,
    GO,
    C,
    CPP,
    CSHARP,
    RUBY,
    PHP,
    UNSUPPORTED;

    public static Languages fromExtension(String extension) {
        if (extension == null) {
            return UNSUPPORTED;
        }

        return switch (extension.toLowerCase()) {
            case "java" -> JAVA;
            case "kt", "kts" -> KOTLIN;
            case "py" -> PYTHON;
            case "js", "jsx" -> JAVASCRIPT;
            case "ts", "tsx" -> TYPESCRIPT;
            case "go" -> GO;
            case "c", "h" -> C;
            case "cpp", "cc", "cxx", "hpp", "hh" -> CPP;
            case "cs" -> CSHARP;
            case "rb" -> RUBY;
            case "php" -> PHP;
            default -> UNSUPPORTED;
        };
    }
}
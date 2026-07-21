package com.GitSenseAI.Retriever.PARSER.parser;

import com.GitSenseAI.Retriever.PARSER.DTO.ParseResult;
import com.GitSenseAI.Retriever.PARSER.Model.*;
import com.GitSenseAI.Retriever.PARSER.Exception.ParsingException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JavaParserAdapter implements LanguageParser {

    @Override
    public boolean supports(Languages language) {
        return language == Languages.JAVA;
    }

    @Override
    public ParseResult parse(Path file) {
        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(file);

            String packageName = compilationUnit.getPackageDeclaration()
                    .map(pd -> pd.getNameAsString())
                    .orElse("");

            List<String> imports = compilationUnit.getImports().stream()
                    .map(ImportDeclaration::getNameAsString)
                    .toList();

            List<ParsedType> types = new ArrayList<>();

            for (TypeDeclaration<?> typeDeclaration : compilationUnit.getTypes()) {
                types.add(extractType(typeDeclaration, packageName));
            }

            return new ParseResult(file.toString(), Languages.JAVA, packageName, imports, types);
        } catch (IOException ex) {
            log.error("Failed to read Java file: {}", file, ex);
            throw new ParsingException("Failed to read Java file: " + file, ex);
        } catch (Exception ex) {
            log.error("Failed to parse Java file: {}", file, ex);
            throw new ParsingException("Failed to parse Java file: " + file, ex);
        }
    }

    private ParsedType extractType(TypeDeclaration<?> typeDeclaration, String packageName) {
        NodeType nodeType = resolveNodeType(typeDeclaration);

        List<String> extendedTypes = new ArrayList<>();
        List<String> implementedInterfaces = new ArrayList<>();

        if (typeDeclaration instanceof com.github.javaparser.ast.body.ClassOrInterfaceDeclaration classOrInterface) {
            extendedTypes.addAll(classOrInterface.getExtendedTypes().stream()
                    .map(ClassOrInterfaceType::asString)
                    .toList());
            implementedInterfaces.addAll(classOrInterface.getImplementedTypes().stream()
                    .map(ClassOrInterfaceType::asString)
                    .toList());
        } else if (typeDeclaration instanceof EnumDeclaration enumDeclaration) {
            implementedInterfaces.addAll(enumDeclaration.getImplementedTypes().stream()
                    .map(ClassOrInterfaceType::asString)
                    .toList());
        }

        List<String> annotations = typeDeclaration.getAnnotations().stream()
                .map(AnnotationExpr::getNameAsString)
                .toList();

        List<ParsedField> fields = new ArrayList<>();
        List<ParsedMethod> methods = new ArrayList<>();

        for (BodyDeclaration<?> member : typeDeclaration.getMembers()) {
            if (member instanceof FieldDeclaration fieldDeclaration) {
                fields.addAll(extractFields(fieldDeclaration));
            } else if (member instanceof MethodDeclaration methodDeclaration) {
                methods.add(extractMethod(methodDeclaration, false));
            } else if (member instanceof ConstructorDeclaration constructorDeclaration) {
                methods.add(extractMethod(constructorDeclaration, true));
            }
        }

        int lineNumber = typeDeclaration.getBegin().map(pos -> pos.line).orElse(0);

        return new ParsedType(
                typeDeclaration.getNameAsString(),
                nodeType,
                packageName,
                extendedTypes,
                implementedInterfaces,
                fields,
                methods,
                annotations,
                lineNumber
        );
    }

    private NodeType resolveNodeType(TypeDeclaration<?> typeDeclaration) {
        if (typeDeclaration instanceof EnumDeclaration) {
            return NodeType.ENUM;
        }

        if (typeDeclaration instanceof com.github.javaparser.ast.body.ClassOrInterfaceDeclaration classOrInterface
                && classOrInterface.isInterface()) {
            return NodeType.INTERFACE;
        }

        return NodeType.CLASS;
    }

    private List<ParsedField> extractFields(FieldDeclaration fieldDeclaration) {
        List<String> annotations = fieldDeclaration.getAnnotations().stream()
                .map(AnnotationExpr::getNameAsString)
                .toList();

        int lineNumber = fieldDeclaration.getBegin().map(pos -> pos.line).orElse(0);

        List<ParsedField> fields = new ArrayList<>();

        for (VariableDeclarator variable : fieldDeclaration.getVariables()) {
            fields.add(new ParsedField(
                    variable.getNameAsString(),
                    variable.getTypeAsString(),
                    annotations,
                    lineNumber
            ));
        }

        return fields;
    }

    private ParsedMethod extractMethod(CallableDeclaration<?> callableDeclaration, boolean isConstructor) {
        List<String> parameterTypes = callableDeclaration.getParameters().stream()
                .map(parameter -> parameter.getType().asString())
                .toList();

        List<String> annotations = callableDeclaration.getAnnotations().stream()
                .map(AnnotationExpr::getNameAsString)
                .toList();

        String returnType = (callableDeclaration instanceof MethodDeclaration methodDeclaration)
                ? methodDeclaration.getType().asString()
                : "void";

        List<ParsedMethodCall> methodCalls = callableDeclaration.findAll(MethodCallExpr.class).stream()
                .map(this::toMethodCall)
                .toList();

        int lineNumber = callableDeclaration.getBegin().map(pos -> pos.line).orElse(0);

        return new ParsedMethod(
                callableDeclaration.getNameAsString(),
                returnType,
                parameterTypes,
                methodCalls,
                annotations,
                isConstructor,
                lineNumber,
                callableDeclaration.toString()
        );
    }

    private ParsedMethodCall toMethodCall(MethodCallExpr methodCallExpr) {
        String calledOnType = methodCallExpr.getScope()
                .map(scope -> scope.toString())
                .orElse(null);

        int lineNumber = methodCallExpr.getBegin().map(pos -> pos.line).orElse(0);

        return new ParsedMethodCall(methodCallExpr.getNameAsString(), calledOnType, lineNumber);
    }
}
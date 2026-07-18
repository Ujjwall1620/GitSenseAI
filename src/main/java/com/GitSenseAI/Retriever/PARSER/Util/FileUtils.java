package com.GitSenseAI.Retriever.PARSER.Util;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils {

    private FileUtils() {
    }

    public static boolean isBinary(Path file) {
        try (InputStream inputStream = Files.newInputStream(file)) {
            byte[] buffer = new byte[8000];
            int bytesRead = inputStream.read(buffer);

            for (int i = 0; i < bytesRead; i++) {
                if (buffer[i] == 0) {
                    return true;
                }
            }

            return false;
        } catch (IOException ex) {
            return true;
        }
    }

    public static String readContent(Path file) throws IOException {
        return Files.readString(file);
    }

    public static String getExtension(Path file) {
        String fileName = file.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1);
    }
}
package org.velvet.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public final class FileHandler {
    private FileHandler() {
    }

    public static List<String> readAllLines(Path path) {
        try {
            ensureFile(path);
            return new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read file: " + path, e);
        }
    }

    public static void writeAllLines(Path path, List<String> lines) {
        try {
            ensureFile(path);
            Files.write(path, lines == null ? Collections.emptyList() : lines, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write file: " + path, e);
        }
    }

    public static void appendLine(Path path, String line) {
        try {
            ensureFile(path);
            Files.write(path, List.of(line == null ? "" : line), StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to append to file: " + path, e);
        }
    }

    public static List<String> readFromFile(Path path) {
        return readAllLines(path);
    }

    public static void writeToFile(Path path, List<String> lines) {
        writeAllLines(path, lines);
    }

    public static void updateRecord(Path path, Predicate<String> matcher, String replacement) {
        List<String> lines = readAllLines(path);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            if (matcher != null && matcher.test(line)) {
                updated.add(replacement == null ? "" : replacement);
            } else {
                updated.add(line);
            }
        }
        writeAllLines(path, updated);
    }

    public static void deleteRecord(Path path, Predicate<String> matcher) {
        List<String> lines = readAllLines(path);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            if (matcher == null || !matcher.test(line)) {
                updated.add(line);
            }
        }
        writeAllLines(path, updated);
    }

    public static void ensureFile(Path path) {
        try {
            Path parent = path.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create file: " + path, e);
        }
    }
}

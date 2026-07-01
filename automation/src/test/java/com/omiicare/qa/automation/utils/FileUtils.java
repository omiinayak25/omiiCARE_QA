package com.omiicare.qa.automation.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Generic file helpers (Utility layer). No business logic.
 */
public final class FileUtils {

    private FileUtils() {}

    /** Ensures a directory (and parents) exists; returns it. */
    public static Path ensureDir(Path dir) {
        try {
            Files.createDirectories(dir);
            return dir;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create directory " + dir, e);
        }
    }

    /** Reads a UTF-8 text file. */
    public static String readString(Path file) {
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + file, e);
        }
    }

    /** Writes UTF-8 text, creating parent directories as needed. */
    public static Path writeString(Path file, String content) {
        try {
            if (file.getParent() != null) {
                ensureDir(file.getParent());
            }
            Files.writeString(file, content, StandardCharsets.UTF_8);
            return file;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write " + file, e);
        }
    }
}

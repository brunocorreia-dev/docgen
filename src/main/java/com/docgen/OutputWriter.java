package com.docgen;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class OutputWriter {

    public static void write(Path outputDir, String readme, String architecture) throws IOException {
        Files.createDirectories(outputDir);
        Files.writeString(outputDir.resolve("README.md"), readme, StandardCharsets.UTF_8);
        Files.writeString(outputDir.resolve("ARCHITECTURE.md"), architecture, StandardCharsets.UTF_8);
    }
}

package com.docgen;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class RepoScanner {

    private static final Set<String> SKIP_DIRS = Set.of(
        ".git", ".svn", ".hg", "node_modules", "target", "build", "dist",
        "out", ".idea", ".vscode", "__pycache__", ".gradle", "vendor",
        "coverage", ".nyc_output", ".next", ".cache", ".parcel-cache",
        "bin", "obj", ".nuget", "packages", "bower_components", "jspm_packages",
        ".terraform", ".serverless", "tmp", "temp", "logs"
    );

    private static final Set<String> SOURCE_EXTENSIONS = Set.of(
        ".java", ".kt", ".scala",
        ".py",
        ".js", ".ts", ".jsx", ".tsx", ".vue", ".svelte",
        ".go",
        ".rb",
        ".cs",
        ".php",
        ".cpp", ".c", ".h", ".hpp", ".cc",
        ".rs",
        ".swift",
        ".sh", ".bash", ".zsh",
        ".sql",
        ".r", ".R",
        ".dart",
        ".ex", ".exs",
        ".clj", ".cljs",
        ".hs", ".elm",
        ".tf", ".hcl"
    );

    private static final Set<String> CONFIG_FILES = Set.of(
        "pom.xml", "build.gradle", "build.gradle.kts", "settings.gradle",
        "package.json",
        "go.mod",
        "Cargo.toml",
        "requirements.txt", "pyproject.toml", "setup.py", "Pipfile",
        "Gemfile",
        "Makefile", "Dockerfile", "docker-compose.yml", "docker-compose.yaml",
        ".env.example", ".env.template",
        "tsconfig.json", "webpack.config.js", "vite.config.ts", "vite.config.js",
        ".gitignore"
    );

    private static final long MAX_FILE_BYTES = 100 * 1024;
    private static final int MAX_FILE_CHARS   = 2_000;
    private static final int MAX_CONTENT_CHARS = 8_000;

    public RepoContext scan(Path repoPath) throws IOException {
        List<Path> sourceFiles = new ArrayList<>();
        List<Path> configFiles = new ArrayList<>();
        StringBuilder fileTree = new StringBuilder();

        Files.walkFileTree(repoPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                if (dir.equals(repoPath)) {
                    String rootName = dir.getFileName() != null ? dir.getFileName().toString() : ".";
                    fileTree.append(rootName).append("/\n");
                    return FileVisitResult.CONTINUE;
                }
                String name = dir.getFileName().toString();
                if (SKIP_DIRS.contains(name) || name.startsWith(".")) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                int depth = repoPath.relativize(dir).getNameCount();
                fileTree.append("  ".repeat(depth)).append(name).append("/\n");
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (attrs.size() > MAX_FILE_BYTES) return FileVisitResult.CONTINUE;
                String name = file.getFileName().toString();
                if (name.startsWith(".") && !CONFIG_FILES.contains(name)) {
                    return FileVisitResult.CONTINUE;
                }

                int depth = repoPath.relativize(file).getNameCount();
                fileTree.append("  ".repeat(depth)).append(name).append("\n");

                String ext = extension(name);
                if (SOURCE_EXTENSIONS.contains(ext)) {
                    sourceFiles.add(file);
                } else if (CONFIG_FILES.contains(name)) {
                    configFiles.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        // Config files first, then source files sorted by size ascending (more diversity)
        sourceFiles.sort(Comparator.comparingLong(p -> p.toFile().length()));
        List<Path> ordered = new ArrayList<>(configFiles);
        ordered.addAll(sourceFiles);

        StringBuilder content = new StringBuilder();
        int filesIncluded = 0;

        for (Path file : ordered) {
            if (content.length() >= MAX_CONTENT_CHARS) break;

            try {
                String raw = Files.readString(file, StandardCharsets.UTF_8);
                String text = raw.length() > MAX_FILE_CHARS
                    ? raw.substring(0, MAX_FILE_CHARS) + "\n... [truncated]"
                    : raw;
                String rel = repoPath.relativize(file).toString();
                String entry = "\n\n--- " + rel + " ---\n" + text;

                if (content.length() + entry.length() > MAX_CONTENT_CHARS) {
                    int remaining = MAX_CONTENT_CHARS - content.length();
                    if (remaining > 300) {
                        content.append("\n\n--- ").append(rel).append(" (truncated) ---\n");
                        content.append(text, 0, remaining - 50).append("\n... [truncated]");
                    }
                    break;
                }
                content.append(entry);
                filesIncluded++;
            } catch (IOException ignored) {
                // Skip unreadable files silently
            }
        }

        return new RepoContext(fileTree.toString(), content.toString(), filesIncluded, content.length() / 1024.0);
    }

    private String extension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot).toLowerCase() : "";
    }
}

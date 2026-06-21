package com.docgen;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LLMClient {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    private final String model;
    private final HttpClient http;

    public LLMClient(String model) {
        this.model = model;
        this.http = HttpClient.newHttpClient();
        checkOllamaRunning();
    }

    public String generate(String prompt) throws IOException, InterruptedException {
        String body = """
            {"model": %s, "prompt": %s, "stream": true}
            """.formatted(jsonString(model), jsonString(prompt));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(OLLAMA_URL))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        StringBuilder result = new StringBuilder();

        http.send(request, HttpResponse.BodyHandlers.ofLines())
            .body()
            .forEach(line -> {
                if (line.isBlank()) return;
                String token = extractField(line, "response");
                if (token != null && !token.isEmpty()) {
                    System.out.print(token);
                    System.out.flush();
                    result.append(token);
                }
            });

        System.out.println();
        return result.toString();
    }

    private void checkOllamaRunning() {
        try {
            HttpRequest ping = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434"))
                .GET()
                .build();
            http.send(ping, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            throw new IllegalStateException(
                "Ollama não está rodando. Inicie com: ollama serve"
            );
        }
    }

    // Extracts the value of a JSON string field from a flat JSON line.
    // Handles common escape sequences (\n, \t, \\, \").
    private String extractField(String json, String field) {
        String key = "\"" + field + "\":\"";
        int start = json.indexOf(key);
        if (start < 0) return null;
        start += key.length();

        StringBuilder sb = new StringBuilder();
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(++i);
                switch (next) {
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case 'r' -> sb.append('\r');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    default -> { sb.append('\\'); sb.append(next); }
                }
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String jsonString(String s) {
        return "\"" + s
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            + "\"";
    }
}

package com.docgen;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiProvider implements LLMProvider {

    private static final String API_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    private final String model;
    private final String apiKey;
    private final HttpClient http;

    public GeminiProvider(String model) {
        this.model = model;
        this.apiKey = resolveApiKey();
        this.http = HttpClient.newHttpClient();
    }

    @Override
    public String generate(String prompt) throws IOException, InterruptedException {
        String url = API_URL.formatted(model, apiKey);

        String body = """
            {
              "contents": [
                {
                  "parts": [
                    { "text": %s }
                  ]
                }
              ],
              "generationConfig": {
                "temperature": 0.2
              }
            }
            """.formatted(jsonString(prompt));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Gemini API error " + response.statusCode() + ": " + response.body());
        }

        String text = extractText(response.body());
        if (text == null) {
            throw new IOException("Could not parse Gemini response: " + response.body());
        }

        System.out.print(text);
        System.out.println();
        return text;
    }

    // Navigates to "parts" then extracts the first "text" string value.
    // Covers candidates[0].content.parts[0].text without a JSON library.
    private String extractText(String json) {
        int partsIdx = json.indexOf("\"parts\"");
        if (partsIdx < 0) return null;

        int textKeyIdx = json.indexOf("\"text\":\"", partsIdx);
        if (textKeyIdx < 0) return null;

        return extractStringValue(json, textKeyIdx + "\"text\":\"".length());
    }

    private String extractStringValue(String json, int start) {
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

    private String resolveApiKey() {
        String key = System.getenv("GEMINI_API_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalStateException(
                "GEMINI_API_KEY environment variable is not set.\n" +
                "Get your key at https://aistudio.google.com/app/apikey"
            );
        }
        return key;
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

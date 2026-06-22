package com.docgen;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroqProvider implements LLMProvider {

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final String model;
    private final String apiKey;
    private final HttpClient http;

    public GroqProvider(String model, String apiKey) {
        this.model = model;
        this.apiKey = resolveApiKey(apiKey);
        this.http = HttpClient.newHttpClient();
    }

    @Override
    public String generate(String prompt) throws IOException, InterruptedException {
        String body = """
            {
              "model": %s,
              "messages": [
                {"role": "user", "content": %s}
              ],
              "temperature": 0.2
            }
            """.formatted(jsonString(model), jsonString(prompt));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 429) {
            long waitSeconds = extractRetrySeconds(response.body()) + 2;
            System.out.println("[docgen] Rate limit hit, waiting " + waitSeconds + "s...");
            Thread.sleep(waitSeconds * 1000);
            response = http.send(request, HttpResponse.BodyHandlers.ofString());
        }

        if (response.statusCode() != 200) {
            throw new IOException("Groq API error " + response.statusCode() + ": " + response.body());
        }

        String text = extractContent(response.body());
        if (text == null) {
            throw new IOException("Could not parse Groq response: " + response.body());
        }

        System.out.print(text);
        System.out.println();
        return text;
    }

    // Extracts seconds from "try again in X.XXs"; returns 10 as fallback.
    private long extractRetrySeconds(String body) {
        Matcher m = Pattern.compile("try again in (\\d+(?:\\.\\d+)?)s").matcher(body);
        if (m.find()) {
            return (long) Math.ceil(Double.parseDouble(m.group(1)));
        }
        return 10;
    }

    // Navigates choices[0].message.content without a JSON library.
    // Finds "choices" → "message" → "content": → extracts the string value.
    private String extractContent(String json) {
        int choicesIdx = json.indexOf("\"choices\"");
        if (choicesIdx < 0) return null;

        int messageIdx = json.indexOf("\"message\"", choicesIdx);
        if (messageIdx < 0) return null;

        int contentIdx = json.indexOf("\"content\":", messageIdx);
        if (contentIdx < 0) return null;

        int valueStart = json.indexOf("\"", contentIdx + "\"content\":".length());
        if (valueStart < 0) return null;

        return extractStringValue(json, valueStart + 1);
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

    private String resolveApiKey(String flagKey) {
        if (flagKey != null && !flagKey.isBlank()) return flagKey;
        String envKey = System.getenv("GROQ_API_KEY");
        if (envKey != null && !envKey.isBlank()) return envKey;
        throw new IllegalStateException(
            "Groq API key not found. Set GROQ_API_KEY or use --api-key.\n" +
            "Get your free key at https://console.groq.com"
        );
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

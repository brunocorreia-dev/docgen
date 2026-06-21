package com.docgen;

public class LLMClient {

    public static LLMProvider create(String provider, String model, String apiKey) {
        return switch (provider.toLowerCase()) {
            case "ollama" -> new OllamaProvider(model);
            case "gemini" -> new GeminiProvider(model, apiKey);
            default -> throw new IllegalArgumentException("Unknown provider: " + provider + ". Use 'gemini' or 'ollama'.");
        };
    }
}

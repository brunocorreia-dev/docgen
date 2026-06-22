package com.docgen;

public class LLMProviderFactory {

    public static LLMProvider create(String provider, String model, String apiKey) {
        return switch (provider.toLowerCase()) {
            case "groq" -> new GroqProvider(model, apiKey);
            case "ollama" -> new OllamaProvider(model);
            default -> throw new IllegalArgumentException(
                "Unknown provider: " + provider + ". Valid options: groq, ollama"
            );
        };
    }
}

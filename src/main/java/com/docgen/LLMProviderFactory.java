package com.docgen;

public class LLMProviderFactory {

    public static LLMProvider create(String provider, String model) {
        return switch (provider.toLowerCase()) {
            case "gemini" -> new GeminiProvider(model);
            case "ollama" -> new OllamaProvider(model);
            default -> throw new IllegalArgumentException(
                "Unknown provider: " + provider + ". Valid options: gemini, ollama"
            );
        };
    }
}

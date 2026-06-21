package com.docgen;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.core.http.StreamResponse;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.anthropic.models.messages.RawMessageStreamEvent;

public class LLMClient {

    private final AnthropicClient client;

    public LLMClient() {
        String apiKey = System.getenv("ANTHROPIC_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                "ANTHROPIC_API_KEY is not set. Export it before running: export ANTHROPIC_API_KEY=sk-..."
            );
        }
        this.client = AnthropicOkHttpClient.fromEnv();
    }

    public String generate(String prompt) {
        MessageCreateParams params = MessageCreateParams.builder()
            .model(Model.of("claude-opus-4-8"))
            .maxTokens(8192L)
            .addUserMessage(prompt)
            .build();

        StringBuilder result = new StringBuilder();
        try (StreamResponse<RawMessageStreamEvent> stream = client.messages().createStreaming(params)) {
            stream.stream()
                .flatMap(event -> event.contentBlockDelta().stream())
                .flatMap(delta -> delta.delta().text().stream())
                .forEach(textDelta -> {
                    String chunk = textDelta.text();
                    System.out.print(chunk);
                    System.out.flush();
                    result.append(chunk);
                });
        }
        System.out.println();
        return result.toString();
    }
}

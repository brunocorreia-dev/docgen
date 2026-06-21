package com.docgen;

import java.io.IOException;

public interface LLMProvider {
    String generate(String prompt) throws IOException, InterruptedException;
}

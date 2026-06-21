package com.docgen;

public class PromptBuilder {

    public static String readmePrompt(RepoContext ctx) {
        return """
            You are an expert technical writer. Analyze this code repository and generate a comprehensive, professional README.md.

            Include all relevant sections:
            - Project title and a clear, compelling description
            - Features and capabilities
            - Tech stack and key dependencies
            - Prerequisites
            - Installation and setup instructions (step-by-step, copy-pasteable commands)
            - Usage examples with real code snippets
            - Project structure overview
            - Configuration options (if any environment variables or config files exist)
            - API reference (if it's a library or has HTTP endpoints)
            - Contributing guidelines
            - License

            Skip sections that don't apply to this project.

            ## Repository Structure

            ```
            %s
            ```

            ## File Contents

            %s

            Write the complete README.md now. Use clean Markdown formatting.
            """.formatted(ctx.fileTree(), ctx.fileContents());
    }

    public static String architecturePrompt(RepoContext ctx) {
        return """
            You are a software architect. Analyze this code repository and produce an ARCHITECTURE.md file.

            The file must contain:
            1. A 2–4 sentence summary of what the system does and its architectural style
            2. A Mermaid diagram in a ```mermaid code block showing the main components and their relationships
            3. A brief legend explaining each component (bullet list)

            Diagram guidelines:
            - Pick the diagram type that best fits:
                - `flowchart TD` for layered / pipeline architectures
                - `classDiagram` for OOP class hierarchies
                - `sequenceDiagram` for key request flows
                - `graph LR` for dependency graphs
            - Keep it under 20 nodes so it stays readable
            - Show data flows and major dependencies

            ## Repository Structure

            ```
            %s
            ```

            ## File Contents

            %s

            Write the complete ARCHITECTURE.md now.
            """.formatted(ctx.fileTree(), ctx.fileContents());
    }
}

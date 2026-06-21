package com.docgen;

public class PromptBuilder {

    public static String readmePrompt(RepoContext ctx) {
        return """
            You are a technical writer. Your job is to EXTRACT information from the repository files below and write a README.md. DO NOT invent, assume or add anything not explicitly found in the provided files. If information for a section is not present, skip that section entirely.

            ## Extraction rules per section

            **Project name**: extract from pom.xml `<artifactId>`, package.json `name`, Cargo.toml `[package] name`, or the directory name. Do not invent.

            **Description**: extract from pom.xml `<description>`, package.json `description`, or the main class/module docstring. If absent, write one sentence based only on what the entry point class/file does.

            **Tech stack**: list ONLY technologies found in dependency files (pom.xml, package.json, go.mod, Cargo.toml, requirements.txt, build.gradle). Include the version if present in those files. Do not add technologies not listed there.

            **Prerequisites**: derive ONLY from the dependency/build files. Java version from `<maven.compiler.release>` or `<java.version>`. Node version from `.nvmrc` or `engines` in package.json. Do not guess versions not explicitly stated.

            **Installation**: use ONLY the build commands that correspond to the build tool found. pom.xml → Maven commands. build.gradle → Gradle commands. package.json → npm or yarn. Do not mix tools or invent commands.

            **Usage**: extract CLI flags and arguments ONLY from what is explicitly defined in the source code. Look for `@Option`, `@Parameters` (Picocli), `argparse`, `commander`, `clap`, or equivalent annotations/calls. List each flag with its exact name, description and default value as found in the code. Do not add flags that do not exist in the code.

            **Project structure**: use the file tree provided below. Describe each file or directory based ONLY on what you can read in its content. Do not describe files that are not in the tree.

            **Configuration**: list ONLY environment variables and config options explicitly referenced in the source code (look for `System.getenv`, `process.env`, `os.environ`, `env::var`, etc). Do not invent config options.

            Do not include sections for which you found no evidence in the provided files. Do not add Contributing, License, or API Reference sections unless those files or annotations exist in the repository.

            ## Repository Structure

            ```
            %s
            ```

            ## File Contents

            %s

            Write the README.md now. Use clean Markdown formatting. Include only what the extraction rules above permit.
            """.formatted(ctx.fileTree(), ctx.fileContents());
    }

    public static String architecturePrompt(RepoContext ctx) {
        return """
            You are a software architect. Analyze ONLY the provided files and produce an ARCHITECTURE.md. Base every statement on evidence found in the code. Do not invent components, layers or patterns not visible in the files.

            The file must contain:

            1. **Summary**: write 2-3 sentences describing what the system does. Base this ONLY on the entry point class/file and the call chain you can trace from it through the provided files. Do not describe hypothetical capabilities.

            2. **Mermaid diagram**: generate a Mermaid diagram showing ONLY components that exist as classes or modules in the provided files. Each node must correspond to an actual file or class found in the repository. Do not add external systems, databases or services unless they are explicitly called in the code (look for HTTP calls, JDBC connections, SQL statements, etc). Pick the diagram type that best fits the code structure:
               - `flowchart TD` for layered or pipeline architectures
               - `classDiagram` for OOP class hierarchies
               - `sequenceDiagram` for key request/response flows
               - `graph LR` for dependency graphs
               Keep it under 20 nodes.

            3. **Legend**: for each node in the diagram, write one line describing what that class/module does, based on what you read in its source file. Do not describe files you have not read.

            ## Repository Structure

            ```
            %s
            ```

            ## File Contents

            %s

            Write the complete ARCHITECTURE.md now. Every claim must trace back to a specific file in the repository above.
            """.formatted(ctx.fileTree(), ctx.fileContents());
    }
}

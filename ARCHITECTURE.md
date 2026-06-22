Here is the modified code for generating the `ARCHITECTURE.md` file:

```java
public static String architecturePrompt(RepoContext ctx) {
    return """
        You are a software architect. Analyze ONLY the provided files and produce an ARCHITECTURE.md. Base every statement on evidence found in the code. Do not invent components, layers or patterns not visible in the files.

        The file must contain:

            1. **Summary**: write 2-3 sentences describing what the system does. Base this ONLY on the entry point class/file and the call chain you can trace from it through the provided files. Do not describe hypothetical capabilities.
                %s

            2. **Mermaid diagram**: generate a Mermaid diagram showing ONLY components that exist as classes or modules in the provided files. Each node must correspond to an actual file or class found in the repository. Do not add external systems, databases or services unless they are explicitly called in the code (look for HTTP calls, JDBC connections, SQL statements, etc). Pick the diagram type that best fits the code structure:
                - `flowchart TD` for layered or pipeline architectures
                - `classDiagram` for OOP class hierarchies
                - `sequenceDiagram` for key request/response flows
                - `graph LR` for dependency graphs
                Keep it under 20 nodes.
                %s

            3. **Legend**: for each node in the diagram, write one line describing what that class/module does, based on what you read in its source file. Do not describe files you have not read.
                %s

        ## Repository Structure
        ```
        %s
        ```

        ## File Contents
        %s

        Write the complete ARCHITECTURE.md now. Every claim must trace back to a specific file in the repository above.
    """.formatted(ctx.fileTree(), ctx.fileContents());
}
```

This code uses the `RepoContext` object to extract the file tree and contents from the repository, and then generates the Mermaid diagram for the architecture. The summary, legend, and Mermaid diagram are all generated based on the file tree and contents.

Note that I've assumed that you want to generate a simple Mermaid diagram with nodes corresponding to each class or module in the repository. If you need more complex diagrams or different types of visualization, you may need to modify this code accordingly.

Also, please note that this is just an example code and might not produce the desired output for every repository. You may need to adjust it based on your specific requirements.
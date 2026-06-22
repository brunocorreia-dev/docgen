package com.docgen;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;

@Command(
    name = "docgen",
    mixinStandardHelpOptions = true,
    version = "1.0",
    description = "Generate README and architecture diagram from a code repository using an LLM"
)
public class DocGenCLI implements Runnable {

    @Parameters(index = "0", description = "Path to the repository to document", defaultValue = ".")
    private Path repoPath;

    @Option(names = {"-o", "--output"}, description = "Output directory for generated files (default: current dir)", defaultValue = ".")
    private Path outputDir;

    @Option(names = {"--provider"}, description = "LLM provider: gemini (default) or ollama", defaultValue = "gemini")
    private String provider;

    @Option(names = {"-m", "--model"}, description = "Model to use (default: gemini-2.0-flash)", defaultValue = "gemini-2.0-flash")
    private String model;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new DocGenCLI()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        try {
            if ("ollama".equalsIgnoreCase(provider) && "gemini-2.0-flash".equals(model)) {
                model = "llama3.2";
            }

            Path absRepo = repoPath.toAbsolutePath().normalize();
            Path absOutput = outputDir.toAbsolutePath().normalize();

            System.out.println("Scanning repository: " + absRepo);
            RepoContext ctx = new RepoScanner().scan(absRepo);
            System.out.printf("Collected %d files (%.1f KB of content)%n", ctx.fileCount(), ctx.contentSizeKb());
            System.out.println("Provider: " + provider + " | Model: " + model);

            LLMProvider llm = LLMProviderFactory.create(provider, model);

            System.out.println("\n=== Generating README ===\n");
            String readme = llm.generate(PromptBuilder.readmePrompt(ctx));

            System.out.println("\n\n=== Generating Architecture Diagram ===\n");
            String architecture = llm.generate(PromptBuilder.architecturePrompt(ctx));

            OutputWriter.write(absOutput, readme, architecture);
            System.out.printf("%nDone! Files written:%n  %s/README.md%n  %s/ARCHITECTURE.md%n",
                absOutput, absOutput);
        } catch (IllegalStateException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}

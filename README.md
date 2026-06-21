# docgen

CLI that scans a code repository and generates `README.md` and `ARCHITECTURE.md` (Mermaid diagram) using an LLM. Supports Google Gemini (default) and Ollama (local, no API key required).

## Requirements

- Java 21+
- **Gemini (default):** free API key from [aistudio.google.com](https://aistudio.google.com)
- **Ollama (alternative):** [Ollama](https://ollama.com) running locally (`ollama serve`) + model pulled

## Installation

### Option 1 — Script (recommended)

```bash
curl -fsSL https://raw.githubusercontent.com/brunocorreia-dev/docgen/main/install.sh | bash
```

Downloads the latest `docgen.jar` from GitHub Releases, places it in `~/.local/share/docgen/`, and installs a wrapper at `~/.local/bin/docgen`. Checks that Java 21+ and Ollama are present.

### Option 2 — Manual

1. Download `docgen.jar` from the [latest release](https://github.com/brunocorreia-dev/docgen/releases/latest).
2. Run it directly:

```bash
java -jar docgen.jar <repo-path> [options]
```

## Providers

### Gemini (default)

Uses the Google Gemini API. Requires a free API key from [aistudio.google.com](https://aistudio.google.com). Default model: `gemini-2.0-flash`.

### Ollama (alternative)

Runs locally with no API key. Requires `ollama serve` running and a model pulled (e.g. `ollama pull llama3.2`). Default model: `llama3.2`.

## Usage

```bash
java -jar target/docgen.jar <repo-path> [options]
```

| Argument | Description | Default |
|---|---|---|
| `<repo-path>` | Path to the repository to document | `.` (current dir) |
| `-o`, `--output` | Directory where files will be written | `.` (current dir) |
| `--provider` | LLM provider: `gemini` or `ollama` | `gemini` |
| `-m`, `--model` | Model to use | `gemini-2.0-flash` / `llama3.2` |
| `--api-key` | Gemini API key (or set `GEMINI_API_KEY`) | — |

**Examples:**

```bash
# Gemini (default) — via env var
export GEMINI_API_KEY=sua_chave
docgen /path/to/repo -o ./docs

# Gemini — via flag
docgen /path/to/repo --api-key sua_chave -o ./docs

# Ollama — local model
docgen /path/to/repo --provider ollama -m mistral -o ./docs
```

## Output

- `README.md` — project overview generated from source code analysis
- `ARCHITECTURE.md` — Mermaid architecture diagram inferred from the codebase

## Project Structure
src/main/java/com/docgen/

├── DocGenCLI.java       # CLI entry point (Picocli)

├── RepoScanner.java     # Walks the repo, collects up to 400KB of content

├── RepoContext.java     # Record holding file tree, contents and stats

├── PromptBuilder.java   # Builds prompts for README and architecture generation

├── LLMClient.java       # Factory: creates the right LLMProvider

├── LLMProvider.java     # Interface implemented by each provider

├── GeminiProvider.java  # HTTP client → Gemini generateContent API

├── OllamaProvider.java  # HTTP client → Ollama /api/generate (streaming)

└── OutputWriter.java    # Writes README.md and ARCHITECTURE.md

## Tech Stack

- Java 21
- [Picocli](https://picocli.info) — CLI framework
- [Google Gemini API](https://ai.google.dev) — default LLM provider
- [Ollama](https://ollama.com) — local LLM alternative
- Maven — build tool

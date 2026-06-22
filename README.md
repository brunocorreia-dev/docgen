# docgen

CLI that scans a code repository and generates `README.md` and `ARCHITECTURE.md` (Mermaid diagram) using an LLM — supports Groq (default, free API) and Ollama (local, no API key).

## Requirements

- Java 21+
- **Groq (default):** Free API key from [console.groq.com](https://console.groq.com) — keys start with `gsk_`
- **Ollama (alternative):** [Ollama](https://ollama.com) running locally (`ollama serve`)

## Installation

**Option 1 — Script (recommended)**

```bash
curl -fsSL https://raw.githubusercontent.com/brunocorreia-dev/docgen/main/install.sh | bash
```

Downloads the latest `docgen.jar` from GitHub Releases, places it in `~/.local/share/docgen/`, and installs a wrapper at `~/.local/bin/docgen`.

**Option 2 — Manual**

Download `docgen.jar` from the [latest release](https://github.com/brunocorreia-dev/docgen/releases) and run:

```bash
java -jar docgen.jar <repo-path> [options]
```

## Providers

### Groq (default)

Get a free API key at [console.groq.com](https://console.groq.com). Keys start with `gsk_`. Default model: `llama-3.3-70b-versatile`.

```bash
# Via environment variable
export GROQ_API_KEY=gsk_...
docgen /path/to/repo -o ./docs

# Or pass it directly
docgen /path/to/repo --api-key gsk_... -o ./docs
```

### Ollama (local, no API key)

```bash
ollama serve
ollama pull llama3.2
docgen /path/to/repo --provider ollama -o ./docs
```

## Usage

```bash
java -jar target/docgen.jar <repo-path> [options]
```

| Argument | Description | Default |
|---|---|---|
| `<repo-path>` | Path to the repository to document | `.` (current dir) |
| `--provider` | LLM provider: `groq` or `ollama` | `groq` |
| `--api-key` | Groq API key (or set `GROQ_API_KEY`) | — |
| `-m`, `--model` | Model to use | `llama-3.3-70b-versatile` / `llama3.2` |
| `-o`, `--output` | Directory where files will be written | `.` (current dir) |

## Output

- `README.md` — project overview generated from source code analysis
- `ARCHITECTURE.md` — Mermaid architecture diagram inferred from the codebase

## Project Structure
src/main/java/com/docgen/

├── DocGenCLI.java          # CLI entry point (Picocli)

├── LLMClient.java          # Factory — creates the right provider

├── LLMProvider.java        # Interface implemented by all providers

├── GroqProvider.java       # Groq API (default, OpenAI-compatible)

├── OllamaProvider.java     # Local Ollama inference

├── RepoScanner.java        # Walks the repo, collects up to 400KB of content

├── RepoContext.java        # Record holding file tree, contents and stats

├── PromptBuilder.java      # Builds prompts for README and architecture generation

└── OutputWriter.java       # Writes README.md and ARCHITECTURE.md

## Tech Stack

- Java 21
- [Picocli](https://picocli.info) — CLI framework
- [Groq API](https://console.groq.com) — default LLM provider (OpenAI-compatible, free)
- [Ollama](https://ollama.com) — local LLM alternative
- Maven — build tool

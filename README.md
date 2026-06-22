# docgen

CLI that scans a code repository and generates `README.md` and `ARCHITECTURE.md` (Mermaid diagram) using an LLM — supports Google Gemini (default) and Ollama (local, no API key).

## Requirements

- Java 21+
- **Gemini (default):** API key from [aistudio.google.com](https://aistudio.google.com) — free tier available
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

### Gemini (default)

Get a free API key at [aistudio.google.com](https://aistudio.google.com) → Get API key → Create API key.

```bash
export GEMINI_API_KEY=your_key_here
docgen /path/to/repo -o ./docs
```

Or pass it directly:

```bash
docgen /path/to/repo --api-key your_key_here -o ./docs
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
| `--provider` | LLM provider: `gemini` or `ollama` | `gemini` |
| `--api-key` | Gemini API key (or set `GEMINI_API_KEY`) | — |
| `-m`, `--model` | Model to use | `gemini-2.0-flash` / `llama3.2` |
| `-o`, `--output` | Directory where files will be written | `.` (current dir) |

## Output

- `README.md` — project overview generated from source code analysis
- `ARCHITECTURE.md` — Mermaid architecture diagram inferred from the codebase

## Project Structure
src/main/java/com/docgen/

├── DocGenCLI.java          # CLI entry point (Picocli)

├── LLMClient.java          # Factory — creates the right provider

├── LLMProvider.java        # Interface implemented by all providers

├── GeminiProvider.java     # Google Gemini API (default)

├── OllamaProvider.java     # Local Ollama inference

├── RepoScanner.java        # Walks the repo, collects up to 400KB of content

├── RepoContext.java        # Record holding file tree, contents and stats

├── PromptBuilder.java      # Builds prompts for README and architecture generation

└── OutputWriter.java       # Writes README.md and ARCHITECTURE.md

## Tech Stack

- Java 21
- [Picocli](https://picocli.info) — CLI framework
- [Google Gemini API](https://aistudio.google.com) — default LLM provider
- [Ollama](https://ollama.com) — local LLM alternative
- Maven — build tool

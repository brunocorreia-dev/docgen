# docgen

CLI that scans a code repository and generates `README.md` and `ARCHITECTURE.md` (Mermaid diagram) using a local LLM via Ollama — no API key required.

## Requirements

- Java 21+
- [Ollama](https://ollama.com) running locally (`ollama serve`)
- llama3.2 pulled (`ollama pull llama3.2`)

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

## Usage

```bash
java -jar target/docgen.jar <repo-path> [options]
```

| Argument | Description | Default |
|---|---|---|
| `<repo-path>` | Path to the repository to document | `.` (current dir) |
| `-o`, `--output` | Directory where files will be written | `.` (current dir) |
| `-m`, `--model` | Ollama model to use | `llama3.2` |

**Example:**

```bash
# Start Ollama
ollama serve

# Generate docs for a project
java -jar target/docgen.jar /path/to/my-project -o /path/to/output

# Using a different model
java -jar target/docgen.jar /path/to/my-project -m mistral -o ./docs
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

├── LLMClient.java       # HTTP client → Ollama /api/generate (streaming)

└── OutputWriter.java    # Writes README.md and ARCHITECTURE.md

## Tech Stack

- Java 21
- [Picocli](https://picocli.info) — CLI framework
- [Ollama](https://ollama.com) — local LLM inference
- Maven — build tool

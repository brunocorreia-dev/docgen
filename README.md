# docgen

CLI that scans a code repository and generates `README.md` and `ARCHITECTURE.md` (Mermaid diagram) using a local LLM via Ollama — no API key required.

## Requirements

- Java 21+
- [Ollama](https://ollama.com) running locally (`ollama serve`)
- llama3.2 pulled (`ollama pull llama3.2`)

## Installation

```bash
git clone https://github.com/brunocorreia-dev/docgen.git
cd docgen
```

Build the fat jar:

```bash
java -cp "$MVN_HOME/boot/plexus-classworlds-2.9.0.jar" \
  -Dclassworlds.conf="$MVN_HOME/bin/m2.conf" \
  -Dmaven.multiModuleProjectDirectory=$(pwd) \
  org.codehaus.plexus.classworlds.launcher.Launcher \
  -f pom.xml package -q
```

> `$MVN_HOME` is the path to your Maven installation (e.g. `~/idea-IU-.../plugins/maven/lib/maven3`).

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

# docgen

AI-powered documentation generator for code repositories. Point it at any codebase and get a professional `README.md` and `ARCHITECTURE.md` in seconds — powered by Claude Opus.

```
./docgen /path/to/your/project
```

---

## What it does

`docgen` scans a repository, builds a rich context from its source files and configuration, and sends that context to the Claude API. It streams back two documents:

| Output | Description |
|---|---|
| `README.md` | Full project documentation — setup, usage, API reference, contributing guide |
| `ARCHITECTURE.md` | System overview with a Mermaid diagram and component breakdown |

No templates, no manual fill-in-the-blanks. The output is generated fresh from your actual code.

---

## Features

- **Single command** — one invocation generates both documents
- **Real-time streaming** — watch the docs appear as Claude writes them
- **Smart repository scanning** — reads source files in 30+ languages, skips binaries and build artifacts
- **Content budget** — caps at 400 K characters (~100 K tokens) with per-file limits so large repos don't blow up the API call
- **Ordered ingestion** — config files first, then source files by size ascending, maximizing signal density
- **Uber-JAR distribution** — ships as a single self-contained JAR with a thin Bash wrapper

---

## Requirements

| Requirement | Version |
|---|---|
| Java | 17 or later |
| Maven | 3.6.0 or later |
| Anthropic API key | Any valid key with Opus access |

---

## Installation

```bash
# 1. Clone the repository
git clone https://github.com/brunocorreia-dev/docgen.git
cd docgen

# 2. Export your Anthropic API key
export ANTHROPIC_API_KEY=sk-ant-...

# 3. Build the uber-JAR
mvn clean package

# 4. (Optional) Put the wrapper on your PATH
ln -s "$(pwd)/docgen" /usr/local/bin/docgen
```

The `mvn clean package` step produces `target/docgen.jar`. The included `docgen` shell script wraps it so you never have to type `java -jar`.

---

## Usage

```bash
# Document the current directory
./docgen

# Document a specific repository
./docgen /path/to/repo

# Write output to a custom directory
./docgen /path/to/repo --output ./docs

# Short form
./docgen /path/to/repo -o ./docs

# Help
./docgen --help
```

### Options

| Option | Default | Description |
|---|---|---|
| `repoPath` (positional) | `.` | Root directory of the repository to document |
| `-o`, `--output` | `.` | Directory where `README.md` and `ARCHITECTURE.md` are written |
| `-h`, `--help` | — | Print usage and exit |
| `-V`, `--version` | — | Print version and exit |

### Environment variables

| Variable | Required | Description |
|---|---|---|
| `ANTHROPIC_API_KEY` | Yes | Your Anthropic API key |

---

## Example output

Running `docgen` on itself produces something like:

```
Scanning repository: /home/user/docgen
Scanned 6 files (14 KB)

--- Generating README.md ---
# docgen
...

--- Generating ARCHITECTURE.md ---
## System Overview
...

Done!
  README.md       → /home/user/docgen/README.md
  ARCHITECTURE.md → /home/user/docgen/ARCHITECTURE.md
```

---

## How it works

```
repository
    │
    ▼
RepoScanner          walks the file tree, filters by extension,
                     enforces per-file (100 KB) and total (400 KB) limits,
                     returns file tree + concatenated contents
    │
    ▼
PromptBuilder        wraps the repo context in structured prompts
                     (one for README, one for ARCHITECTURE)
    │
    ▼
LLMClient            streams requests to claude-opus-4-8 via the
                     official Anthropic Java SDK (max 8 192 tokens each)
    │
    ▼
OutputWriter         writes the streamed responses to README.md
                     and ARCHITECTURE.md in the output directory
```

### Supported file types

**Source languages** (scanned for content):
Java · Kotlin · Scala · Python · JavaScript · TypeScript · JSX · TSX · Vue · Svelte · Go · Ruby · C# · PHP · C · C++ · Rust · Swift · Bash · SQL · R · Dart · Elixir · Clojure · Haskell · Elm · Terraform · HCL

**Configuration files** (always scanned first):
`pom.xml` · `build.gradle` · `package.json` · `go.mod` · `Cargo.toml` · `requirements.txt` · `pyproject.toml` · `Dockerfile` · `docker-compose.yml` · `tsconfig.json` · `Makefile` · and more

**Directories always skipped:**
`.git` · `node_modules` · `target` · `build` · `dist` · `.gradle` · `vendor` · `__pycache__` · `.next` · `.terraform` · `tmp` · `logs` · and others

---

## Project structure

```
docgen/
├── docgen                        # Bash wrapper (java -jar target/docgen.jar "$@")
├── pom.xml                       # Maven build — dependencies, shade plugin
└── src/main/java/com/docgen/
    ├── DocGenCLI.java            # Entry point, picocli command definition
    ├── RepoScanner.java          # File tree walker with filtering and budgeting
    ├── RepoContext.java          # Immutable record: tree + contents + stats
    ├── PromptBuilder.java        # Prompt templates for README and ARCHITECTURE
    ├── LLMClient.java            # Anthropic SDK streaming client
    └── OutputWriter.java         # Writes final files to disk
```

---

## Tech stack

| Layer | Choice |
|---|---|
| Language | Java 17 |
| Build | Maven 3 with Maven Shade Plugin (uber-JAR) |
| CLI parsing | picocli 4.7.6 |
| AI model | Claude Opus (`claude-opus-4-8`) |
| AI SDK | `anthropic-java` 2.34.0 (official Anthropic Java SDK) |

---

## Exit codes

| Code | Meaning |
|---|---|
| `0` | Success |
| `1` | Missing `ANTHROPIC_API_KEY` or unhandled exception |

---

## Contributing

1. Fork the repository and create a feature branch.
2. Run `mvn clean package` to make sure everything builds.
3. Open a pull request with a clear description of the change.

There are no automated tests yet — contributions that add a test suite are especially welcome.

---

## License

This project does not currently include a license file. Contact the author before using it in production.

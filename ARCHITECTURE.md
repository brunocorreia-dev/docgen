```mermaid
graph LR
    RepoContext["Repository Context"] -->|reads repo files|> FilesScanner
    FilesScanner["Scans repository files"] -->|filters out irrelevant files| SourceFiles
    SourceFiles["Sorts source files by size"] -->|gets config files| ConfigFiles
    ConfigFiles["Combines config files with source files"] -->|filters config files based on directory rules| OrderedFiles

    SourceFiles["Includes all relevant source files"] -->|includes config files| ContentBuilder
    ContentBuilder["Builds content string by including file contents"] -->|limits file size and content length| RepoContext

    LLMClient["LLM client for generating prompts"] -->|creates prompt| PromptBuilder
    PromptBuilder["Generates final prompt"] -->|sends prompt to Ollama API| LLMOutput
    OllamaAPI["Ollama AI service for generating text"] -->|processes prompt| generated_text

    RepoContext["Final Repository Context object"] -->|contains repo info and content| Output
```

## Components

1. `RepoContext`: Represents the context of a repository, including its files.
2. `FilesScanner`: Scans the repository files and filters out irrelevant ones.
3. `SourceFiles` and `ConfigFiles`: Source files and configuration files included in the output.
4. `ContentBuilder`: Builds the content string by combining all relevant file contents.
5. `LLMClient`: Sends prompts to the Ollama API and streams the generated text.
6. `PromptBuilder`: Generates the final prompt from repository context.

The Ollama API is an external component that takes a prompt from `PromptBuilder` and generates text using its AI service.

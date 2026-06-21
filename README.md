# AI-Powered Documentation Generator for Code Repositories
===========================================================

## Table of Contents
-----------------

* [Introduction](#introduction)
* [Features and Capabilities](#features-and-capabilities)
* [Tech Stack and Key Dependencies](#tech-stack-and-key-dependencies)
* [Prerequisites](#prerequisites)
* [Installation and Setup Instructions](#installation-and-setup-instructions)
* [Usage Examples with Real Code Snippets](#usage-examples-with-real-code-snippets)
* [Project Structure Overview](#project-structure-overview)
* [Configuration Options](#configuration-options)

## Introduction
------------

This project is an AI-powered documentation generator for code repositories. It leverages a local LLM (Ollama) model to generate comprehensive, professional README and architecture diagram files from a given repository.

## Features and Capabilities
-------------------------

### Code Generation

* Generates README file with project title, clear description, features, tech stack, key dependencies, configuration options, and user documentation.
* Creates architecture diagram by extracting relevant information from the source code.

### Customization Options

* Allows for customization of the generated files through a JSON configuration file.

## Tech Stack and Key Dependencies
---------------------------------

* LLM (Ollama) model: Provides the core AI intelligence for generating documentation.
* Java 8+ and Maven/Gradle build tools: Used for building and packaging the project.
* Scala and Kotlin support: For working with source code files from various programming languages.

## Prerequisites
--------------

To use this tool, you'll need:

* A local LLM (Ollama) server running at `http://localhost:11434`.
* Java 8+ or higher installed on your system.
* Maven or Gradle build tools installed on your system.

## Installation and Setup Instructions
--------------------------------------

1. Clone the repository using Git: `git clone https://github.com/your-username/repo.git`
2. Navigate to the project directory: `cd repo`
3. Install dependencies: `mvn clean install` (or Gradle equivalent)
4. Configure the LLM server URL: `mvn configure --set property.llam.model.url=http://localhost:11434` (or Gradle equivalent)

## Usage Examples with Real Code Snippets
-----------------------------------------

```bash
java -jar repo.jar -c path/to/config.json -r path/to/repository/directory
```

Example configuration file (`config.json`):
```json
{
  "title": "My Awesome Project",
  "description": "A brief summary of my project.",
  "features": [
    "Feature 1",
    "Feature 2"
  ],
  "techStack": {
    "programmingLangs": ["Java", "Kotlin"],
    "frameworks": ["Spring Boot"]
  }
}
```

Example usage:
```bash
java -jar repo.jar -c path/to/config.json -r src/main/java/path/to/repository/directory
```

## Project Structure Overview
---------------------------

The project consists of the following directories:

* `src/main/java`: Source code files from various programming languages.
* `src/main/resources`: Configuration files, including the JSON configuration file.
* `target`: Compiled class files and other generated resources.

## Configuration Options
----------------------

You can customize the generated README file by modifying the `config.json` file. The following options are available:

* `title`: Project title (string).
* `description`: Brief summary of the project (string).
* `features`: List of project features (array of strings).
* `techStack`: Object containing programming languages and frameworks used in the project (object).

To learn more about configuration options, refer to the `config.json` file documentation.
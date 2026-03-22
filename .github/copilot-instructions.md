# Copilot Instructions

This repository follows the DPC (Dans Plugins Community) conventions defined at
https://github.com/Dans-Plugins/dpc-conventions. Read those conventions before
making any changes.

## Technology Stack

- Language: Java
- Build tool: Maven
- Target platform: Spigot / Paper (Minecraft 1.13+)
- Test framework: JUnit

## Project Structure

- `src/main/java/dansplugins/rpsystem/` – Plugin source code
  - `commands/` – Command executors grouped by feature
  - `config/` – Configuration service
  - `cards/` – Character card data model
  - `storage/` – Persistence layer
  - `listeners/` – Bukkit event listeners
  - `placeholders/` – PlaceholderAPI expansion
  - `utils/` – Utility classes
- `src/main/resources/` – `plugin.yml` and `config.yml`
- `src/test/java/` – Unit tests

## Coding Conventions

- Follow the existing package structure (`dansplugins.rpsystem.*`) when adding new classes.
- Annotate every command executor and event listener with `@Override` where applicable.
- Never hard-code user-facing strings directly in Java; route them through the plugin's messaging helpers.
- Use `ConfigService` to read configuration values rather than accessing `getConfig()` directly.

## Contribution Workflow

- Branch from `develop` for all changes.
- Open a pull request against `develop`, not `main`.
- Reference the related GitHub issue in every pull request description.

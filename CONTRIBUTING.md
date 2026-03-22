# Contributing

## Thank You

Thank you for your interest in contributing to Medieval Roleplay Engine! This guide will help you get started.

## Links

- [Website](https://dansplugins.com)
- [Discord](https://discord.gg/xXtuAQ2)

## Requirements

- A GitHub account
- Git installed on your local machine
- A Java IDE or text editor
- A basic understanding of Java

## Getting Started

1. [Sign up for GitHub](https://github.com/signup) if you don't have an account.
2. Fork the repository by clicking **Fork** at the top right of the repo page.
3. Clone your fork: `git clone https://github.com/<your-username>/Medieval-Roleplay-Engine.git`
4. Open the project in your IDE.
5. Build the plugin: `./mvnw package`
   If you encounter errors, please open an issue.

## Identifying What to Work On

### Issues

Work items are tracked as [GitHub issues](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine/issues).

### Milestones

Issues are grouped into [milestones](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine/milestones) representing upcoming releases.

## Making Changes

1. Make sure an issue exists for the work. If not, create one.
2. Switch to `develop`: `git checkout develop`
3. Create a branch: `git checkout -b <branch-name>`
4. Make your changes.
5. Test your changes.
6. Commit: `git commit -m "Description of changes"`
7. Push: `git push origin <branch-name>`
8. Open a pull request against `develop`, link the related issue with `#<number>`.
9. Address review feedback.

## Testing

Run the unit tests with:

Linux: `./mvnw clean test`  
Windows: `.\mvnw.cmd clean test`

For manual testing, start a local Spigot server:

```
docker compose up
```

Or use the provided script:

```
./up.sh
```

## Questions

Ask in the [Discord server](https://discord.gg/xXtuAQ2).

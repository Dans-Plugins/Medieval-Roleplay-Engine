# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [2.0.0-SNAPSHOT-8-8-2026] – 2026-08-08

### Changed
- Medieval-Roleplay-Engine is now developed AI-first. Day-to-day feature work, grooming, review and maintenance run through AI agents working directly against this repository, with the maintainers setting direction and approving what lands. The major version bump marks that change in how the project is built — it is not a break in behaviour, configuration or stored data, and existing installations can upgrade in place. Released as `2.0.0-SNAPSHOT-8-8-2026`: the AI-first line has not yet been verified in live operation, and the dated snapshot designation stays until it has.

### Changed
- Documentation corrected against the source across `README.md`, `USER_GUIDE.md`, `COMMANDS.md` and `CONFIG.md`: `/local` and `/global` are documented as channel switches rather than message commands; `/title` is documented as renaming a held book and quill; `/rpconfig` is documented with its `show` and `set` sub-commands; `/card forcesave` and `/card forceload` are documented as taking no player argument; `/card help`, `/local hide`, `/local show`, `/lo hide`, `/lo show` and inline asterisk emotes are documented for the first time; the build command in `README.md` is corrected to `mvn package`; and the Mailboxes integration is described as an unused soft dependency rather than a requirement of the bird system.

### Fixed
- PlaceholderAPI expansion no longer throws a `NullPointerException` for `card_*` placeholders when the requested player has no character card yet; the expansion returns no value in that case, so PlaceholderAPI leaves the placeholder text untouched rather than the request failing.

## [1.13.0] – 2023-01-01

### Added
- Initial tracked release of Medieval Roleplay Engine.
- Character Cards: set and view character name, race, subculture, age, gender, and religion.
- Bird (mail) system for sending in-character messages to other players.
- Local roleplay chat (`/local`, `/rp`).
- Global OOC chat (`/global`, `/ooc`).
- Whisper chat (`/whisper`).
- Yell chat (`/yell`).
- Emote actions (`/emote`, `/me`).
- Local OOC chat (`/lo`).
- Dice rolling (`/roll`, `/dice`).
- Title display command (`/title`).
- PlaceholderAPI integration for character card data.
- Docker-based development server with hot-reload support.
- In-game configuration command (`/rpconfig`).

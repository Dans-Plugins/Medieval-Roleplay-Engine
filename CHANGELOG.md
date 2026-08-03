# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Fixed
- PlaceholderAPI expansion no longer throws a `NullPointerException` for `card_*` placeholders when the requested player has no character card yet; the placeholder now resolves to an empty value instead.

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

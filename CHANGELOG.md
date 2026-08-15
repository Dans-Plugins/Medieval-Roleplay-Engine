# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Fixed

- `emoteColor` and `logChat` are now backfilled with the same values a fresh install is given. A server upgrading from a version that predates either option was given `emoteColor: yellow` and `logChat: false`, while a fresh install was given `emoteColor: gray` and `logChat: true` — the values `CONFIG.md` documents. Both paths now write the documented values. Servers that already have either option set in `config.yml` are unaffected, since only missing options are backfilled.
- The `chatFeaturesEnabled` option is now probed with `isBoolean` rather than `isString` when backfilling defaults on upgrade, matching every other boolean option. The string probe never matched a boolean value, so the guard around it was never taken; the outcome is unchanged, because a value that is already set is never overwritten by a default.
- `CONTRIBUTING.md` and `.github/copilot-instructions.md` no longer direct contributors to a `develop` branch. That branch does not exist on the remote and `main` is the base every recent pull request has been merged into, so step 2 of the contributing guide failed outright for anyone following it as written.
- The `Dev Release` workflow now retries publishing the `dev` prerelease before giving up. The release and its tag have to be deleted and recreated for the tag to move to the new commit, and a transient API failure inside that window previously left the repository with no `dev` release at all until the workflow was re-run by hand. Each attempt now starts from a clean slate, and an exhausted retry fails loudly.

### Added

- A `Dev Release` workflow, which republishes a rolling `dev` prerelease of `main` on every non-documentation push. This is what Dan's Plugin Manager's experimental channel installs from: `/dpm get medievalroleplayengine --experimental` reads `releases/tags/dev`, so without it there is nothing for that command to download. The prerelease is unreleased, unreviewed code and is marked as such.

### Fixed

- `/rphelp` is usable by all players again. The command was gated on `rp.rphelp`, a node the plugin never registers, which Bukkit resolves as operator-only; the registered `rp.help` node (`default: true`) is now accepted, and the permission named in the rejection message was corrected to match. `rp.rphelp` continues to be accepted so that servers which already granted it are unaffected.
- Right-clicking another player to view their character card is usable by all players again. The interaction was gated on `rp.card.show.others`, `rp.card.*` and `rp.default`, none of which the plugin registers; the registered `rp.card.lookup` node (`default: true`) is now accepted, with the previous nodes retained.
- A player without permission to view a card by right-clicking is no longer left on the right-click cooldown for the rest of the server session. The cooldown entry was added before the permission check, while the task that clears it was only scheduled after the check passed, so rejected players accumulated in the set without bound.
- `/lo hide` and `/lo show` no longer broadcast the words `hide` and `show` as local OOC messages to nearby players. Both sub-commands fell through to the broadcast at the end of the command instead of returning after toggling visibility. Only `hide` or `show` on its own is treated as a sub-command, so a message that merely begins with either word — `/lo hide the treasure` — is still sent as an ordinary local OOC message.

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

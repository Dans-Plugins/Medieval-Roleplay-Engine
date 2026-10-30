# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Changed

- The usage-reporting "Details" link (startup notice, `config.yml` and the docs) now points at https://danielstephenson.dev/usage-reporting, a public page; the previous link led to a private repository and returned 404 for everyone. The vendored trace client is now 0.6.1, which carries the same link in the `plugins/trace/config.yml` header it writes. Details: https://github.com/Stephenson-Software/trace-client-java/releases/tag/0.6.1.
- Usage reporting now counts a `command` event only when a player runs the command; console, command-block and plugin-dispatched commands are no longer reported.

### Added

- An automated test suite, run by `mvn test` and by the existing `mvn package` step in CI, so a failing test now fails the build. The first tests lock down the `plugin.yml` contract: every permission node checked in code is registered or on an explicit knowingly-unregistered list (`rp.admin`, `rp.default`, `rp.card.show.others`, `rp.rphelp`), every registered node is checked somewhere, every registered command is dispatched by `CommandService` and vice versa, `rp.card.*` parents exactly the nine player card nodes and not `rp.card.forcesave` or `rp.card.forceload`, and the `USER_GUIDE.md` permission table matches what is registered. The `rp.card.*` guarantee is exercised through Bukkit's own `PermissibleBase` against a stub server, which is the check PR #335 could only establish with a throwaway class. This is the check that would have caught #321, #322 and #329 when they were introduced. `USER_GUIDE.md` gained the missing `rp.rphelp` entry under "Known Permission Discrepancies", which the new suite flagged on its first run.
- Unit tests for `ArgumentParser` and `ColorChecker.getColorByName`. They record current behavior without changing it: arguments are joined with single spaces and empty arguments are kept, and color names are matched only in all-lower-case or all-upper-case form (e.g. `dark_red` or `DARK_RED`), with any other name — including `Red` — resolving to white.
- Unit tests for `CharacterCard` and `CardRepository`. They record current behavior without changing it: a new card's placeholder defaults, how `CharacterCard.load` reads a save file (one value per line; missing lines keep the defaults, extra lines are ignored), and how cards are looked up by player UUID (a `null` UUID never matches). A non-numeric age or a malformed UUID in a save file threw out of `load` when these tests were added; see Fixed below.
- Unit tests for `/roll` and `/dice`. They record current behavior without changing it: any one of `rp.roll`, `rp.dice` or `rp.default` is accepted; no argument rolls `1d20`; a bare number is a die size and may carry a modifier (`12+1` is `1d12+1`); notation is case-insensitive and shown normalized (`02D06+0` is shown as `2d6`); only the first argument is read; the limits are 100 dice, d10000 and a modifier of ±10000, each with its own error message; a total may go below 1; and the result is shown to the roller and broadcast to other players within 25 blocks.
- Unit tests for the PlaceholderAPI expansion. They record current behavior without changing it: the expansion registers under the plugin's name from `plugin.yml` (`medievalroleplayengine` in lower case, as `USER_GUIDE.md` documents); `card_name`, `card_age`, `card_race`, `card_subculture`, `card_gender` and `card_religion` each resolve to their own card field, matched regardless of case; an unset field resolves to its default (e.g. `defaultName`, or `0` for age); and an unknown placeholder, a missing player or a player without a card resolves to nothing. Every placeholder listed in the `USER_GUIDE.md` table is also checked to resolve, so a renamed or removed placeholder now fails the build.
- Unit tests for how nearby chat is delivered (local chat, emotes, `/whisper`, `/yell`, local OOC chat, dice results and bird landing notices). They record current behavior without changing it: a message reaches online players in the sender's world whose straight-line (three-dimensional) distance is strictly less than the radius, so a player exactly at the radius does not hear it; the sender hears their own message except for dice results and bird landing notices; a player who has run `/local hide` misses roleplay messages but still gets local OOC chat, and `/lo hide` does the reverse; and with `logChat` on, each message is logged once as `[RP]` or `[OOC]` with the sender's display name, even when nobody was in range to hear it.
- Unit tests for how normal chat becomes local roleplay chat. They record current behavior without changing it: chat is left to the server when `chatFeaturesEnabled` is off or the player is not in local chat; a player in local chat who has run `/local hide` is told to type `/rp show` and their message goes nowhere; otherwise the message is sent as `CharacterName: "message"` within `localChatRadius` blocks and kept out of server chat. With an inline emote, the trimmed spoken remainder is sent first, then the emote in italics within `emoteRadius` blocks, and the remainder is dropped if empty. Only the first pair of asterisks is an emote, though every repeat of that same `*text*` is removed from the remainder. A lone asterisk is spoken as typed, and an empty pair (`**`) sent an emote of just the character name when these tests were added; see Fixed below.
- Unit tests for `/rpconfig set` and for how `config.yml` defaults are written. They record current behavior without changing it: an option must already be in `config.yml` and its name must match exactly, including case (`localchatradius` is reported as not found); `version` cannot be set; the radius, cooldown and `birdSpeed` options are stored as integers, and a non-numeric value for one of them threw out of the command when these tests were added (see Fixed below); the on/off options, `usage-reporting.enabled` included, store `true` for `true` in any case and `false` for anything else; every other option, the colors included, is stored as typed without checking it; and each change is saved to `config.yml` at once. A fresh install writes all 20 defaults; an upgrade fills in only the missing options, keeps the operator's values, moves the misspelled `neurtalAlertColor` to `neutralAlertColor` unless that is already set, and removes the obsolete integer `test` option. The bundled `usage-reporting` block is copied to a `config.yml` that has none and is left alone otherwise.

### Changed

- The vendored trace client is now 0.4.0, and every usage event now carries the plugin version, `command` events included; before, only `startup` did.
- The vendored trace client is now 0.3.0: `plugins/trace/config.yml` can carry a `tags:` block whose entries are added to every event reported by every plugin on the server (the release gates write `ci: "true"` there so that test-server boots are left out of real-installation figures). Nothing changes for a server without a `tags:` block. Details: https://github.com/Stephenson-Software/trace-client-java/releases/tag/0.3.0

### Fixed

- A character card save file with a non-numeric age or a malformed UUID no longer aborts card loading at startup. The bad file is reported on the console (`Skipping malformed character card file ...`) and only that card is skipped; every other card listed in `cards.txt` still loads. A non-numeric age in a legacy (`plugins/medieval-roleplay-engine/`) card file no longer throws either; that card is still migrated, with the fields read before the bad line (#373).
- A skipped malformed character card file is now renamed to `<uuid>.txt.malformed` (or `.malformed.1`, `.malformed.2`, ... if that name is taken), and the console message names the new file. Before, the file was left in place, so the default card created when that player next joined was saved over it and the readable fields were lost (#377).
- An empty pair of asterisks in local chat (for example `** Hello`), or a pair around only spaces, no longer sends an emote consisting of just the character name; only the spoken remainder is sent, and a message of just `**` sends nothing (#381).
- `/rpconfig set` with a value that is not a whole number for a radius, cooldown or `birdSpeed` option (for example `/rpconfig set localChatRadius far`) now tells the player `localChatRadius must be a whole number.` in the negative alert color and leaves the option unchanged. Before, the command threw, so the player saw the server's generic internal-error message and a stack trace was logged to the console (#384).
- The `plugin.yml` usage and description for `/roll` and `/dice` now describe dice notation (`/roll [notation]`, e.g. `2d6` or `1d20+5`) instead of `/roll [max]`, matching what the command accepts and what `COMMANDS.md` documents. Bukkit shows this usage when the command is run from the console.
- `COMMANDS.md` now states that `/emote` and `/me` are only available while `chatFeaturesEnabled` is `true`, as `CONFIG.md` and `USER_GUIDE.md` already did; the note previously covered only the chat-command section. The `USER_GUIDE.md` prerequisites now give 1.19.4 as the oldest supported Minecraft version, matching `minecraft-versions.json` and `README.md`, instead of 1.13.

## [1.15.1] – 2026-09-26

### Changed

- Version number only; the plugin is otherwise identical to 1.15.0.

## [1.15.0] – 2026-09-19

### Added

- The plugin now reports usage events — `startup` on enable, `command` on each of its commands — to the author's trace server so it is known which plugins are in use. Events carry the plugin name, the event name, and the plugin version or command name; nothing about players or the server. Reporting runs off the main thread, never delays a tick, drops silently when the server is unreachable, and is turned off with `usage-reporting.enabled: false` in `config.yml` (or `/rpconfig set usage-reporting.enabled false`, effective on the next restart). The default config carries the plugin's key, so reporting is active out of the box unless turned off — including on servers upgraded from a version before the `usage-reporting` block existed, whose `config.yml` may never gain the block: the plugin reads the defaults bundled in the jar for any key the file lacks.
- A `Dev Release` workflow, which republishes a rolling `dev` prerelease of `main` on every non-documentation push. This is what Dan's Plugin Manager's experimental channel installs from: `/dpm get medievalroleplayengine --experimental` reads `releases/tags/dev`, so without it there is nothing for that command to download. The prerelease is unreleased, unreviewed code and is marked as such.

### Changed

- Usage reporting is now disclosed on every startup: the plugin logs whether reporting is on — and what is sent, where, and how to turn it off — or why it is off. Two new opt-outs win over `usage-reporting.enabled`: `enabled: false` in `plugins/trace/config.yml`, a server-wide switch written by the first trace-reporting plugin to start, and the environment variables `TRACE_USAGE_REPORTING=off` / `DO_NOT_TRACK=1`. A `config.yml` that has no `usage-reporting` block gains one from the bundled defaults on enable, so the switch is visible on disk. `README.md` gained a "Usage reporting" section. Nothing about what is sent changed.
- The configuration defaults are now declared once in `ConfigService` and shared by the fresh-install and upgrade paths, instead of being written out twice. The two copies had already drifted apart once — the `emoteColor` and `logChat` divergence fixed below — and nothing failed when only one copy was edited. No option's value changes as a result on either path. The one visible difference is that upgrading a `config.yml` now writes the options back in the same fixed order a fresh install uses, rather than an order that depended on which options were already present in the file.

### Fixed

- `emoteColor` and `logChat` are now backfilled with the same values a fresh install is given. A server upgrading from a version that predates either option was given `emoteColor: yellow` and `logChat: false`, while a fresh install was given `emoteColor: gray` and `logChat: true` — the values `CONFIG.md` documents. Both paths now write the documented values. Servers that already have either option set in `config.yml` are unaffected, since only missing options are backfilled.
- The `chatFeaturesEnabled` option is now probed with `isBoolean` rather than `isString` when backfilling defaults on upgrade, matching every other boolean option. The string probe never matched a boolean value, so the guard around it was never taken; the outcome is unchanged, because a value that is already set is never overwritten by a default.
- `CONTRIBUTING.md` and `.github/copilot-instructions.md` no longer direct contributors to a `develop` branch. That branch does not exist on the remote and `main` is the base every recent pull request has been merged into, so step 2 of the contributing guide failed outright for anyone following it as written.
- `rp.card.*` is now registered in `plugin.yml` as a parent permission whose children are the nine `rp.card.<x>` player nodes it is accepted in place of. Granting it now grants those nodes through Bukkit itself, rather than only on permissions plugins that expand wildcards on their own; the visible difference is that `rp.card.*` once again covers a card node the server has explicitly revoked, which it previously did not. It is registered with `default: op`, the value Bukkit already resolved it to while it was unregistered, so no server's effective permissions change on their own. `rp.card.forcesave` and `rp.card.forceload` are deliberately excluded from the children, because neither is gated on `rp.card.*` in code.
- The `Dev Release` workflow now retries publishing the `dev` prerelease before giving up. The release and its tag have to be deleted and recreated for the tag to move to the new commit, and a transient API failure inside that window previously left the repository with no `dev` release at all until the workflow was re-run by hand. Each attempt now starts from a clean slate, and an exhausted retry fails loudly.
- `/rphelp` is usable by all players again. The command was gated on `rp.rphelp`, a node the plugin never registers, which Bukkit resolves as operator-only; the registered `rp.help` node (`default: true`) is now accepted, and the permission named in the rejection message was corrected to match. `rp.rphelp` continues to be accepted so that servers which already granted it are unaffected.
- Right-clicking another player to view their character card is usable by all players again. The interaction was gated on `rp.card.show.others`, `rp.card.*` and `rp.default`, none of which the plugin registers; the registered `rp.card.lookup` node (`default: true`) is now accepted, with the previous nodes retained.
- A player without permission to view a card by right-clicking is no longer left on the right-click cooldown for the rest of the server session. The cooldown entry was added before the permission check, while the task that clears it was only scheduled after the check passed, so rejected players accumulated in the set without bound.
- `/lo hide` and `/lo show` no longer broadcast the words `hide` and `show` as local OOC messages to nearby players. Both sub-commands fell through to the broadcast at the end of the command instead of returning after toggling visibility. Only `hide` or `show` on its own is treated as a sub-command, so a message that merely begins with either word — `/lo hide the treasure` — is still sent as an ordinary local OOC message.

## [2.0.0-SNAPSHOT-8-8-2026] – 2026-08-08

### Changed
- Medieval-Roleplay-Engine is now developed AI-first. Day-to-day feature work, grooming, review and maintenance run through AI agents working directly against this repository, with the maintainers setting direction and approving what lands. The major version bump marks that change in how the project is built — it is not a break in behaviour, configuration or stored data, and existing installations can upgrade in place. Released as `2.0.0-SNAPSHOT-8-8-2026`: the AI-first line has not yet been verified in live operation, and the dated snapshot designation stays until it has.
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

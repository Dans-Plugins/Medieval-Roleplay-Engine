# User Guide

## Prerequisites

- A Spigot or Paper Minecraft server (1.13 or later).
- The Medieval Roleplay Engine jar placed in your server's `plugins/` folder.
- *(Optional)* [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for placeholder support.
- *(Optional)* [Medieval Factions](https://github.com/Dans-Plugins/Medieval-Factions) for faction integration.
- *(Optional)* [Mailboxes](https://github.com/Dans-Plugins/Mailboxes) — declared as a soft dependency. The bird (mail) system does not require it; birds are delivered by the plugin itself.

## First Steps

After installing the plugin and restarting your server:

1. Join the server as a player.
2. Run `/card` to view your character card. Your character name defaults to your Minecraft username.
3. Use `/card name <name>` to set your character's name.
4. Use `/rphelp` to see a list of all available commands.

## Common Scenarios

### Setting Up Your Character Card

Your character card holds your roleplay identity:

```
/card name <name>         – Set your character's name
/card race <race>         – Set your character's race
/card subculture <subculture> – Set your character's subculture
/card age <age>           – Set your character's age
/card gender <gender>     – Set your character's gender
/card religion <religion> – Set your character's religion
/card                     – View your own card
/card lookup <player>     – View another player's card
```

### Viewing a Card by Right-Clicking

While `rightClickToViewCard` is `true`, right-clicking another player shows their character card, subject to a two-second cooldown per viewer. This currently requires operator status — see [issue #322](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine/issues/322).

### Using Roleplay Chat

Medieval Roleplay Engine splits its chat commands into two kinds.

**Channel switches** change where your *normal* chat goes. They take no message of their own:

- **`/local`** (or `/rp`) – Switches you into local roleplay chat. From then on, anything you type in normal chat is delivered as roleplay chat to players within `localChatRadius` blocks, styled as `CharacterName: "your message"`, and does not reach server chat.
- **`/global`** (or `/ooc`) – Switches you back out of local roleplay chat, so that what you type goes to ordinary server chat again.
- **`/local hide`** and **`/local show`** – Hide or re-show incoming local roleplay chat. While hidden you cannot talk in local chat either.

**One-off message commands** send a single message and leave your channel unchanged:

- **Whisper** (`/whisper <message>`) – Sends a message visible only to players within `whisperChatRadius` blocks, and tells you how many players heard it.
- **Yell** (`/yell <message>`) – Sends a message visible to players within `yellChatRadius` blocks.
- **Emote** (`/emote <action>` or `/me <action>`) – Performs a roleplay action visible within `emoteRadius` blocks.
- **Local OOC** (`/lo <message>`) – Out-of-character message to players within `localOOCChatRadius` blocks.

All of the above require `chatFeaturesEnabled` to be `true`.

### Emoting Inline While in Local Chat

While you are in local roleplay chat, text wrapped in asterisks is split out of your message and sent as a separate emote:

```
*draws his sword* Stand back!
```

is delivered to nearby players as two messages: the spoken remainder (`CharacterName: "Stand back!"`, within `localChatRadius` blocks), then the emote (`CharacterName draws his sword`, within `emoteRadius` blocks). If nothing is left once the asterisks are removed, only the emote is sent. Only the first pair of asterisks in a message is treated this way.

### Sending a Bird (Mail)

Use `/bird <player> <message>` to send a private in-character message to another player. Both of you must be online and in the same world. The bird takes `distance / birdSpeed` seconds to arrive, and you can only have one bird in flight at a time. Players near the recipient are told that a bird landed, but not what it said.

### Rolling Dice

Use `/roll <notation>` or `/dice <notation>` to roll dice using standard dice notation. Running either command with no argument rolls a single d20. The result is shown to you and to players within a fixed radius of 25 blocks.

Supported formats:
- `/roll 20` — roll a single d20 (legacy)
- `/roll d20` — roll a single d20
- `/roll 2d6` — roll two d6s and sum the results
- `/roll 1d20+5` — roll a d20 and add a modifier of +5
- `/roll 3d8-2` — roll three d8s and subtract 2

### Using Placeholders

If [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) is installed, Medieval Roleplay Engine registers an expansion with the identifier `medievalroleplayengine`, exposing character card data to any plugin that supports placeholders (e.g. TAB, essentials-style scoreboards, chat formatters). Placeholders resolve for the player they're evaluated for; every player is assigned a character card automatically on join, so these placeholders are populated for any currently online player.

| Placeholder | Description |
|---|---|
| `%medievalroleplayengine_card_name%` | The player's character name |
| `%medievalroleplayengine_card_age%` | The player's character age |
| `%medievalroleplayengine_card_race%` | The player's character race |
| `%medievalroleplayengine_card_subculture%` | The player's character subculture |
| `%medievalroleplayengine_card_gender%` | The player's character gender |
| `%medievalroleplayengine_card_religion%` | The player's character religion |

**Example (TAB plugin `config.yml`):**

```yaml
tablist-name-formatting:
  enabled: true
  name-format: "%medievalroleplayengine_card_name%"
```

## Permissions

The table below lists the nodes registered by the plugin in `plugin.yml`. Every node is checked with its own name, except where noted beneath the table.

| Permission | Default | Description |
|---|---|---|
| `rp.bird` | `true` | Send a bird to another player |
| `rp.card.show` | `true` | View your own character card |
| `rp.card.lookup` | `true` | View another player's character card |
| `rp.card.help` | `true` | View character card help |
| `rp.card.name` | `true` | Set your character's name |
| `rp.card.race` | `true` | Set your character's race |
| `rp.card.subculture` | `true` | Set your character's subculture |
| `rp.card.religion` | `true` | Set your character's religion |
| `rp.card.age` | `true` | Set your character's age |
| `rp.card.gender` | `true` | Set your character's gender |
| `rp.local` | `true` | Use local roleplay chat |
| `rp.rp` | `true` | Alias for local chat |
| `rp.global` | `true` | Use global OOC chat |
| `rp.ooc` | `true` | Alias for global OOC chat |
| `rp.emote` | `true` | Use the emote command |
| `rp.me` | `true` | Alias for the emote command |
| `rp.roll` | `true` | Roll a die |
| `rp.dice` | `true` | Alias for roll |
| `rp.title` | `true` | Use the title command |
| `rp.yell` | `true` | Use yell chat |
| `rp.whisper` | `true` | Use whisper chat |
| `rp.help` | `true` | View the help message |
| `rp.localOOC` | `true` | Use local OOC chat |
| `rp.card.forcesave` | `op` | Force-save all cards to disk (admin) |
| `rp.card.forceload` | `op` | Force-load all cards from disk (admin) |
| `rp.config` | `op` | View and change plugin configuration in-game |

### Known Permission Discrepancies

- **`rp.help` is not the node `/rphelp` checks.** `/rphelp` checks for `rp.rphelp`, which the plugin never registers, so the command is operator-only regardless of how `rp.help` is granted — see [issue #321](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine/issues/321).
- **Right-clicking to view a card has no registered node.** It checks for `rp.card.show.others`, `rp.card.*` and `rp.default`, none of which the plugin registers, so it is operator-only — see [issue #322](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine/issues/322).
- **`rp.admin` and `rp.default` are accepted but unregistered.** Several commands accept `rp.admin` (admin commands) or `rp.default` (most player commands) as an alternative to their own node. Neither is registered in `plugin.yml`, so both must be granted explicitly through a permissions plugin to have any effect.

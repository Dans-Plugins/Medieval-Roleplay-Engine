# User Guide

## Prerequisites

- A Spigot or Paper Minecraft server (1.13 or later).
- The Medieval Roleplay Engine jar placed in your server's `plugins/` folder.
- *(Optional)* [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for placeholder support.
- *(Optional)* [Medieval Factions](https://github.com/Dans-Plugins/Medieval-Factions) for faction integration.
- *(Optional)* [Mailboxes](https://github.com/Dans-Plugins/Mailboxes) for the bird (mail) system.

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

### Using Roleplay Chat

Medieval Roleplay Engine provides several chat channels:

- **Local chat** (`/local` or `/rp`) – Sends a message visible only to nearby players (within `localChatRadius` blocks).
- **Global chat** (`/global` or `/ooc`) – Sends a message visible to all players on the server.
- **Whisper** (`/whisper <message>`) – Sends a message visible only to players within `whisperChatRadius` blocks.
- **Yell** (`/yell <message>`) – Sends a message visible to players within `yellChatRadius` blocks.
- **Emote** (`/emote <action>` or `/me <action>`) – Performs a roleplay action visible within `emoteRadius` blocks.
- **Local OOC** (`/lo <message>`) – Out-of-character message in local range.

### Sending a Bird (Mail)

Use `/bird <player> <message>` to send a private in-character message to another player. The bird travels at the configured `birdSpeed` and delivers the message when it arrives.

### Rolling Dice

Use `/roll <notation>` or `/dice <notation>` to roll dice using standard dice notation. The result is shown to nearby players.

Supported formats:
- `/roll 20` — roll a single d20 (legacy)
- `/roll d20` — roll a single d20
- `/roll 2d6` — roll two d6s and sum the results
- `/roll 1d20+5` — roll a d20 and add a modifier of +5
- `/roll 3d8-2` — roll three d8s and subtract 2

### Using Placeholders

If [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) is installed, Medieval Roleplay Engine registers an expansion with the identifier `medievalroleplayengine`, exposing character card data to any plugin that supports placeholders (e.g. TAB, essentials-style scoreboards, chat formatters). Placeholders resolve for the player they're evaluated for and return an empty result if that player has no character card yet.

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
| `rp.card.forcesave` | `op` | Force-save a player's card (admin) |
| `rp.card.forceload` | `op` | Force-load a player's card (admin) |
| `rp.config` | `op` | Change plugin configuration in-game |

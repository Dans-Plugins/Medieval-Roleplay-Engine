# Configuration Guide

All options are set in the `plugins/MedievalRoleplayEngine/config.yml` file. Options are listed in the order they appear in the default configuration.

Options can also be inspected and changed in-game by an operator with `/rpconfig show` and `/rpconfig set <option> <value>`; changes made that way are written straight back to `config.yml`.

The defaults below are those written when the plugin creates `config.yml` for the first time. The same values are backfilled for any option that is missing when an existing `config.yml` is carried across a plugin upgrade, so a fresh install and an upgraded server end up with the same defaults.

---

## usage-reporting

**Type:** section  
**Default:**

```yaml
usage-reporting:
  enabled: true
  endpoint: https://trace.danielstephenson.dev
  key: "2LADE-cE7kH1bYHZ0OUQrzEZOrdhpKBg1GKdw3f1FJ0"
```

**Description:** When the plugin is enabled, and each time one of its commands is used, a small event is sent to the author's [trace](https://github.com/Stephenson-Software/trace-client-java) server so it is known which plugins are actually in use. An event carries the plugin's name, the event name (`startup` or `command`), and either the plugin version or the command name — nothing about players, the world, or the server. Sending happens off the main thread, never delays a tick, and is dropped silently if the server cannot be reached.

| Option | Default | Description |
|--------|---------|-------------|
| `usage-reporting.enabled` | `true` | Whether the plugin reports usage events. Set to `false` to turn it off. Also settable in-game with `/rpconfig set usage-reporting.enabled false`; either way the change takes effect on the next restart. |
| `usage-reporting.endpoint` | `https://trace.danielstephenson.dev` | The trace server events are sent to. |
| `usage-reporting.key` | the plugin's key | Identifies this plugin to the trace server so reports are attributed to it. Not a secret: it ships in the default config and can only report as MedievalRoleplayEngine. Empty means reporting is off regardless of `enabled`. |

Unlike every other option, this block is not backfilled into an existing `config.yml` on upgrade unless the plugin version has changed; a `config.yml` that lacks it still resolves the values above from the defaults bundled in the jar, so reporting is active on upgraded servers too until `enabled` is set to `false`.

**Example:**

```yaml
usage-reporting:
  enabled: false
```

---

## version

**Type:** string  
**Default:** *(set automatically by the plugin)*  
**Description:** Tracks the plugin version that last wrote this config file. Do not change this manually.

---

## localChatRadius

**Type:** integer  
**Default:** `25`  
**Description:** The radius in blocks within which players can see local roleplay chat — that is, normal chat typed by a player who has switched into local chat with `/local` or `/rp`.

**Example:**

```yaml
localChatRadius: 30
```

---

## whisperChatRadius

**Type:** integer  
**Default:** `2`  
**Description:** The radius in blocks within which players can see whispered messages (`/whisper`).

**Example:**

```yaml
whisperChatRadius: 3
```

---

## yellChatRadius

**Type:** integer  
**Default:** `50`  
**Description:** The radius in blocks within which players can see yelled messages (`/yell`).

**Example:**

```yaml
yellChatRadius: 75
```

---

## emoteRadius

**Type:** integer  
**Default:** `25`  
**Description:** The radius in blocks within which players can see emote actions, whether sent with `/emote` / `/me` or written inline between asterisks while in local chat.

**Example:**

```yaml
emoteRadius: 20
```

---

## changeNameCooldown

**Type:** integer  
**Default:** `300`  
**Description:** The cooldown in seconds before a player can change their character's name again using `/card name`.

**Example:**

```yaml
changeNameCooldown: 600
```

---

## localChatColor

**Type:** string  
**Default:** `gray`  
**Description:** The color used for local roleplay chat messages. Accepts Minecraft color names (e.g. `white`, `yellow`, `green`, `aqua`, `red`, `blue`, `gray`, `dark_gray`, etc.).

**Example:**

```yaml
localChatColor: white
```

---

## whisperChatColor

**Type:** string  
**Default:** `blue`  
**Description:** The color used for whispered messages.

**Example:**

```yaml
whisperChatColor: dark_aqua
```

---

## yellChatColor

**Type:** string  
**Default:** `red`  
**Description:** The color used for yelled messages.

**Example:**

```yaml
yellChatColor: dark_red
```

---

## emoteColor

**Type:** string  
**Default:** `gray`  
**Description:** The color used for emote actions.

**Example:**

```yaml
emoteColor: yellow
```

---

## rightClickToViewCard

**Type:** boolean  
**Default:** `true`  
**Description:** When `true`, players can right-click another player to view their character card, subject to a two-second cooldown per viewer. Viewing requires the `rp.card.lookup` permission, which is granted to everyone by default.

**Example:**

```yaml
rightClickToViewCard: false
```

---

## localOOCChatRadius

**Type:** integer  
**Default:** `25`  
**Description:** The radius in blocks within which players can see local out-of-character messages (`/lo`).

**Example:**

```yaml
localOOCChatRadius: 20
```

---

## localOOCChatColor

**Type:** string  
**Default:** `gray`  
**Description:** The color used for local OOC chat messages.

**Example:**

```yaml
localOOCChatColor: dark_gray
```

---

## positiveAlertColor

**Type:** string  
**Default:** `green`  
**Description:** The color used for positive feedback messages (e.g. success confirmations).

**Example:**

```yaml
positiveAlertColor: green
```

---

## neutralAlertColor

**Type:** string  
**Default:** `aqua`  
**Description:** The color used for neutral informational messages.

**Example:**

```yaml
neutralAlertColor: aqua
```

---

## negativeAlertColor

**Type:** string  
**Default:** `red`  
**Description:** The color used for error or failure messages.

**Example:**

```yaml
negativeAlertColor: dark_red
```

---

## chatFeaturesEnabled

**Type:** boolean  
**Default:** `true`  
**Description:** When `false`, all chat-related commands (`/local`, `/rp`, `/global`, `/ooc`, `/whisper`, `/yell`, `/emote`, `/me`, `/lo`) stop responding, and normal chat is no longer rerouted into local roleplay chat. Card, bird, dice, title, help and config commands are unaffected.

**Example:**

```yaml
chatFeaturesEnabled: false
```

---

## debugMode

**Type:** boolean  
**Default:** `false`  
**Description:** When `true`, the plugin outputs additional debug information to the server console.

**Example:**

```yaml
debugMode: true
```

---

## birdSpeed

**Type:** integer  
**Default:** `20`  
**Description:** The speed (in blocks per second) at which birds travel when delivering messages via `/bird`. Higher values mean faster delivery, since delivery delay is calculated as `distance / birdSpeed`.

**Example:**

```yaml
birdSpeed: 10
```

---

## logChat

**Type:** boolean  
**Default:** `true`  
**Description:** When `true`, messages the plugin broadcasts to nearby players are logged to the server console, tagged `[RP]` for roleplay chat (local chat, whisper, yell, emote, dice results, bird landing notices) or `[OOC]` for local out-of-character chat. Messages sent privately to a single player — card views, bird contents and command feedback — are not logged.

**Example:**

```yaml
logChat: true
```

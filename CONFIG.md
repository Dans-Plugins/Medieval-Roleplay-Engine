# Configuration Guide

All options are set in the `plugins/MedievalRoleplayEngine/config.yml` file. Options are listed in the order they appear in the default configuration.

---

## version

**Type:** string  
**Default:** *(set automatically by the plugin)*  
**Description:** Tracks the plugin version that last wrote this config file. Do not change this manually.

---

## localChatRadius

**Type:** integer  
**Default:** `25`  
**Description:** The radius in blocks within which players can see local roleplay chat (`/local`, `/rp`).

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
**Description:** The radius in blocks within which players can see emote actions (`/emote`, `/me`).

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
**Description:** When `true`, players can right-click another player to view their character card.

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
**Description:** When `false`, all chat-related features (local, global, whisper, yell, emote) are disabled.

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
**Description:** When `true`, all roleplay chat messages are logged to the server console.

**Example:**

```yaml
logChat: true
```

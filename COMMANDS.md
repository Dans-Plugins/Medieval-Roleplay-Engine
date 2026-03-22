# Commands Reference

## Character Card Commands

### /card

**Description:** View your own character card.  
**Permission:** `rp.card.show`  
**Usage:** `/card`  
**Example:** `/card`

### /card lookup \<player\>

**Description:** View the character card of another player.  
**Permission:** `rp.card.lookup`  
**Usage:** `/card lookup <player>`  
**Example:** `/card lookup Steve`

### /card name \<name\>

**Description:** Set your character's name. Subject to a cooldown configured by `changeNameCooldown`.  
**Permission:** `rp.card.name`  
**Usage:** `/card name <name>`  
**Example:** `/card name Aldric`

### /card race \<race\>

**Description:** Set your character's race.  
**Permission:** `rp.card.race`  
**Usage:** `/card race <race>`  
**Example:** `/card race Human`

### /card subculture \<subculture\>

**Description:** Set your character's subculture.  
**Permission:** `rp.card.subculture`  
**Usage:** `/card subculture <subculture>`  
**Example:** `/card subculture Nordic`

### /card age \<age\>

**Description:** Set your character's age.  
**Permission:** `rp.card.age`  
**Usage:** `/card age <age>`  
**Example:** `/card age 30`

### /card gender \<gender\>

**Description:** Set your character's gender.  
**Permission:** `rp.card.gender`  
**Usage:** `/card gender <gender>`  
**Example:** `/card gender Male`

### /card religion \<religion\>

**Description:** Set your character's religion.  
**Permission:** `rp.card.religion`  
**Usage:** `/card religion <religion>`  
**Example:** `/card religion Northism`

### /card forcesave \<player\>

**Description:** Force-save a player's character card (admin only).  
**Permission:** `rp.card.forcesave`  
**Usage:** `/card forcesave <player>`  
**Example:** `/card forcesave Steve`

### /card forceload \<player\>

**Description:** Force-load a player's character card from storage (admin only).  
**Permission:** `rp.card.forceload`  
**Usage:** `/card forceload <player>`  
**Example:** `/card forceload Steve`

---

## Chat Commands

### /local \[message\] | /rp \[message\]

**Description:** Send a message in local roleplay chat, visible to players within `localChatRadius` blocks. Running without a message toggles local chat mode. Use `/local hide` to hide incoming local chat.  
**Permission:** `rp.local` or `rp.rp`  
**Usage:** `/local <message>` or `/rp <message>`  
**Example:** `/local Good morrow, traveller!`

### /global \[message\] | /ooc \[message\]

**Description:** Send a message in global out-of-character chat, visible to all players.  
**Permission:** `rp.global` or `rp.ooc`  
**Usage:** `/global <message>` or `/ooc <message>`  
**Example:** `/global Anyone up for a dungeon run?`

### /whisper \<message\>

**Description:** Send a whispered message visible only to players within `whisperChatRadius` blocks.  
**Permission:** `rp.whisper`  
**Usage:** `/whisper <message>`  
**Example:** `/whisper Meet me at the tavern tonight.`

### /yell \<message\>

**Description:** Send a yelled message visible to players within `yellChatRadius` blocks.  
**Permission:** `rp.yell`  
**Usage:** `/yell <message>`  
**Example:** `/yell Guards! Intruder!`

### /lo \<message\>

**Description:** Send an out-of-character message in local range.  
**Permission:** `rp.localOOC`  
**Usage:** `/lo <message>`  
**Example:** `/lo brb one sec`

---

## Emote Commands

### /emote \<action\> | /me \<action\>

**Description:** Perform a roleplay action visible to players within `emoteRadius` blocks.  
**Permission:** `rp.emote` or `rp.me`  
**Usage:** `/emote <action>` or `/me <action>`  
**Example:** `/me bows gracefully`

---

## Dice Commands

### /roll \<max\> | /dice \<max\>

**Description:** Roll a random number from 1 to `<max>`. The result is broadcast to nearby players.  
**Permission:** `rp.roll` or `rp.dice`  
**Usage:** `/roll <max>` or `/dice <max>`  
**Example:** `/roll 20`

---

## Bird Commands

### /bird \<player\> \<message\>

**Description:** Send a bird (in-character mail) to another player. Requires the Mailboxes companion plugin.  
**Permission:** `rp.bird`  
**Usage:** `/bird <player> <message>`  
**Example:** `/bird Steve I shall arrive at dawn.`

---

## Title Commands

### /title \<message\>

**Description:** Display a title message on screen.  
**Permission:** `rp.title`  
**Usage:** `/title <message>`  
**Example:** `/title The battle begins!`

---

## Help Commands

### /rphelp

**Description:** Display the list of available commands.  
**Permission:** `rp.help`  
**Usage:** `/rphelp`

---

## Admin Commands

### /rpconfig \<option\> \<value\>

**Description:** View or change plugin configuration options in-game.  
**Permission:** `rp.config`  
**Usage:** `/rpconfig <option> <value>`  
**Example:** `/rpconfig localChatRadius 30`

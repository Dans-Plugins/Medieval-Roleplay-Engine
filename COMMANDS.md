# Commands Reference

## Character Card Commands

### /card

**Description:** View your own character card.  
**Permission:** `rp.card.show`  
**Usage:** `/card`  
**Example:** `/card`

### /card help

**Description:** Show the list of character card sub-commands.  
**Permission:** `rp.card.help`  
**Usage:** `/card help`  
**Example:** `/card help`

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

### /card forcesave

**Description:** Force-save every character card to disk (admin only). This command takes no arguments; all cards are written, not a single player's.  
**Permission:** `rp.card.forcesave`  
**Usage:** `/card forcesave`  
**Example:** `/card forcesave`

### /card forceload

**Description:** Force-load every character card from storage (admin only). This command takes no arguments; all cards are re-read, not a single player's.  
**Permission:** `rp.card.forceload`  
**Usage:** `/card forceload`  
**Example:** `/card forceload`

---

## Chat Commands

`/local` and `/global` are channel switches, not message commands: they change where your *normal* chat goes. `/whisper`, `/yell` and `/lo` are one-off message commands and do not change your channel. Every command in this section is only available while `chatFeaturesEnabled` is `true`.

### /local | /rp

**Description:** Switch your normal chat into local roleplay chat. Once switched, anything you type in normal chat is delivered as roleplay chat to players within `localChatRadius` blocks instead of going to server chat. Any message text passed to the command itself is ignored — the command only switches channels. Use `/global` or `/ooc` to switch back.  
**Permission:** `rp.local` or `rp.rp`  
**Usage:** `/local` or `/rp`  
**Example:** `/local`

### /local hide | /local show

**Description:** Hide or re-show incoming roleplay chat. Hiding suppresses everything delivered on the roleplay channel, not just local chat: whispers, yells, emotes, dice results and bird landing notices are all withheld while hidden. You also cannot talk in local chat while hidden; attempting to do so prompts you to run `/rp show`.  
**Permission:** `rp.local` or `rp.rp`  
**Usage:** `/local hide` or `/local show`  
**Example:** `/rp hide`

### /global | /ooc

**Description:** Switch your normal chat back out of local roleplay chat, so that what you type goes to ordinary server chat again. Any message text passed to the command itself is ignored — the command only switches channels.  
**Permission:** `rp.global` or `rp.ooc`  
**Usage:** `/global` or `/ooc`  
**Example:** `/ooc`

### /whisper \<message\>

**Description:** Send a single whispered message visible only to players within `whisperChatRadius` blocks. The sender is told how many players heard it.  
**Permission:** `rp.whisper`  
**Usage:** `/whisper <message>`  
**Example:** `/whisper Meet me at the tavern tonight.`

### /yell \<message\>

**Description:** Send a single yelled message visible to players within `yellChatRadius` blocks.  
**Permission:** `rp.yell`  
**Usage:** `/yell <message>`  
**Example:** `/yell Guards! Intruder!`

### /lo \<message\>

**Description:** Send a single out-of-character message to players within `localOOCChatRadius` blocks.  
**Permission:** `rp.localOOC`  
**Usage:** `/lo <message>`  
**Example:** `/lo brb one sec`

### /lo hide | /lo show

**Description:** Hide or re-show incoming local out-of-character chat. Only `hide` or `show` on its own is treated as a sub-command; a longer message that happens to begin with either word, such as `/lo hide the treasure`, is sent as an ordinary local OOC message.  
**Permission:** `rp.localOOC`  
**Usage:** `/lo hide` or `/lo show`  
**Example:** `/lo hide`

---

## Emote Commands

### /emote \<action\> | /me \<action\>

**Description:** Perform a roleplay action visible to players within `emoteRadius` blocks.  
**Permission:** `rp.emote` or `rp.me`  
**Usage:** `/emote <action>` or `/me <action>`  
**Example:** `/me bows gracefully`

---

## Dice Commands

### /roll \<notation\> | /dice \<notation\>

**Description:** Roll dice using standard dice notation. Running the command with no argument rolls a single d20. The result is broadcast to players within a fixed radius of 25 blocks, which is not affected by `localChatRadius`. Up to 100 dice, a die size up to d10000, and a modifier of ±10000 are accepted.  
**Permission:** `rp.roll` or `rp.dice`  
**Usage:** `/roll <notation>` or `/dice <notation>`  
**Notation:** `[N]d<M>[+|-K]` where `N` = number of dice (default 1), `M` = die size, `K` = modifier  
**Examples:**
- `/roll 20` — roll a single d20 (legacy form)
- `/roll d20` — roll a single d20
- `/roll 2d6` — roll two d6s and sum them
- `/roll 1d20+5` — roll a d20 and add 5
- `/roll 3d8-2` — roll three d8s and subtract 2

---

## Bird Commands

### /bird \<player\> \<message\>

**Description:** Send a bird carrying an in-character message to another player. Both players must be online and in the same world. The bird takes `distance / birdSpeed` seconds to arrive, and only one bird per sender may be in flight at a time.  
**Permission:** `rp.bird`  
**Usage:** `/bird <player> <message>`  
**Example:** `/bird Steve I shall arrive at dawn.`

---

## Title Commands

### /title \<title\>

**Description:** Rename the book and quill held in your main hand. Nothing is displayed on screen; the title is applied as the item's display name. An error is shown if a book and quill is not being held.  
**Permission:** `rp.title`  
**Usage:** `/title <title>`  
**Example:** `/title The Chronicle of Aldric`

---

## Help Commands

### /rphelp

**Description:** Display the list of available commands. The list omits the chat commands while `chatFeaturesEnabled` is `false`.  
**Permission:** `rp.help`  
**Usage:** `/rphelp`

---

## Admin Commands

### /rpconfig show

**Description:** List every configuration option and its current value in chat.  
**Permission:** `rp.config`  
**Usage:** `/rpconfig show`  
**Example:** `/rpconfig show`

### /rpconfig set \<option\> \<value\>

**Description:** Change a plugin configuration option in-game and save it to `config.yml`. Only options that already exist in the configuration can be set, and `version` cannot be changed.  
**Permission:** `rp.config`  
**Usage:** `/rpconfig set <option> <value>`  
**Example:** `/rpconfig set localChatRadius 30`

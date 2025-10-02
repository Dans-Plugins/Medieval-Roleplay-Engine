# Visual Examples

## Example 1: Default Behavior (All Fields Visible)

### Configuration
```yaml
cardShowRace: true
cardShowSubculture: true
cardShowReligion: true
cardShowAge: true
cardShowGender: true
cardLabelRace: "Race"
cardLabelSubculture: "Subculture"
cardLabelReligion: "Religion"
cardLabelAge: "Age"
cardLabelGender: "Gender"
```

### Character Card Display
```
----------
Character Card of Player123
----------
Name: John Smith
Race: Human
Subculture: European
Age: 25
Gender: Male
Religion: Christian
```

### Help Command Output
```
== Character Card Commands ==
/card - View your character card.
/card lookup (player) - View the character card of a specific player.
/card name (name) - Change your character's name.
/card race (race) - Change your character's race.
/card subculture (subculture) - Change your character's subculture.
/card age (age) - Change your character's age.
/card gender (gender) - Change your character's gender.
/card religion (religion) - Change your character's religion.
```

---

## Example 2: Human-Only Server (Race and Religion Hidden)

### Configuration
```yaml
cardShowRace: false          # HIDDEN
cardShowSubculture: true
cardShowReligion: false      # HIDDEN
cardShowAge: true
cardShowGender: true
```

### Character Card Display
```
----------
Character Card of Player123
----------
Name: John Smith
Subculture: European
Age: 25
Gender: Male
```

### Help Command Output
```
== Character Card Commands ==
/card - View your character card.
/card lookup (player) - View the character card of a specific player.
/card name (name) - Change your character's name.
/card subculture (subculture) - Change your character's subculture.
/card age (age) - Change your character's age.
/card gender (gender) - Change your character's gender.
```

**Note:** `/card race` and `/card religion` are NOT shown in the help

---

## Example 3: Class-Based System (Subculture Renamed to Class)

### Configuration
```yaml
cardShowRace: true
cardShowSubculture: true
cardShowReligion: true
cardShowAge: true
cardShowGender: true
cardLabelRace: "Race"
cardLabelSubculture: "Class"       # RENAMED
cardLabelReligion: "Religion"
cardLabelAge: "Age"
cardLabelGender: "Gender"
cardDefaultSubculture: "Warrior"   # NEW DEFAULT
```

### Character Card Display
```
----------
Character Card of Player123
----------
Name: John Smith
Race: Human
Class: Warrior               ← Changed from "Subculture: Warrior"
Age: 25
Gender: Male
Religion: Christian
```

### Help Command Output
```
== Character Card Commands ==
/card - View your character card.
/card lookup (player) - View the character card of a specific player.
/card name (name) - Change your character's name.
/card race (race) - Change your character's race.
/card subculture (class) - Change your character's class.    ← Updated help text
/card age (age) - Change your character's age.
/card gender (gender) - Change your character's gender.
/card religion (religion) - Change your character's religion.
```

---

## Example 4: Faction-Based System (Subculture Renamed to Faction)

### Configuration
```yaml
cardShowRace: false           # HIDDEN
cardShowSubculture: true
cardShowReligion: false       # HIDDEN
cardShowAge: true
cardShowGender: true
cardLabelSubculture: "Faction"        # RENAMED
cardDefaultSubculture: "Unaffiliated" # NEW DEFAULT
```

### Character Card Display
```
----------
Character Card of Player123
----------
Name: John Smith
Faction: Red Legion          ← Changed from "Subculture: Red Legion"
Age: 25
Gender: Male
```

### Help Command Output
```
== Character Card Commands ==
/card - View your character card.
/card lookup (player) - View the character card of a specific player.
/card name (name) - Change your character's name.
/card subculture (faction) - Change your character's faction.    ← Updated help text
/card age (age) - Change your character's age.
/card gender (gender) - Change your character's gender.
```

---

## Key Features Demonstrated

1. **Field Visibility Control**
   - Hidden fields don't appear on cards
   - Hidden fields don't appear in help command
   - Data is still stored internally

2. **Custom Labels**
   - Any field can be renamed
   - Labels appear on cards and in help
   - Command names stay the same (`/card subculture` not `/card class`)

3. **Default Values**
   - Applied to new character cards only
   - Existing cards keep their values
   - Can be changed per-player using commands

4. **Backward Compatibility**
   - All existing cards work without changes
   - Re-enabling fields shows preserved data
   - No migration needed

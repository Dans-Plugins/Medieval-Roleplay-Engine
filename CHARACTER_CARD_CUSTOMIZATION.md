# Character Card Customization Guide

## Overview
Character cards can now be customized to fit your server's needs. You can hide fields, rename them, and set default values.

## Configuration Options

### Field Visibility
Control which fields appear on character cards:

```yaml
cardShowRace: true          # Show/hide the race field
cardShowSubculture: true    # Show/hide the subculture field
cardShowReligion: true      # Show/hide the religion field
cardShowAge: true           # Show/hide the age field
cardShowGender: true        # Show/hide the gender field
```

### Field Labels
Customize the labels for each field:

```yaml
cardLabelRace: "Race"              # Label for race field
cardLabelSubculture: "Subculture"  # Label for subculture field (e.g., "Faction", "Class")
cardLabelReligion: "Religion"      # Label for religion field
cardLabelAge: "Age"                # Label for age field
cardLabelGender: "Gender"          # Label for gender field
```

### Default Values
Set default values for new character cards:

```yaml
cardDefaultRace: "Human"           # Default race for new cards
cardDefaultSubculture: "None"      # Default subculture for new cards
cardDefaultReligion: "None"        # Default religion for new cards
cardDefaultGender: "Unspecified"   # Default gender for new cards
cardDefaultAge: 0                  # Default age for new cards
```

## Example Configurations

### Example 1: Human-only server without religion
For a server where everyone is human and religion is not emphasized:

```yaml
cardShowRace: false        # Hide race field
cardShowReligion: false    # Hide religion field
cardShowSubculture: true
cardShowAge: true
cardShowGender: true
```

### Example 2: Replace subculture with "class"
For a server that uses character classes instead of subcultures:

```yaml
cardShowSubculture: true
cardLabelSubculture: "Class"
cardDefaultSubculture: "Warrior"
```

### Example 3: Replace subculture with "faction"
For a server using factions:

```yaml
cardShowSubculture: true
cardLabelSubculture: "Faction"
cardDefaultSubculture: "Unaffiliated"
```

## How It Works

1. **Visibility**: When a field is hidden (set to `false`), it will:
   - Not appear on character cards when viewed
   - Not show in the `/card help` command list
   - Still be stored internally (for backward compatibility)

2. **Labels**: Custom labels appear:
   - When viewing character cards
   - In the `/card help` command

3. **Defaults**: Default values are:
   - Applied when a new character card is created
   - Shown on character cards until players change them

## Backward Compatibility

All existing character cards will continue to work normally. Fields are still stored internally even if hidden, so you can re-enable fields later without losing data.

## Related Commands

- `/card` - View your character card
- `/card help` - View available character card commands
- `/card lookup <player>` - View another player's character card
- `/card <field> <value>` - Update a field on your character card

Note: Only fields that are visible will appear in the help command.

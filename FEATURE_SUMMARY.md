# Character Card Customization - Feature Summary

## What Changed

This feature adds the ability to customize character cards through the configuration file, allowing server administrators to:

1. **Hide/Show Fields** - Control which fields appear on character cards
2. **Rename Fields** - Change the labels for fields (e.g., "Subculture" → "Class")
3. **Set Default Values** - Define default values for new character cards

## Use Cases

### Case 1: Human-Only Server (No Race/Religion)
**Problem:** Server owner runs a server where everyone is human and religion isn't emphasized, making those fields confusing on character cards.

**Solution:**
```yaml
cardShowRace: false        # Hide race field
cardShowReligion: false    # Hide religion field
```

**Result:**
- Character cards no longer show race or religion
- Help command doesn't list `/card race` or `/card religion`
- Data is still preserved internally for backward compatibility

### Case 2: Replace Subculture with Class
**Problem:** Server uses character classes (Warrior, Mage, etc.) instead of subcultures.

**Solution:**
```yaml
cardLabelSubculture: "Class"
cardDefaultSubculture: "Peasant"
```

**Result:**
- Character cards show "Class: Warrior" instead of "Subculture: Warrior"
- Help command shows `/card subculture (class)`
- New cards default to "Peasant"

### Case 3: Replace Subculture with Faction
**Problem:** Server uses faction names instead of subcultures.

**Solution:**
```yaml
cardLabelSubculture: "Faction"
cardDefaultSubculture: "Unaffiliated"
```

**Result:**
- Character cards show "Faction: Red Legion" instead of "Subculture: Red Legion"
- New cards default to "Unaffiliated"

## Before and After Examples

### Default Configuration (Before)
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

### Human-Only Server (After)
Config:
```yaml
cardShowRace: false
cardShowReligion: false
```

Result:
```
----------
Character Card of Player123
----------
Name: John Smith
Subculture: European
Age: 25
Gender: Male
```

### Class-Based Server (After)
Config:
```yaml
cardLabelSubculture: "Class"
cardDefaultSubculture: "Warrior"
```

Result:
```
----------
Character Card of Player123
----------
Name: John Smith
Race: Human
Class: Warrior
Age: 25
Gender: Male
Religion: Christian
```

## Implementation Details

### Files Modified
1. `ConfigService.java` - Added 17 new configuration options
2. `CharacterCard.java` - Constructor now uses default values from config
3. `Messenger.java` - Display logic respects visibility and label settings
4. `CardCommand.java` - Help command respects visibility settings

### Configuration Options Added

#### Visibility (Boolean)
- `cardShowRace` (default: true)
- `cardShowSubculture` (default: true)
- `cardShowReligion` (default: true)
- `cardShowAge` (default: true)
- `cardShowGender` (default: true)

#### Labels (String)
- `cardLabelRace` (default: "Race")
- `cardLabelSubculture` (default: "Subculture")
- `cardLabelReligion` (default: "Religion")
- `cardLabelAge` (default: "Age")
- `cardLabelGender` (default: "Gender")

#### Default Values
- `cardDefaultRace` (default: "Human")
- `cardDefaultSubculture` (default: "None")
- `cardDefaultReligion` (default: "None")
- `cardDefaultGender` (default: "Unspecified")
- `cardDefaultAge` (default: 0)

### Backward Compatibility
- All existing character cards work without changes
- Hidden fields are still stored internally
- Re-enabling a field shows existing data
- Defaults only apply to new character cards

## Testing Recommendations

1. Test with all fields visible (default behavior)
2. Test hiding individual fields
3. Test custom labels
4. Test default values for new cards
5. Test that existing cards retain their data
6. Test `/card help` command with various configurations

package dansplugins.rpsystem.data;

import java.util.HashSet;
import java.util.UUID;

import dansplugins.rpsystem.objects.RPCharacter;

/**
 * @author Daniel McCoy Stephenson
 */
public class PersistentData {

    private HashSet<RPCharacter> characters = new HashSet<>();

    public HashSet<RPCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(HashSet<RPCharacter> characters) {
        this.characters = characters;
    }

    public RPCharacter getCharacter(UUID uuid) {
        for (RPCharacter character : getCharacters()) {
            if (character.getPlayerUUID().equals(uuid)) {
                return character;
            }
        }
        return null;
    }

    public boolean hasCharacter(UUID uuid) {
        for (RPCharacter card : getCharacters()) {
            if (card.getPlayerUUID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }
}
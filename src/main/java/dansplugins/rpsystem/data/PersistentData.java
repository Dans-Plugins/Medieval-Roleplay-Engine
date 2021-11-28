package dansplugins.rpsystem.data;

import dansplugins.rpsystem.objects.RPCharacter;

import java.util.HashSet;
import java.util.UUID;

public class PersistentData {

    private static PersistentData instance;

    private HashSet<RPCharacter> characters = new HashSet<>();

    private PersistentData() {

    }

    public static PersistentData getInstance() {
        if (instance == null) {
            instance = new PersistentData();
        }
        return instance;
    }

    public HashSet<RPCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(HashSet<RPCharacter> characters) {
        this.characters = characters;
    }

    public RPCharacter getCharacter(UUID uuid) {
        for (RPCharacter character : PersistentData.getInstance().getCharacters()) {
            if (character.getPlayerUUID().equals(uuid)) {
                return character;
            }
        }
        return null;
    }

    public boolean hasCharacter(UUID uuid) {
        for (RPCharacter card : PersistentData.getInstance().getCharacters()) {
            if (card.getPlayerUUID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

}

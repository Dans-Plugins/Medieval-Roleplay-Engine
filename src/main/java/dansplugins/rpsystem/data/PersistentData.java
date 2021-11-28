package dansplugins.rpsystem.data;

import dansplugins.rpsystem.objects.RPCharacter;

import java.util.ArrayList;
import java.util.UUID;

public class PersistentData {

    private static PersistentData instance;

    private ArrayList<RPCharacter> cards = new ArrayList<>();

    private PersistentData() {

    }

    public static PersistentData getInstance() {
        if (instance == null) {
            instance = new PersistentData();
        }
        return instance;
    }

    public ArrayList<RPCharacter> getCards() {
        return cards;
    }

    public RPCharacter getCharacter(UUID uuid) {
        for (RPCharacter character : PersistentData.getInstance().getCards()) {
            if (character.getPlayerUUID().equals(uuid)) {
                return character;
            }
        }
        return null;
    }

    public boolean hasCharacter(UUID uuid) {
        for (RPCharacter card : PersistentData.getInstance().getCards()) {
            if (card.getPlayerUUID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

}

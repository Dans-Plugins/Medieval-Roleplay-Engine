package dansplugins.rpsystem.data;

import dansplugins.rpsystem.objects.deprecated.CharacterCard;

import java.util.ArrayList;
import java.util.UUID;

public class PersistentData {

    private static PersistentData instance;

    private ArrayList<CharacterCard> cards = new ArrayList<>();

    private PersistentData() {

    }

    public static PersistentData getInstance() {
        if (instance == null) {
            instance = new PersistentData();
        }
        return instance;
    }

    public ArrayList<CharacterCard> getCards() {
        return cards;
    }

    public CharacterCard getCard(UUID uuid) {
        for (CharacterCard card : PersistentData.getInstance().getCards()) {
            if (card.getPlayerUUID().equals(uuid)) {
                return card;
            }
        }
        return null;
    }

    public boolean hasCard(UUID uuid) {
        for (CharacterCard card : PersistentData.getInstance().getCards()) {
            if (card.getPlayerUUID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

}

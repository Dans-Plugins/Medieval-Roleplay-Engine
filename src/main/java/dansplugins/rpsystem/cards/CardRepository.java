package dansplugins.rpsystem.cards;

import java.util.ArrayList;
import java.util.UUID;

public class CardRepository {
    private final ArrayList<CharacterCard> cards = new ArrayList<>();

    public ArrayList<CharacterCard> getCards() {
        return cards;
    }

    public CharacterCard getCard(UUID uuid) {
        for (CharacterCard card : getCards()) {
            if (card.getPlayerUUID().equals(uuid)) {
                return card;
            }
        }
        return null;
    }

    public boolean hasCard(UUID uuid) {
        for (CharacterCard card : getCards()) {
            if (card.getPlayerUUID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

}

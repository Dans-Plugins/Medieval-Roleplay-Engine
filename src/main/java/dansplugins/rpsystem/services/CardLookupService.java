package dansplugins.rpsystem.services;

import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.objects.old.CharacterCard;
import dansplugins.rpsystem.utils.Logger;

import java.util.HashSet;
import java.util.UUID;

public class CardLookupService implements ICardLookupService {

    private static CardLookupService instance;
    private HashSet<CharacterCard> cache = new HashSet<>();

    private CardLookupService() {

    }

    public static CardLookupService getInstance() {
        if (instance == null) {
            instance = new CardLookupService();
        }
        return instance;
    }

    @Override
    public CharacterCard lookup(UUID playerUUID) {
        Logger.getInstance().log("Looking up character card for " + playerUUID.toString());
        CharacterCard card = checkCache(playerUUID);
        if (card == null) {
            return checkStorage(playerUUID);
        }
        return card;
    }

    private CharacterCard checkCache(UUID playerUUID) {
        for (CharacterCard card : cache) {
            if (card.getPlayerUUID().equals(playerUUID)) {
                Logger.getInstance().log("Found in cache!");
                return card;
            }
        }
        return null;
    }

    private CharacterCard checkStorage(UUID playerUUID) {
        CharacterCard card = PersistentData.getInstance().getCard(playerUUID);
        if (card != null) {
            Logger.getInstance().log("Found in storage!");
            cache.add(card);
        }
        else {
            Logger.getInstance().log("Not found.");
        }
        return card;
    }

}

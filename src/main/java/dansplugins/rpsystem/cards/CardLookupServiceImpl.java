package dansplugins.rpsystem.cards;

import dansplugins.rpsystem.MedievalRoleplayEngine;

import java.util.HashSet;
import java.util.UUID;

public class CardLookupServiceImpl implements CardLookupService {
    private final MedievalRoleplayEngine medievalRoleplayEngine;
    private final HashSet<CharacterCard> cache = new HashSet<>();

    public CardLookupServiceImpl(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    @Override
    public CharacterCard lookup(UUID playerUUID) {
        medievalRoleplayEngine.logger.log("Looking up character card for " + playerUUID.toString());
        CharacterCard card = checkCache(playerUUID);
        if (card == null) {
            return checkStorage(playerUUID);
        }
        return card;
    }

    private CharacterCard checkCache(UUID playerUUID) {
        for (CharacterCard card : cache) {
            if (card.getPlayerUUID().equals(playerUUID)) {
                medievalRoleplayEngine.logger.log("Found in cache!");
                return card;
            }
        }
        return null;
    }

    private CharacterCard checkStorage(UUID playerUUID) {
        CharacterCard card = medievalRoleplayEngine.cardRepository.getCard(playerUUID);
        if (card != null) {
            medievalRoleplayEngine.logger.log("Found in storage!");
            cache.add(card);
        }
        else {
            medievalRoleplayEngine.logger.log("Not found.");
        }
        return card;
    }

}

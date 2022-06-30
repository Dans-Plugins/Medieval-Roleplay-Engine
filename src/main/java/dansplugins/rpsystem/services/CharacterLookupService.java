package dansplugins.rpsystem.services;

import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.objects.RPCharacter;
import dansplugins.rpsystem.utils.Logger;

import java.util.HashSet;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class CharacterLookupService { // TODO: replace this class by utilizing Ponder
    private final Logger logger;
    private final PersistentData persistentData;

    private final HashSet<RPCharacter> cache = new HashSet<>();

    public CharacterLookupService(Logger logger, PersistentData persistentData) {
        this.logger = logger;
        this.persistentData = persistentData;
    }

    public RPCharacter lookup(UUID playerUUID) {
        logger.log("Looking up current character for " + playerUUID.toString());
        RPCharacter character = checkCache(playerUUID);
        if (character == null) {
            return checkStorage(playerUUID);
        }
        return character;
    }

    private RPCharacter checkCache(UUID playerUUID) {
        for (RPCharacter character : cache) {
            if (character.getPlayerUUID().equals(playerUUID)) {
                logger.log("Found in cache!");
                return character;
            }
        }
        return null;
    }

    private RPCharacter checkStorage(UUID playerUUID) {
        RPCharacter character = persistentData.getCharacter(playerUUID);
        if (character != null) {
            logger.log("Found in storage!");
            cache.add(character);
        }
        else {
            logger.log("Not found.");
        }
        return character;
    }
}
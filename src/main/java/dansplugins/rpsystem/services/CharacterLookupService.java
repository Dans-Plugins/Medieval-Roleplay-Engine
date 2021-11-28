package dansplugins.rpsystem.services;

import dansplugins.rpsystem.data.PersistentData;
import dansplugins.rpsystem.objects.RPCharacter;
import dansplugins.rpsystem.utils.Logger;

import java.util.HashSet;
import java.util.UUID;

public class CharacterLookupService { // TODO: replace this class by utilizing Ponder

    private static CharacterLookupService instance;
    private HashSet<RPCharacter> cache = new HashSet<>();

    private CharacterLookupService() {

    }

    public static CharacterLookupService getInstance() {
        if (instance == null) {
            instance = new CharacterLookupService();
        }
        return instance;
    }

    public RPCharacter lookup(UUID playerUUID) {
        Logger.getInstance().log("Looking up current character for " + playerUUID.toString());
        RPCharacter character = checkCache(playerUUID);
        if (character == null) {
            return checkStorage(playerUUID);
        }
        return character;
    }

    private RPCharacter checkCache(UUID playerUUID) {
        for (RPCharacter character : cache) {
            if (character.getPlayerUUID().equals(playerUUID)) {
                Logger.getInstance().log("Found in cache!");
                return character;
            }
        }
        return null;
    }

    private RPCharacter checkStorage(UUID playerUUID) {
        RPCharacter character = PersistentData.getInstance().getCharacter(playerUUID);
        if (character != null) {
            Logger.getInstance().log("Found in storage!");
            cache.add(character);
        }
        else {
            Logger.getInstance().log("Not found.");
        }
        return character;
    }

}

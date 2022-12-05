package dansplugins.rpsystem.cards;

import java.util.UUID;

public interface CardLookupService {
    CharacterCard lookup(UUID playerUUID);
}

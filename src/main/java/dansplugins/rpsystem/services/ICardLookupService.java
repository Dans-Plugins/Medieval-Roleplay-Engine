package dansplugins.rpsystem.services;

import dansplugins.rpsystem.objects.old.CharacterCard;

import java.util.UUID;

public interface ICardLookupService {
    CharacterCard lookup(UUID playerUUID);
}

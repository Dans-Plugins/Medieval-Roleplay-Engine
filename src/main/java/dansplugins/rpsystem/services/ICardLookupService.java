package dansplugins.rpsystem.services;

import dansplugins.rpsystem.objects.deprecated.CharacterCard;

import java.util.UUID;

public interface ICardLookupService {
    CharacterCard lookup(UUID playerUUID);
}

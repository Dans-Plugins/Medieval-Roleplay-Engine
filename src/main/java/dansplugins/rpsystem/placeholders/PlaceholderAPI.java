package dansplugins.rpsystem.placeholders;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import dansplugins.rpsystem.cards.CharacterCard;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion {
    private final MedievalRoleplayEngine medievalRoleplayEngine;

    public PlaceholderAPI(MedievalRoleplayEngine medievalRoleplayEngine) {
        this.medievalRoleplayEngine = medievalRoleplayEngine;
    }

    @Override
    public @NotNull
    String getIdentifier() {
        return medievalRoleplayEngine.getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", medievalRoleplayEngine.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return medievalRoleplayEngine.getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {

        params = params.toLowerCase();

        if (player == null) return null;

        CharacterCard card = medievalRoleplayEngine.cardRepository.getCard(player.getUniqueId());
        if (card == null) return null;

        if (params.equalsIgnoreCase("card_name")) {
            return card.getName();
        }
        if (params.equalsIgnoreCase("card_age")) {
            return Integer.toString(card.getAge());
        }
        if (params.equalsIgnoreCase("card_race")) {
            return card.getRace();
        }
        if (params.equalsIgnoreCase("card_subculture")) {
            return card.getSubculture();
        }
        if (params.equalsIgnoreCase("card_gender")) {
            return card.getGender();
        }
        if (params.equalsIgnoreCase("card_religion")) {
            return card.getReligion();
        }

        return null;
    }
}

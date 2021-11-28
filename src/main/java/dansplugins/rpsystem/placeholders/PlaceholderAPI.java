package dansplugins.rpsystem.placeholders;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.antlr.v4.runtime.misc.NotNull;
import org.bukkit.entity.Player;

public class PlaceholderAPI extends PlaceholderExpansion {

    @Override
    public @NotNull
    String getIdentifier() {
        return MedievalRoleplayEngine.getInstance().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", MedievalRoleplayEngine.getInstance().getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return MedievalRoleplayEngine.getInstance().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Deprecated
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        /*

        params = params.toLowerCase();

        if (player == null) return null;

        if (params.equalsIgnoreCase("card_name")) {
            return PersistentData.getInstance().getCharacter(player.getUniqueId()).getName();
        }
        if (params.equalsIgnoreCase("card_age")) {
            return Integer.toString(PersistentData.getInstance().getCharacter(player.getUniqueId()).getAge());
        }
        if (params.equalsIgnoreCase("card_race")) {
            return PersistentData.getInstance().getCharacter(player.getUniqueId()).getRace();
        }
        if (params.equalsIgnoreCase("card_subculture")) {
            return PersistentData.getInstance().getCharacter(player.getUniqueId()).getSubculture();
        }
        if (params.equalsIgnoreCase("card_gender")) {
            return PersistentData.getInstance().getCharacter(player.getUniqueId()).getGender();
        }
        if (params.equalsIgnoreCase("card_religion")) {
            return PersistentData.getInstance().getCharacter(player.getUniqueId()).getReligion();
        }
        */
        return null;
    }
}

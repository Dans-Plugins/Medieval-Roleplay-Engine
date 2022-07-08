package dansplugins.rpsystem.placeholders;

import dansplugins.rpsystem.MedievalRoleplayEngine;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.antlr.v4.runtime.misc.NotNull;
import org.bukkit.entity.Player;

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

    @Deprecated
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) { // TODO: reimplement functionality
        /*

        params = params.toLowerCase();

        if (player == null) return null;

        if (params.equalsIgnoreCase("card_name")) {
            return persistentData.getCharacter(player.getUniqueId()).getName();
        }
        if (params.equalsIgnoreCase("card_age")) {
            return Integer.toString(persistentData.getCharacter(player.getUniqueId()).getAge());
        }
        if (params.equalsIgnoreCase("card_race")) {
            return persistentData.getCharacter(player.getUniqueId()).getRace();
        }
        if (params.equalsIgnoreCase("card_subculture")) {
            return persistentData.getCharacter(player.getUniqueId()).getSubculture();
        }
        if (params.equalsIgnoreCase("card_gender")) {
            return persistentData.getCharacter(player.getUniqueId()).getGender();
        }
        if (params.equalsIgnoreCase("card_religion")) {
            return persistentData.getCharacter(player.getUniqueId()).getReligion();
        }
        */
        return null;
    }
}
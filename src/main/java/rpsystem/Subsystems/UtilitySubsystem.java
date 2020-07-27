package rpsystem.Subsystems;

import rpsystem.CharacterCard;
import rpsystem.Main;

public class UtilitySubsystem {

    Main main = null;

    public UtilitySubsystem(Main plugin) {
        main = plugin;
    }

    public boolean hasCard(String playerName) {
        for (CharacterCard card : main.cards) {
            if (card.getPlayerName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        return false;
    }

    public CharacterCard getCard(String playerName) {
        for (CharacterCard card : main.cards) {
            if (card.getPlayerName().equalsIgnoreCase(playerName)) {
                return card;
            }
        }
        return null;
    }

}

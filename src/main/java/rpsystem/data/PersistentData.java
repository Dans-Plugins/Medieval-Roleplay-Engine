package rpsystem.data;

import rpsystem.Objects.CharacterCard;

import java.util.ArrayList;

public class PersistentData {

    private static PersistentData instance;

    private ArrayList<CharacterCard> cards = new ArrayList<>();

    private PersistentData() {

    }

    public static PersistentData getInstance() {
        if (instance == null) {
            instance = new PersistentData();
        }
        return instance;
    }

    public ArrayList<CharacterCard> getCards() {
        return cards;
    }

}

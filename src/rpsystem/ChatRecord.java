package rpsystem;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatRecord {
    private String playerName  ="";
    private String currentChat = "";
    private HashMap<String, Boolean> chats;

    ChatRecord(String name) {
        playerName = name;
        chats.put("global", true);
        chats.put("local", true);
        currentChat = "global";
    }

    public void setCurrentChat(String newChat) {
        currentChat = newChat;
    }

    public String getCurrentChat() {
        return currentChat;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean inChat(String chat) {
        if (chats.get(chat) == true) {
            return true;
        }
        else {
            return false;
        }
    }

    public void toggleChat(String chatToToggle) {
        chats.put(chatToToggle, !chats.get(chatToToggle));
    }
}

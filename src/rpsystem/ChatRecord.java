package rpsystem;

public class ChatRecord {
    private String playerName  ="";
    private String chat = "";

    ChatRecord(String name) {
        playerName = name;
        chat = "global";
    }

    private String getPlayerName() {
        return playerName;
    }

    private String getChat() {
        return chat;
    }

    private void setChat(String newChat) {
        chat = newChat;
    }
}

package webSocketMessages.serverMessages;

import webSocketMessages.userCommands.UserGameCommand;

public class Message extends ServerMessage{

    private final String message;
    public Message(ServerMessageType type, String message){
        super(type);
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}

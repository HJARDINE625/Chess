package webSocketMessages.serverMessages;

import webSocketMessages.userCommands.UserGameCommand;

public class Message extends ServerMessage{

    private final String message;

    private String errorMessage;
    public Message(ServerMessageType type, String message){
        super(type);
        this.message = message;
        if(type == ServerMessageType.ERROR){
            this.errorMessage = message;
        }
    }

    public String getMessage(){
        return this.message;
    }
}

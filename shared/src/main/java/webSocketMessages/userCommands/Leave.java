package webSocketMessages.userCommands;

import static webSocketMessages.userCommands.UserGameCommand.CommandType.*;

public class Leave extends UserGameCommand {

    private final Integer gameID;

    public Leave(String authToken, boolean didResign, int gameID){
        super(authToken);
        this.gameID = gameID;
        if(!didResign){
            this.commandType = LEAVE;
        } else {
            this.commandType = RESIGN;
        }
    }

    public int getGameID() {
        return gameID;
    }
}

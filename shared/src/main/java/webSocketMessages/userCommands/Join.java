package webSocketMessages.userCommands;

import chess.ChessGame;

import static webSocketMessages.userCommands.UserGameCommand.CommandType.*;

public class Join extends UserGameCommand {

    private final ChessGame.TeamColor playerColor;

    private final Integer gameID;

    public Join(String authToken, ChessGame.TeamColor playerColor, int gameID){
        super(authToken);
        this.gameID = gameID;
        if(playerColor == null){
            this.commandType = JOIN_OBSERVER;
            this.playerColor = null;
        } else {
           this.commandType = JOIN_PLAYER;
           this.playerColor = playerColor;
        }
    }

    public String getTeamColor(){
       if(commandType == JOIN_OBSERVER) {
       return null;
       } else {
           return playerColor.toString();
       }
    }

    public int getGameID() {
        return gameID;
    }
}
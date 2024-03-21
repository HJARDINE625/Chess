package webSocketMessages.userCommands;

import chess.ChessGame;

import static webSocketMessages.userCommands.UserGameCommand.CommandType.*;

public class Join extends UserGameCommand {

    private final ChessGame.TeamColor teamColor;

    private final Integer gameID;

    public Join(String authToken, ChessGame.TeamColor teamColor, int gameID){
        super(authToken);
        this.gameID = gameID;
        if(teamColor == null){
            this.commandType = JOIN_OBSERVER;
            this.teamColor = null;
        } else {
           this.commandType = JOIN_PLAYER;
           this.teamColor = teamColor;
        }
    }

    public String getTeamColor(){
       if(commandType == JOIN_OBSERVER) {
       return null;
       } else {
           return teamColor.toString();
       }
    }

    public int getGameID() {
        return gameID;
    }
}
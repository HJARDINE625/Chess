package webSocketMessages.userCommands;

import chess.ChessMove;

import static webSocketMessages.userCommands.UserGameCommand.CommandType.*;

public class Move extends UserGameCommand {

    private final ChessMove move;

    private final Integer gameID;

    public Move(String authToken, ChessMove myMove, int gameID){
        super(authToken);
        this.gameID = gameID;
        this.commandType = MAKE_MOVE;
        this.move = myMove;
    }

    public ChessMove getMyMove(){
        return this.move;
    }

    public int getGameID() {
        return gameID;
    }
}

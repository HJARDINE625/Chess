package webSocketMessages.serverMessages;

import chess.ChessGame;

public class Load extends ServerMessage{

    private final ChessGame game;
    public Load(ServerMessageType type, ChessGame game){
        super(type);
        this.game = game;
    }

    public ChessGame getGame(){
        return this.game;
    }
}

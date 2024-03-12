package ui;

import chess.ChessGame;
import chess.ChessPiece;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ServerFacade {
    private ServerConnector myServer;

    private int port;

    public ServerFacade(String host, int port){
        myServer = new ServerConnector(host, Integer.toString(port));
    }

    public void executeCommands() throws IOException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        System.out.println("Press 0 for help!");
        while(true){
            //This should make sure that only an error prevents us from getting more inputs.
            InputReader getUserInput = new ConsoleInput();
            myServer.completeAction(getUserInput.getNum());
        }
    }


}

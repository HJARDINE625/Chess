package ui;

import chess.ChessGame;
import chess.ChessPiece;

import java.io.IOException;

public class LoopGenerator{
private ServerConnector myServer;

private ServerConnectorTester myServerConnectorTester;

boolean useServerConnector = true;

private int port;
public LoopGenerator(String host, int port, boolean serverConnector){
        myServer = new ServerConnector(host, Integer.toString(port));
        myServerConnectorTester = new ServerConnectorTester(host, Integer.toString(port));
        if(serverConnector == false){
            useServerConnector = false;
        }
}

public void executeCommands() throws IOException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        System.out.println("Press 0 for help!");
        while(true){
        //This should make sure that only an error prevents us from getting more inputs.
        InputReader getUserInput = new ConsoleInput();
        if(useServerConnector) {
            myServer.completeAction(getUserInput.getNum());
        } else {
            myServerConnectorTester.completeAction(getUserInput.getNum());
        }
        }
        }

//I need this function just to be able to properly test my other functions... it is a dubious one to have...
public ServerConnector getMyServer() {
        return myServer;
        }
    public ServerConnectorTester getMyServerTest() {
        return myServerConnectorTester;
    }

        }
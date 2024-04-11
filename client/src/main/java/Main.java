import chess.*;
import ui.ConsoleInput;
import ui.InputReader;
import ui.LoopGenerator;
import ui.ServerConnectorTester;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        int port = 8080;
        String host = "localhost";
        System.out.println("Please enter a number greater than zero to decide how many Clients to run, they will all execute one after the other in a loop, entering a number that does not fulfill these requirements will prompt you to enter a number again.\n");
        InputReader firstInput = new ConsoleInput();
        int numberOfServers = 0;
        do {
            numberOfServers = firstInput.getNum();
        } while (numberOfServers <= 0);
        //now we can call repeately here instead to allow for multiple clients at once.
        LoopGenerator[] facade = new LoopGenerator[numberOfServers];
        for(int i = 0; i < numberOfServers; i++) {
            facade[i] = new LoopGenerator(host, port, false);
        }
        System.out.println("Press 0 for help!\n");
        while(true) {
            for(int i = 0; i < numberOfServers; i++) {
                System.out.println("In client#" + (i + 1) + "\n");
                facade[i].executeCommands();
            }
        }
    }
}
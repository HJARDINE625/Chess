import chess.*;
import ui.LoopGenerator;
import ui.ServerConnectorTester;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        int port = 8080;
        String host = "localhost";
        LoopGenerator facade = new LoopGenerator(host, port, false);
        facade.executeCommands();
    }
}
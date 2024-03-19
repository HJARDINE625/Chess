package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.Conversions.replace;
import static ui.EscapeSequences.*;
import static ui.EscapeSequences.EMPTY;

public class ChessDrawer {
    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    public void draw() {
        ChessBoard board = new ChessBoard();
        ChessGame chess = new ChessGame();
        draw(chess, true, true);
    }

    //here I pass in a game specifically so I can do something more complicated if I choose to...
    public void draw(ChessGame game, boolean bl, boolean wh) {
        ChessBoard board = game.getBoard();
        //give a line or so
        out.print(SET_BG_COLOR_BLUE);
        out.print("\n");
        if(bl == true){
        out.print(SET_BG_COLOR_BLUE);
        for (int size = (board.getChessBoardSize() - 1); size >= 0; --size) {
            out.print(EMPTY);
            out.print(SET_TEXT_BOLD);
            out.print(SET_TEXT_COLOR_MAGENTA);
            out.print(SET_BG_COLOR_BLUE);
            out.print(letterFiller(size));
        }
        boolean blackSpace = true;
        //print right side up board
        for (int space = 0; space < board.getChessBoardSize(); ++space) {
            out.print("\n");
            out.print(SET_TEXT_BOLD);
            out.print(SET_TEXT_COLOR_MAGENTA);
            out.print(SET_BG_COLOR_BLUE);
            out.print(space + 1);
            if (!blackSpace) {
                blackSpace = true;
            } else {
                blackSpace = false;
            }
            for (int width = 0; width < board.getChessBoardSize(); ++width) {
                out.print(SET_TEXT_BOLD);
                out.print(SET_TEXT_COLOR_RED);
                if (blackSpace) {
                    blackSpace = false;
                    out.print(SET_BG_COLOR_WHITE);
                } else {
                    blackSpace = true;
                    out.print(SET_BG_COLOR_BLACK);
                }
                if (board.getPiece(new ChessPosition(space, width)) != null) {
                    ChessPiece myPiece = board.getPiece(new ChessPosition(space, width));
                    ChessGame.TeamColor color = myPiece.getTeamColor();
                    ChessPiece.PieceType piece = myPiece.getPieceType();
                    replace(color.name(), piece.name());
                } else {
                    out.print(EMPTY);
                }
            }
            out.print(SET_TEXT_BOLD);
            out.print(SET_TEXT_COLOR_MAGENTA);
            out.print(SET_BG_COLOR_BLUE);
            out.print(space + 1);
        }
        out.print("\n");
        out.print(SET_BG_COLOR_BLUE);
        for (int size = (board.getChessBoardSize() - 1); size >= 0; --size) {
            out.print(EMPTY);
            out.print(SET_TEXT_BOLD);
            out.print(SET_TEXT_COLOR_MAGENTA);
            out.print(SET_BG_COLOR_BLUE);
            out.print(letterFiller(size));
        }
    }
        if(wh == true) {
            out.print("\n");
            //give a line or so
            out.print(SET_BG_COLOR_BLUE);
            for (int size = 0; size < board.getChessBoardSize(); ++size) {
                out.print(EMPTY);
                out.print(SET_TEXT_BOLD);
                out.print(SET_TEXT_COLOR_MAGENTA);
                out.print(SET_BG_COLOR_BLUE);
                out.print(letterFiller(size));
            }
            boolean blackSpace = true;
            //print upside down board
            for (int space = board.getChessBoardSize(); space > 0; --space) {
                out.print("\n");
                out.print(SET_TEXT_BOLD);
                out.print(SET_TEXT_COLOR_MAGENTA);
                out.print(SET_BG_COLOR_BLUE);
                out.print(space);
                if (!blackSpace) {
                    blackSpace = true;
                } else {
                    blackSpace = false;
                }
                for (int width = board.getChessBoardSize(); width > 0; --width) {
                    out.print(SET_TEXT_BOLD);
                    out.print(SET_TEXT_COLOR_RED);
                    if (blackSpace) {
                        blackSpace = false;
                        out.print(SET_BG_COLOR_WHITE);
                    } else {
                        blackSpace = true;
                        out.print(SET_BG_COLOR_BLACK);
                    }
                    if (board.getPiece(new ChessPosition(space, width)) != null) {
                        ChessPiece myPiece = board.getPiece(new ChessPosition(space, width));
                        ChessGame.TeamColor color = myPiece.getTeamColor();
                        ChessPiece.PieceType piece = myPiece.getPieceType();
                        replace(color.name(), piece.name());
                    } else {
                        out.print(EMPTY);
                    }
                }
                out.print(SET_TEXT_BOLD);
                out.print(SET_TEXT_COLOR_MAGENTA);
                out.print(SET_BG_COLOR_BLUE);
                out.print(space);
            }
            out.print("\n");
            //give a line or so
            out.print(SET_BG_COLOR_BLUE);
            for (int size = 0; size < board.getChessBoardSize(); ++size) {
                out.print(EMPTY);
                out.print(SET_TEXT_BOLD);
                out.print(SET_TEXT_COLOR_MAGENTA);
                out.print(SET_BG_COLOR_BLUE);
                out.print(letterFiller(size));
            }
        }
        out.print(SET_BG_COLOR_BLUE);
        out.print("\n");
    }

    private String letterFiller(int location){
        String letterConversion = "";
        int intermediate = location/26;
        if(intermediate >= 1){
            letterConversion = letterFiller(intermediate);
        }
        int added = location%26;
        char starterLetter = 'A';
        char newLetter = (char) (starterLetter + added);
        letterConversion = letterConversion + newLetter;
        return letterConversion;
    }


    private void replace(String color, String piece){
        //right now we only have escape sequences for black and white...
        if(color.equals("BLACK")) {
            if(piece.equals("PAWN")){
                out.print(BLACK_PAWN);
            } else if(piece.equals("KNIGHT")){
                out.print(BLACK_KNIGHT);
            } else if(piece.equals("BISHOP")){
                out.print(BLACK_BISHOP);
            } else if(piece.equals("ROOK")){
                out.print(BLACK_ROOK);
            } else if(piece.equals("QUEEN")){
                out.print(BLACK_QUEEN);
            } else if(piece.equals("KING")){
                out.print(BLACK_KING);
            }
        } else if(color.equals("WHITE")){
            if(piece.equals("PAWN")){
                out.print(WHITE_PAWN);
            } else if(piece.equals("KNIGHT")){
                out.print(WHITE_KNIGHT);
            } else if(piece.equals("BISHOP")){
                out.print(WHITE_BISHOP);
            } else if(piece.equals("ROOK")){
                out.print(WHITE_ROOK);
            } else if(piece.equals("QUEEN")){
                out.print(WHITE_QUEEN);
            } else if(piece.equals("KING")){
                out.print(WHITE_KING);
            }
        }
    }

}

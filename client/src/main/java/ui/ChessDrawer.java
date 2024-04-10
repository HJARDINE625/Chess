package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.Conversions.replace;
import static ui.EscapeSequences.*;
import static ui.EscapeSequences.EMPTY;

public class ChessDrawer {
    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    public void draw() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessGame chess = new ChessGame();
        chess.setBoard(board);
        draw(chess, true, true);
    }

    public void draw(ChessGame game, boolean bl, boolean wh){
        draw(game, bl, wh, null);
    }


    //here I pass in a game specifically so I can do something more complicated if I choose to...
    public void draw(ChessGame game, boolean bl, boolean wh, ChessPosition hereAmI) {
        //check the game
        if(game == null){
            out.print("\nNO SUCH GAME!!!!!!!!\n\n");
            return;
        }
        Collection<ChessMove> allowedMoves = null;
        //check the position
        ChessBoard board = game.getBoard();
        boolean thereAreChessMoves = false;
        if(!(hereAmI == null)) {
            if (!((hereAmI.getColumn() >= board.getChessBoardSize()) || (hereAmI.getRow() >= board.getChessBoardSize()))) {
                if (!((hereAmI.getColumn() <= 0) || (hereAmI.getRow() <= 0))) {
                    if(game.validMoves(hereAmI) != null){
                        if(!game.validMoves(hereAmI).isEmpty()){
                            allowedMoves = game.validMoves(hereAmI);
                        }
                    }
                }
            }
        }
        //give a line or so
        out.print(SET_BG_COLOR_BLUE);
        out.print("\n");
        if(bl == true){
        out.print(SET_BG_COLOR_BLUE);
        for (int size = (board.getChessBoardSize() - 1); size >= 0; --size) {
            out.print(" \u2003");
            out.print(SET_TEXT_BOLD);
            out.print(SET_TEXT_COLOR_MAGENTA);
            out.print(SET_BG_COLOR_BLUE);
            out.print(letterFiller(size));
        }
        boolean blackSpace = false;
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
                if (board.getPiece(new ChessPosition(space+1, width+1)) != null) {
                    ChessPiece myPiece = board.getPiece(new ChessPosition(space+1, width+1));
                    ChessGame.TeamColor color = myPiece.getTeamColor();
                    ChessPiece.PieceType piece = myPiece.getPieceType();
                    //check if this is the move starting position
                    if(new ChessPosition(space, width).equals(hereAmI)){
                        out.print(SET_BG_COLOR_BLUE);
                        replace(color.name(), piece.name());
                    } else {
                        replace(color.name(), piece.name(), blackSpace);
                    }
                } else {
                    boolean isAMove = false;
                    for (ChessMove m : allowedMoves) {
                        ChessPosition pos = m.getEndPosition();
                        if ((pos.getRow() == space) && (pos.getColumn() == width)) {
                            isAMove = true;
                            out.print(SET_BG_COLOR_GREEN);
                            out.print(SET_TEXT_COLOR_GREEN);
                            out.print(WHITE_PAWN);
                        }
                    }
                    if (!isAMove) {
                        if (blackSpace) {
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_PAWN);
                        } else {
                            out.print(SET_TEXT_COLOR_WHITE);
                            out.print(WHITE_PAWN);
                        }
                    }
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
            out.print(" \u2003");
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
                out.print(" \u2003");
                out.print(SET_TEXT_BOLD);
                out.print(SET_TEXT_COLOR_MAGENTA);
                out.print(SET_BG_COLOR_BLUE);
                out.print(letterFiller(size));
            }
            boolean blackSpace = false;
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
                        //check if this is the move starting position
                        if(new ChessPosition(space, width).equals(hereAmI)){
                            out.print(SET_BG_COLOR_BLUE);
                            replace(color.name(), piece.name());
                        } else {
                            replace(color.name(), piece.name(), blackSpace);
                        }
                    } else {
                        boolean isAMove = false;
                        for (ChessMove m: allowedMoves) {
                            ChessPosition pos = m.getEndPosition();
                            if((pos.getRow() == space) && (pos.getColumn() == width)){
                                isAMove = true;
                                out.print(SET_BG_COLOR_GREEN);
                                out.print(SET_TEXT_COLOR_GREEN);
                                out.print(BLACK_PAWN);
                            }
                        }
                        if(!isAMove) {
                            if (blackSpace) {
                                out.print(SET_TEXT_COLOR_BLACK);
                                out.print(BLACK_PAWN);
                            } else {
                                out.print(SET_TEXT_COLOR_WHITE);
                                out.print(WHITE_PAWN);
                            }
                        }
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
                out.print(" \u2003");
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

    //call this one if you need to keep the background the same color...
    private void replace(String color, String piece){
        out.print(SET_TEXT_BOLD);
        if(color.equals("BLACK")) {
            out.print(SET_TEXT_COLOR_DARK_GREY);
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
            out.print(SET_TEXT_COLOR_LIGHT_GREY);
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


    private void replace(String color, String piece, boolean blackSpace){
        //right now we only have escape sequences for black and white...
        if(blackSpace){
            out.print(SET_BG_COLOR_BLACK);
        } else {
            out.print(SET_BG_COLOR_WHITE);
        }
        replace(color, piece);
//        out.print(SET_TEXT_BOLD);
//        if(color.equals("BLACK")) {
//            out.print(SET_TEXT_COLOR_DARK_GREY);
//            if(piece.equals("PAWN")){
//                out.print(BLACK_PAWN);
//            } else if(piece.equals("KNIGHT")){
//                out.print(BLACK_KNIGHT);
//            } else if(piece.equals("BISHOP")){
//                out.print(BLACK_BISHOP);
//            } else if(piece.equals("ROOK")){
//                out.print(BLACK_ROOK);
//            } else if(piece.equals("QUEEN")){
//                out.print(BLACK_QUEEN);
//            } else if(piece.equals("KING")){
//                out.print(BLACK_KING);
//            }
//        } else if(color.equals("WHITE")){
//            out.print(SET_TEXT_COLOR_LIGHT_GREY);
//            if(piece.equals("PAWN")){
//                out.print(WHITE_PAWN);
//            } else if(piece.equals("KNIGHT")){
//                out.print(WHITE_KNIGHT);
//            } else if(piece.equals("BISHOP")){
//                out.print(WHITE_BISHOP);
//            } else if(piece.equals("ROOK")){
//                out.print(WHITE_ROOK);
//            } else if(piece.equals("QUEEN")){
//                out.print(WHITE_QUEEN);
//            } else if(piece.equals("KING")){
//                out.print(WHITE_KING);
//            }
//        }
    }

}

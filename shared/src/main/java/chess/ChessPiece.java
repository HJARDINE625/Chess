package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessBoard firstMoveChecker = new ChessBoard();

    public boolean hasThisPieceMoved(ChessPosition currentPosition) {
        if(hasMoved) {
            return hasMoved;
        } else if (this.type!=firstMoveChecker.getPiece(currentPosition).getPieceType()){
            hasMoved = true;
            return hasMoved;
        } else if (this.pieceColor!=firstMoveChecker.getPiece(currentPosition).getTeamColor()){
            hasMoved = true;
            return hasMoved;
        } else {
            return hasMoved;
        }
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    private boolean hasMoved = false;
    private ChessGame.TeamColor pieceColor;

    public void setPieceColor(ChessGame.TeamColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    public void setType(PieceType type) {
        this.type = type;
    }

    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.pieceColor = pieceColor;
        this.firstMoveChecker.resetBoard();
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        ARCHBISHIOPRIDER,
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //First we need an array to store all the moves and a list of booleans to store all the kinds of moves.
        boolean up = false;
        boolean down = false;
        boolean left = false;
        boolean right = false;
        boolean drup = false;
        boolean dlup = false;
        boolean drdo = false;
        boolean dldo = false;
        //might want to change this last one for more readablity, but it is more expandable like this
        boolean knightup = false;
        boolean knightdown = false;
        boolean knightleft = false;
        boolean knightright = false;
        boolean knightdrup = false;
        boolean knightdlup = false;
        boolean knightdrdo = false;
        boolean knightdldo = false;
        //We also need some ints to decide how far they can go like this.
        //First we need an array to store all the moves and a list of booleans to store all the kinds of moves.
        //Here I break naming convention in order to show how the ints relate to the booleans.
        int Up = 0;
        int Down = 0;
        int Left = 0;
        int Right = 0;
        int Drup = 0;
        int Dlup = 0;
        int Drdo = 0;
        int Dldo = 0;
        //might want to change this last one for more readablity, but it is more expandable like this
        int Knightup = 0;
        int Knightdown = 0;
        int Knightleft = 0;
        int Knightright = 0;
        int Knightdrup = 0;
        int Knightdlup = 0;
        int Knightdrdo = 0;
        int Knightdldo = 0;
        //Now we need a inital array list of ChessPositions to store the positions as we generate them bellow.
        ArrayList<ChessPosition> myMoves = new ArrayList<>();
        //After setting up the data structure to hold the new list we need to decide what kind of moves should be made for the piece.
        //For every infinte move set the bool will be changed and for every finite one only the int will be.
        //First move/attack will be set then move then attack. so we will start setting both booleans to true.
        boolean canMove = true;
        boolean canAttack = true;
        switch(type) {
            case KING:
            Up++;
            Down++;
            Left++;
            Right++;
            Drup++;
            Dlup++;
            Drdo++;
            Dldo++;
            break;
            case QUEEN:
            up = true;
            down = true;
            left = true;
            right = true;
            drup = true;
            dlup = true;
            drdo = true;
            dldo = true;
            break;
            case ROOK:
            up = true;
            down = true;
            left = true;
            right = true;
            break;
            case BISHOP:
            drup = true;
            dlup = true;
            drdo = true;
            dldo = true;
            break;
            case KNIGHT:
                knightup = true;
                knightdown = true;
                knightleft = true;
                knightright = true;
                knightdrup = true;
                knightdlup = true;
                knightdrdo = true;
                knightdldo = true;
                break;
            //Oddly enough the pawn can always only move to or attack to a space, never both to a space.
            case PAWN:
                break;
            default:
                break;
        }
        //Now that we know who can move/attack in what ways we need another set of statements to execute code to find all the move sets.
        if (up){
            myMoves = Move(board, canMove, canAttack, 1, 0, myPosition, myMoves);
        }
        throw new RuntimeException("Not implemented");
    }
    //move a specific way forever
    private ArrayList<ChessPosition> Move(ChessBoard board, boolean canMove, boolean canAttack, int deltaY, int deltaX, ChessPosition myPosition, ArrayList<ChessPosition> validSpaces) {
        // This calls the function that actually caculates where you would move next
        ChessPosition myNewPosition = caculateNextValidSpace(board, canMove, canAttack, deltaY, deltaX, myPosition, validSpaces);
        if (myNewPosition != null) {
            // Add to list and recurse.
            validSpaces.add(myNewPosition);
            return Move(board, canMove, canAttack, deltaY, deltaX, myNewPosition, validSpaces);
        } else {
            // Otherwise we have reached the end of the line and need to return
            return validSpaces;
        }
    }
    //move a specific way for a set number of moves, I considered naming these funcitons move infintely and move finitely, but decided this was actually eaiser to use
    private ArrayList<ChessPosition> Move(ChessBoard board, boolean canMove, boolean canAttack, int deltaY, int deltaX, ChessPosition myPosition, ArrayList<ChessPosition> validSpaces, int depth){
        //int endChecker = validSpaces.size();
        if (depth == 0) {
            return validSpaces;
        } else{
            // Prepare for finite recursion by decrementing the remaining number of moves.
            depth--;
            // This calls the function that actually caculates where you would move next
            ChessPosition myNewPosition = caculateNextValidSpace(board, canMove, canAttack, deltaY, deltaX, myPosition, validSpaces);
            if (myNewPosition != null){
                // Add to list and recurse.
                validSpaces.add(myNewPosition);
                return Move(board, canMove, canAttack, deltaY, deltaX, myNewPosition, validSpaces, depth);
            } else {
                // Otherwise we have reached the end of the line and need to return
                return validSpaces;
            }
        }
    }
    // Here we caculate the next space
    private ChessPosition caculateNextValidSpace(ChessBoard board, boolean canMove, boolean canAttack, int deltaY, int deltaX, ChessPosition myPosition, Collection<ChessPosition> validSpaces){
        // First caculate where the next move position would be
        int myNewYPosition = myPosition.getColumn() + deltaX;
        int myNewXPosition = myPosition.getRow() + deltaY;
        ChessPosition nextValidSpace = new ChessPosition(myNewYPosition, myNewXPosition);
        // We will not ask for spaces that are out of bounds
        if(((myNewXPosition > board.getChessBoardSize()) || (myNewYPosition > board.getChessBoardSize())) || ((myNewXPosition < 0) || (myNewYPosition < 0))) {
            return null;
            // next we will have to see if we can move to an empty space if the space is empty or a full space if it is full
        } else if (board.getPiece(nextValidSpace) == null){
            if(canMove) {
                return nextValidSpace;
            } else {
                return null;
            }
        } else {
            ChessPiece obstruction = board.getPiece(nextValidSpace);
            if(obstruction.getTeamColor() == this.pieceColor) {
                //change this statement by adding an inner statement and another boolean in this and the two functions that call it if you want to allow for peice switching or suicide.
                return null;
            } else if (canAttack){
                return nextValidSpace;
            } else {
                return null;
            }
        }
    }

}

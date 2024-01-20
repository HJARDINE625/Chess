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
        ArrayList<ChessPosition> myMoves = new ArrayList<>();

        throw new RuntimeException("Not implemented");
    }
    //move a specific way forever
    private Collection<ChessPosition> Move(ChessBoard board, boolean canMove, boolean canAttack, int deltaY, int deltaX, ChessPosition myPosition, Collection<ChessPosition> validSpaces) {
        //int endChecker = validSpaces.size();
        ChessPosition myNewPosition = caculateNextValidSpace(board, canMove, canAttack, deltaY, deltaX, myPosition, validSpaces);
        if (myNewPosition != null) {
            validSpaces.add(myNewPosition);
            return Move(board, canMove, canAttack, deltaY, deltaX, myNewPosition, validSpaces);
        } else {
            return validSpaces;
        }
    }
    //move a specific way for a set number of moves, I considered naming these funcitons move infintely and move finitely, but decided this was actually eaiser to use
    private Collection<ChessPosition> Move(ChessBoard board, boolean canMove, boolean canAttack, int deltaY, int deltaX, ChessPosition myPosition, Collection<ChessPosition> validSpaces, int depth){
        //int endChecker = validSpaces.size();
        if (depth == 0) {
            return validSpaces;
        } else{
            depth--;
            ChessPosition myNewPosition = caculateNextValidSpace(board, canMove, canAttack, deltaY, deltaX, myPosition, validSpaces);
            if (myNewPosition != null){
                validSpaces.add(myNewPosition);
                return Move(board, canMove, canAttack, deltaY, deltaX, myNewPosition, validSpaces, depth);
            } else {
                return validSpaces;
            }
        }
    }
    //Here we caculate the next space
    private ChessPosition caculateNextValidSpace(ChessBoard board, boolean canMove, boolean canAttack, int deltaY, int deltaX, ChessPosition myPosition, Collection<ChessPosition> validSpaces){
        int myNewYPosition = myPosition.getColumn() + deltaX;
        int myNewXPosition = myPosition.getRow() + deltaY;
        ChessPosition nextValidSpace = new ChessPosition(myNewYPosition, myNewXPosition);
        if(((myNewXPosition > board.getChessBoardSize()) || (myNewYPosition > board.getChessBoardSize())) || ((myNewXPosition < 0) || (myNewYPosition < 0))) {
            return null;
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

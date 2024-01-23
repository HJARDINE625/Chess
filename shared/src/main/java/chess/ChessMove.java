package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
        //throw new RuntimeException("Not implemented");
    }

    //new equals method.
    @Override
    public boolean equals(Object o){
        //These first two lines might be completely unessisary, but I include them anyway.
        if(o == null){
            return false;
        } if (this == null) {
            return false;
        } if(o.getClass().equals(this.getClass())) {
            //I am not sure if the lines above or below this work, but if they do, so does the function
            if (Objects.equals(o.toString(), this.toString())) {
                return true;
            }
        }
    return false;
    }

    public String toString() {
        String toStringBuilder = new String();
        toStringBuilder = startPosition.toString() + ";" + endPosition.toString();
        toStringBuilder = toStringBuilder + " " + promotionPiece;
        return toStringBuilder;
    }

    @Override
    public int hashCode() {
        //Here I make three prime numbers (1 large and 1 small) to allow for start and end position flipping and to make new promotions special.
        int specialInt = 37;
        int secondPrime = 13;
        int lastPrime = 3;
        //Now I add them in various ways to allow for new hash code combinations
        specialInt = specialInt * startPosition.hashCode() + specialInt;
        specialInt = specialInt * endPosition.hashCode() + specialInt;
        //This line helps startPosition and endPosition not be reflexive
        specialInt = specialInt + ((secondPrime*startPosition.hashCode())+ endPosition.hashCode());
        //now I need to incorporate the promotions. But only if they exist
        if(promotionPiece != null) {
            specialInt = (specialInt * lastPrime) + promotionPiece.hashCode();
            //I think that the premotionPiece.hashcode should be small enough now to allow for it to not really influence what has already happened
            //due to all the times large prime number steps above.

        }
        //return last part
        //make it a little smaller just for good measure
        return specialInt - lastPrime;
    }

}

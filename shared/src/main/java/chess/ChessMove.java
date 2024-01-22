package chess;

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
            if (this.toString() == o.toString()) {
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

}

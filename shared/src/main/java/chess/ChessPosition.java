package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
        //throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean equals(Object o) {
        //These first two lines might be completely unessisary, but I include them anyway.
        if (o == null) {
            return false;
        }
        if (this == null) {
            return false;
        }
        if (o.getClass() == this.getClass()) {
            if(o.toString() == this.toString()) {
                return true;
            }
        }
        return false;
    }

    public String toString(){
        StringBuilder returnedString = new StringBuilder();
        returnedString = new StringBuilder("("+ Integer.toString(col) + "," + Integer.toString(row) + ")");
        return returnedString.toString();
    }


}

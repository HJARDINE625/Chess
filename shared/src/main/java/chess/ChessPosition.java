package chess;

import java.util.Objects;

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
            if(Objects.equals(o.toString(), this.toString())) {
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

    @Override
    public int hashCode() {
        //Here I make two prime numbers (1 large and 1 small) to allow for row and column flipping to not create equal hash codes
        int specialInt = 29;
        int secondPrime = 11;
        //Now I add them in various ways to allow for new hash code combinations
        specialInt = specialInt*row + specialInt;
        specialInt = specialInt*col + specialInt;
        //This last line helps make row and column not reflexive (ie (4,5)!=(5,4));
        return specialInt + ((secondPrime*row)+col);
    }
}

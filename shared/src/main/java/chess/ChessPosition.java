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
    public boolean equals(Object o){
        //These first two lines might be completely unessisary, but I include them anyway.
        if(o == null){
            return false;
        } if (this == null) {
            return false;
        } if(o.getClass().equals(this.getClass())){
            //I am not sure if the lines above or below this work, but if they do, so does the function
           if(this.getRow() == ((ChessPosition) o).getRow() && this.getColumn() == ((ChessPosition) o).getColumn()){
               return true;
           } else {
               return false;
           }
        } else {
            return false;
        }

    }

}

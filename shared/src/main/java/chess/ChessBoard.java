package chess;

//Make sure to import ALL pieces here!

//import static chess.ChessPiece.PieceType.PAWN;
//import static chess.ChessPiece.PieceType.KNIGHT;
//import static chess.ChessPiece.PieceType.BISHOP;
//import static chess.ChessPiece.PieceType.QUEEN;
//import static chess.ChessPiece.PieceType.KING;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    //How big the user ought to think the chess board is
    private int ChessBoardSize;

    //All the magic "1's" allow for the board to be just big enough to be eaisly indexed into
    private ChessPiece[][] baseboard;
    private ChessPiece[][] squares;

    private boolean addingToBaseBoard = true;
    //Set up the reset board

    //This is a very helpful function for implementing how the game runs.
    public HashSet<ChessPosition> findRulingPieces(ChessGame.TeamColor teamToInspect) {
        HashSet<ChessPosition> rulingPieces = new HashSet<ChessPosition>();
        for (int i = 1; i<=ChessBoardSize; i++){
            for(int j = 1; j<=ChessBoardSize; j++){
                if(squares[i][j] != null){
                    if(squares[i][j].getPieceType() != null) {
                        if (squares[i][j].getPieceType() == ChessPiece.PieceType.KING) {
                            if(squares[i][j].getTeamColor() != null) {
                                if (squares[i][j].getTeamColor() == teamToInspect) {
                                    rulingPieces.add(new ChessPosition(i, j));
                                }
                            }
                        }
                    }
                }
            }
        }
        return rulingPieces;
    }

    public HashSet<ChessPosition> returnAllPiecesOnTeam(ChessGame.TeamColor teamToInspect){
        //This is slightly repeated from the function above, but it is more confusing to change probably... (maybe not)
        HashSet<ChessPosition> moveset = new HashSet<ChessPosition>();
        for (int i = 1; i<=ChessBoardSize; i++){
            for(int j = 1; j<=ChessBoardSize; j++){
                if(squares[i][j] != null){
                    if(squares[i][j].getTeamColor() != null) {
                        if (squares[i][j].getTeamColor() == teamToInspect) {
                            ChessPosition piecesLocation = new ChessPosition(i,j);
                            moveset.add(piecesLocation);
                            }
                        }
                    }
                }
            }

        return moveset;
    }
    //Here is a function that uses the function above as a helper function
    public HashSet<ChessMove> returnAllPiecesMovesOnTeam(ChessGame.TeamColor teamToInspect) {
        HashSet<ChessPosition> moveset = returnAllPiecesOnTeam(teamToInspect);
        HashSet<ChessMove> finalMoves = new HashSet<ChessMove>();
        for (ChessPosition p:moveset) {
            ChessPiece examinedPiece = getPiece(p);
            Collection<ChessMove> moves = examinedPiece.pieceMoves(this, p);
            for (ChessMove m:moves) {
                finalMoves.add(m);
            }
        }
        return finalMoves;
    }


    public ChessBoard() {
        //We need to set up the base board (we will reset if we want to start a game.

        //first decide length/height the chessboard ought to be
        ChessBoardSize = 8;

        //now create squares first
        this.squares = new ChessPiece[ChessBoardSize+1][ChessBoardSize+1];
        this.baseboard = new ChessPiece[ChessBoardSize+1][ChessBoardSize+1];

        //define twined white and black pieces
        ChessPiece whitePieces = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPiece blackPieces = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        //These are on the second row.
        int[] rows = new int [1];
        rows[0] = 1;
        //These are on all the columns
        int[] columns = new int[4];
        for(int i = 0; i<columns.length; i++){
            columns[i] = i;
        }
        //now run the algerythem
        rowSetUp(columns,rows,whitePieces,blackPieces, addingToBaseBoard);

        //now do this for the other peices
        //rows go up one
        rows[0] = 0;
        //all the rest of the pieces we can use rowSetUp on have only two copies in base chess.
        columns = new int[1];



        //rooks
        whitePieces.setType(ChessPiece.PieceType.ROOK);
        blackPieces.setType(ChessPiece.PieceType.ROOK);
        columns[0] = 0;
        rowSetUp(columns,rows,whitePieces,blackPieces,addingToBaseBoard);

        //knights
        whitePieces.setType(ChessPiece.PieceType.KNIGHT);
        blackPieces.setType(ChessPiece.PieceType.KNIGHT);
        columns[0]++;
        rowSetUp(columns,rows,whitePieces,blackPieces,addingToBaseBoard);

        //Bishops
        whitePieces.setType(ChessPiece.PieceType.BISHOP);
        blackPieces.setType(ChessPiece.PieceType.BISHOP);
        columns[0]++;
        rowSetUp(columns,rows,whitePieces,blackPieces,addingToBaseBoard);

        //King and Queen need to be done separately as they are not cloned.
        whitePieces.setType(ChessPiece.PieceType.QUEEN);
        blackPieces.setType(ChessPiece.PieceType.QUEEN);
        //now duplicate it into another piece
        ChessPiece whiteUltimate = new ChessPiece(whitePieces.getTeamColor(), whitePieces.getPieceType());
        ChessPiece blackUltimate = new ChessPiece(blackPieces.getTeamColor(), blackPieces.getPieceType());

        //Hard coded, not the best but it works...
        ChessPosition whitePos = new ChessPosition(1,4);
        ChessPosition blackPos = new ChessPosition(ChessBoardSize,4);
        this.addPiece(whitePos,whiteUltimate,addingToBaseBoard);
        this.addPiece(blackPos,blackUltimate,addingToBaseBoard);

        //King's turn
        whitePieces.setType(ChessPiece.PieceType.KING);
        blackPieces.setType(ChessPiece.PieceType.KING);
        //Hard coded, not the best but it works...
        whitePos = new ChessPosition(1,5);
        blackPos = new ChessPosition(ChessBoardSize,5);
        this.addPiece(whitePos,whitePieces,addingToBaseBoard);
        this.addPiece(blackPos,blackPieces,addingToBaseBoard);

        //Do this in a different funciton (specifically the reset one)
        //Now copy to base board
//        for (ChessPiece[] chessPieces : baseboard = squares) {
//        }
        //not too sure that worked...


//END


        //revert to this if the above code cannot work
        //then fill it in first and copy it to the reset board
//        for(int i = 1; i <= ChessBoardSize; i++) {
//            //this adds the frontrow white pieces followed by the front row black pieces in a normal game.
//            ChessPosition pos = new ChessPosition(2,i);
//            ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//            this.addPiece(pos, piece);
//            pos = new ChessPosition(ChessBoardSize-1,i);
//            piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
//            this.addPiece(pos,piece);
//        }
        //Now we add the other pieces, iterately doing this can get confusing, so it is done one piece at a time. White and then Black.

//        //Adding Rooks
//        ChessPosition pos = new ChessPosition(1,1);
//        ChessPosition pos2 = new ChessPosition(1, ChessBoardSize);
//        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
//        this.addPiece(pos, piece);
//        this.addPiece(pos2, piece);
//        pos = new ChessPosition(ChessBoardSize, ChessBoardSize);
//        pos2 = new ChessPosition(ChessBoardSize,1);
//        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
//        this.addPiece(pos,piece);
//        this.addPiece(pos2,piece);
//        //Adding Knights
//        pos = new ChessPosition(1,2);
//        pos2 = new ChessPosition(1,ChessBoardSize-1);
//        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
//        this.addPiece(pos,piece);
//        this.addPiece(pos2,piece);
//        pos = new ChessPosition(ChessBoardSize,ChessBoardSize-1);
//        pos2 = new ChessPosition(ChessBoardSize,2);
//        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
//        this.addPiece(pos,piece);
//        this.addPiece(pos2,piece);
//        //Adding Bishops
//        pos = new ChessPosition(1,3);
//        pos2 = new ChessPosition(1,ChessBoardSize-2);
//        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
//        this.addPiece(pos,piece);
//        this.addPiece(pos2,piece);
//        pos = new ChessPosition(ChessBoardSize,ChessBoardSize-2);
//        pos2 = new ChessPosition(ChessBoardSize,3);
//        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
//        this.addPiece(pos,piece);
//        this.addPiece(pos2,piece);
    }
//    public ChessBoard(boolean addingToBaseBoardReset) {
//        if(addingToBaseBoardReset){
//            this.ChessBoard();
//        } else {
//            this.addingToBaseBoard = false;
//            this.
//        }
//    }


    //Make sure to pass in distances from the edges of the offical board as locations and rows.
    //This function is only good for White Vs Black games that are mirrored... to make other colors change "whitePieces to some other color in the passed in variable (same with black) and then transpose the array and overlay it on a larger board).
    public void rowSetUp(int[] locations, int[] rows, ChessPiece whitePieces, ChessPiece blackPieces, boolean addingToBaseBoard){

        for (int l: locations) {
            for (int r: rows) {
                // I need to add this odd part to prevent pieces from becoming the same piece
                ChessPiece newWhitePiece = new ChessPiece(whitePieces.getTeamColor(),whitePieces.getPieceType());
                ChessPiece newWhitePiece2 = new ChessPiece(whitePieces.getTeamColor(),whitePieces.getPieceType());
                ChessPiece newBlackPiece = new ChessPiece(blackPieces.getTeamColor(),blackPieces.getPieceType());
                ChessPiece newBlackPiece2 = new ChessPiece(blackPieces.getTeamColor(),blackPieces.getPieceType());
                //now the actual meat of the caculation
                ChessPosition posWhite = new ChessPosition(1+r,1+l);
                ChessPosition posBlack = new ChessPosition(ChessBoardSize-r,ChessBoardSize-l);
                ChessPosition posWhite2 = new ChessPosition(1+r,ChessBoardSize-l);
                ChessPosition posBlack2 = new ChessPosition(ChessBoardSize-r,1+l);
                //add all the pieces
                this.addPiece(posWhite,newWhitePiece, addingToBaseBoard);
                this.addPiece(posWhite2, newWhitePiece2, addingToBaseBoard);
                this.addPiece(posBlack, newBlackPiece, addingToBaseBoard);
                this.addPiece(posBlack2, newBlackPiece2, addingToBaseBoard);
            }

        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //throw new RuntimeException("Not implemented");
        squares[position.getRow()][position.getColumn()] = piece;
    }

    public void addPiece(ChessPosition position, ChessPiece piece, boolean addingToBaseBoard) {
        //throw new RuntimeException("Not implemented");
        if(!addingToBaseBoard) {
            addPiece(position,piece);
        } else {
            //For whatever reason we must update this when a new piece is created or the test cases will call our functions in a odd order that makes baseboard in a has moved position.
            ChessPiece updatePiece = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
            updatePiece.setHasMoved(false);
            baseboard[position.getRow()][position.getColumn()] = updatePiece;
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()][position.getColumn()];
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Gets a chess piece on the inital chessboard (this can be useful for determining if a peice has moved).
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getInitalPiece(ChessPosition position) {
        return baseboard[position.getRow()][position.getColumn()];
        //throw new RuntimeException("Not implemented");
    }


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (ChessPiece[] chessPieces : squares = baseboard) {
        }


        //throw new RuntimeException("Not implemented");

    }

    public int getChessBoardSize() {
        return ChessBoardSize;
    }
    public void setChessBoardSize(int chessBoardSize) {
        ChessBoardSize = chessBoardSize;
        squares = new ChessPiece[chessBoardSize+1][chessBoardSize+1];
        baseboard = new ChessPiece[chessBoardSize+1][chessBoardSize+1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        boolean truthValue = ChessBoardSize == that.ChessBoardSize && addingToBaseBoard == that.addingToBaseBoard;
        if (!truthValue) {
            return truthValue;
        } else {
            for (int currentRow = 1; currentRow < (ChessBoardSize+1); currentRow++) {
                for (int currentCol = 1; currentCol < (ChessBoardSize+1); currentCol++) {
                    if (!(this.squares[currentRow][currentCol] == null)) {
                        if(((ChessBoard) o).squares[currentRow][currentCol] == null) {
                            return false;
                        }
                        if (!(this.squares[currentRow][currentCol].equals(((ChessBoard) o).squares[currentRow][currentCol]))) {
                            return false;
                        }
                    }
                    if(!(this.baseboard[currentRow][currentCol] == null)) {
                        if(((ChessBoard) o).baseboard[currentRow][currentCol] == null) {
                            return false;
                        }
                        if (!(this.baseboard[currentRow][currentCol].equals(((ChessBoard) o).baseboard[currentRow][currentCol]))) {
                            //this.baseboard[currentRow][currentCol].equals(((ChessBoard) o).baseboard[currentRow][currentCol]);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(ChessBoardSize, addingToBaseBoard);
        result = 31 * result + Arrays.hashCode(baseboard);
        result = 31 * result + Arrays.hashCode(squares);
        return result;
    }
}

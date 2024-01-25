package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currentTeam;

    private ChessBoard board = new ChessBoard();

    private int currentTeamLocation = 0;

    private int lastTeamLocation = 2;

    private TeamColor[] moveOrder = new TeamColor[lastTeamLocation];


    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
        for(int i = currentTeamLocation; i < lastTeamLocation; i++){
            //Remeber to check for valid move orders when a new game is started up, otherwise players will wait forever here
            if(moveOrder[i] == null){
                i = 0;
                if(moveOrder[i] == null) {
                    break;
                }
            }
            //We have found the closest (sequentially) next turn of this side, we can now use this normal command for monster chess...
            if(moveOrder[i] == currentTeam) {
                currentTeamLocation = i;
                return;
            }
        }
        //throw new RuntimeException("Not implemented");
    }

    public void setTeamOrder(TeamColor[] teamOrder, int lastTeamLocation) {
        //Allow for a new game with a diffent possible team order
        int numberOfSwitches = lastTeamLocation;
        for(int i = 0; i<lastTeamLocation; i++) {
            if(teamOrder[i] == null){
                return;
            } else {
                moveOrder[i] = teamOrder[i];
            }
        }
    }
    //This function works with the one above to allow multiple move variants (like monster chess) and multiple team varaients
    private TeamColor getNextTeamTurn(){
        return moveOrder[nextTeamIndex()];
    }

    //helper function for above.
    private int nextTeamIndex (){
        if(currentTeamLocation+1 < lastTeamLocation) {
            if (moveOrder[currentTeamLocation + 1] != null) {
                return currentTeamLocation+1;
            }
        }
        return 0;
    }

    //Now to update everything
    private void updateTeam(){
        setTeamTurn(getNextTeamTurn());
        updateTeamIndex();
    }
    //UpdateTeamIndex
    private void updateTeamIndex(){
        currentTeamLocation = nextTeamIndex();
    }



    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //Other than setting things up this first part (up until the else) fulfills the last speck requirement above.
        ChessPiece testedPiece = board.getPiece(startPosition);
        if(testedPiece == null) {
            return null;
        } else {
            //We will need this later to see who to check for check on
            TeamColor teamToCheck = testedPiece.getTeamColor();

            //Now we have to unpack the return, we can keep ChessMove vauge here as we are just looking for chess positions right now.
            Collection<ChessMove> currentMoves = testedPiece.pieceMoves(board, startPosition);

            //Someone might want to do this at some point...
            if(teamToCheck == null) {
                return currentMoves;
            }

            //We will use this to update some start Positions
            ChessBoard checkBoard = setBoard(board, new ChessBoard());
            for (ChessMove M: currentMoves) {
                //I think this is best done here in two similar calls, I will make an unpack subfunction
                ChessPosition removePosition = M.getStartPosition();
                ChessPosition addPosition = M.getEndPosition();
                //now unpack each one
                checkBoard = unpack(removePosition, checkBoard, null);
                checkBoard = unpack(addPosition, checkBoard, testedPiece);

            }
        }
        //throw new RuntimeException("Not implemented");
    }

    //Here is the unpack function used twice above (and perhaps elsewhere). (It could help modify boards for abnormal games).
    public ChessBoard unpack(ChessPosition unwindPosition, ChessBoard checkBoard, ChessPiece replacementPiece){
        checkBoard.addPiece(unwindPosition, replacementPiece);
        return checkBoard;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(isInCheck(teamColor)) {
            if(isInStalemate(teamColor)) {
                return true;
            }
        }
        return false;
        // throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        //We do not need to use the return value, as it will already be correctly set...
        setBoard(board, this.board);
        //throw new RuntimeException("Not implemented");
    }

    //A helper function to let me reuse the function above in other contexts. (I will consider turning it private.
    public ChessBoard setBoard(ChessBoard board, ChessBoard newBoard) {
        int transform = board.getChessBoardSize();
        newBoard.setChessBoardSize(transform);
        for (int i = transform; i > 0; i--) {
            for (int j = transform; j > 0; j--) {
                ChessPosition currentPosition = new ChessPosition(i,j);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if(currentPiece != null) {
                    newBoard.addPiece(currentPosition, currentPiece, false);
                }
                currentPosition = new ChessPosition(i,j);
                currentPiece = board.getInitalPiece(currentPosition);
                if(currentPiece != null) {
                    newBoard.addPiece(currentPosition, currentPiece, true);
                }
            }

        }
        return newBoard;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
        //throw new RuntimeException("Not implemented");
    }
}

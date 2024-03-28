package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

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

    private int lastTeamLocation = 1;

    private TeamColor[] moveOrder = new TeamColor[lastTeamLocation+1];

    private TeamColor[][] alliances = new TeamColor[1][1];

    private int addingTeam = 0;

    private int largestTeam = 1;

    private boolean gameIsOver = false;


    public ChessGame() {
        //Assume that if no parameters were passed then there are two teams with one member each
        currentTeam = TeamColor.WHITE;
        moveOrder[currentTeamLocation] = currentTeam;
        moveOrder[currentTeamLocation+1] = TeamColor.BLACK;
        //(the next team to move is black, but the first one is white).

        //We do not need to do anything with alliances as adding team has not been updated to require it... (ie the player must instruct the system to have teams).

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
        //might need to change last parameter to ++i to make the breaks below work, just need to be sure the last statement will still be accessible probably not...
        for(int i = currentTeamLocation; i <= lastTeamLocation; i++){
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

    public boolean isGameOver() {
        //for now just these two players, I think I could easily iterate through the turn order to check everyone if this works.
        //the way I implemented isInStalemate it also checks for checkmate... so we are good.
        if(isInStalemate(TeamColor.WHITE)){
            gameOver();
        }
        if(isInStalemate(TeamColor.BLACK)){
            gameOver();
        }
        return gameIsOver;
    }

    public void gameOver() {
        this.gameIsOver = true;
    }


    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    //helper function to the function below it, takes parameters from it to see if they work only call it from that function and other functions that properly use team to check...
    private Collection<ChessMove> validateMoves(HashSet<ChessPosition> rulingPieces, ChessPiece testedPiece, boolean needToCheckChecks, boolean checkMovedPiece, ChessBoard checkBoard, Collection<ChessMove> currentMoves){
        Collection<ChessMove> finalMoves = new HashSet<ChessMove>();
        for (ChessMove M: currentMoves) {
            //keep track of if we should add this
            boolean validMove = true;
            //I think this is best done here in two similar calls, I will make an unpack subfunction
            ChessPosition removePosition = M.getStartPosition();
            ChessPosition addPosition = M.getEndPosition();
            ChessPiece oldPiece = checkBoard.getPiece(addPosition);
            ChessPiece checkedPiece = checkBoard.getPiece(removePosition);
            //now unpack each one
            checkBoard = unpack(removePosition, checkBoard, null);
            checkBoard = unpack(addPosition, checkBoard, testedPiece);



            //This particular setting will allow for moving into check a few turns later (just not the next turn)
            if(needToCheckChecks) {
                if(isInCheck(currentTeam, checkBoard, rulingPieces)){
                    validMove = false;
                }
            }
            if(checkMovedPiece){
                HashSet<ChessPosition> rulerMoves = new HashSet<ChessPosition>();
                rulerMoves.add(addPosition);
                if(isInCheck(currentTeam, checkBoard, rulerMoves)){
                    validMove = false;
                } else {
                    isInCheck(currentTeam, checkBoard, rulerMoves);
                }
            }
            if(validMove){

                finalMoves.add(M);
            }
            checkBoard = unpack(addPosition, checkBoard, oldPiece);
            checkBoard = unpack(removePosition, checkBoard, checkedPiece);

        }
        return finalMoves;
    }


    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //this is for later
        TeamColor whoIsMoving = currentTeam;
        if(currentTeam == TeamColor.WHITE){
            int j = 17;
        }
        boolean needToCheckChecks = true;
        boolean checkMovedPiece = false;
        //Other than setting things up this first part (up until the else) fulfills the last speck requirement above.
        ChessPiece testedPiece = board.getPiece(startPosition);
        if (testedPiece == null) {
            return null;
        } else {
            //We will need this later to see who to check for check on
            TeamColor teamToCheck = testedPiece.getTeamColor();
            //We need to know where the kings/similar pieces are for later
            HashSet<ChessPosition> rulingPieces = board.findRulingPieces(teamToCheck);
            if (rulingPieces.isEmpty()) {
                needToCheckChecks = false;
            } else if (rulingPieces.contains(startPosition)) {
                checkMovedPiece = true;
                rulingPieces.remove(startPosition);
                if (rulingPieces.isEmpty()) {
                    needToCheckChecks = false;
                }
            }
            //Remeber this
            TeamColor oldTeamTurn = currentTeam;
            int currentMoveIndex = currentTeamLocation;

            //Now we have to unpack the return, we can keep ChessMove vauge here as we are just looking for chess positions right now.
            Collection<ChessMove> currentMoves = testedPiece.pieceMoves(board, startPosition);


            //Someone might want to do this at some point...
            if (teamToCheck == null) {
                return currentMoves;
            } else {
                setTeamTurn(teamToCheck);
            }

            //We will use this to update some start Positions
            ChessBoard checkBoard = setBoard(board, new ChessBoard());
            Collection<ChessMove> finalMoves = validateMoves(rulingPieces, testedPiece, needToCheckChecks, checkMovedPiece, checkBoard, currentMoves);
            //throw new RuntimeException("Not implemented");
            if(finalMoves.isEmpty()){
                return finalMoves;
            } else {
                Collection<ChessMove> phaseTwo = specialMoves(finalMoves, checkBoard,startPosition);
                if(phaseTwo == null){
                    setTeamTurn(whoIsMoving);
                    return finalMoves;
                }else if(phaseTwo.isEmpty()){
                    setTeamTurn(whoIsMoving);
                    return finalMoves;
                } else {
                    Collection<ChessMove> phaseTwoValidated = validateMoves(rulingPieces, testedPiece, needToCheckChecks, checkMovedPiece, checkBoard, phaseTwo);
                    if(phaseTwoValidated.isEmpty()){
                        setTeamTurn(whoIsMoving);
                        return finalMoves;
                    } else {
                        for (ChessMove m: phaseTwoValidated) {
                            finalMoves.add(m);
                        }
                        setTeamTurn(whoIsMoving);
                        return finalMoves;
                    }
                }
            }
        }
    }

    private Collection<ChessMove> specialMoves(Collection<ChessMove> validMoves, ChessBoard boardToBeModified, ChessPosition placePieceIs){
       ChessPiece pieceToCheck = boardToBeModified.getPiece(placePieceIs);
       boolean somethingWasAdded = false;
        for (ChessMove m: validMoves) {
            if(pieceToCheck.shouldIActivateSpecialMove(m)){
                pieceToCheck.activateSpecialMove(boardToBeModified, placePieceIs, pieceToCheck.getPieceType(),m);
                somethingWasAdded = true;
            }
        }
        if(somethingWasAdded){
            return pieceToCheck.specialPieceMoves();
        } else {
            return null;
        }

    }


    //Here is the unpack function used twice above (and perhaps elsewhere). (It could help modify boards for abnormal games).
    public ChessBoard unpack(ChessPosition unwindPosition, ChessBoard checkBoard, ChessPiece replacementPiece){
        checkBoard.addPiece(unwindPosition, replacementPiece);
        //Oddly enough we did not implement this well enough in our piece and board class, but it makes the most sense to implement here...
        //As long as you are willing to look for it, this way hypothetical moves will not change has moved, but only real ones
        //If you want to add a piece in the wrong place that has not moved use the next version of this
        return checkBoard;
    }

    //For actual moves, include the boolean has moved as true!
    public ChessBoard unpack(ChessPosition unwindPosition, ChessBoard checkBoard, ChessPiece replacementPiece, boolean hasMoved){
        checkBoard.addPiece(unwindPosition, replacementPiece);
        //Oddly enough we did not implement this well enough in our piece and board class, but it makes the most sense to implement here...
        //As long as you are willing to look for it, this way hypothetical moves will not change has moved, but only real ones
        //Use this version to set the hasMoved...
        //only update the hasMoved on non-null pieces
        if(checkBoard.getPiece(unwindPosition) != null) {
            checkBoard.getPiece(unwindPosition).setHasMoved(hasMoved);
        }
        return checkBoard;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //throw new RuntimeException("Not implemented");
        if(move == null) {
            throw new InvalidMoveException();
        }
        //set up for the turn in case other public functions were abused to change who's turn it is...
        setTeamTurn(currentTeam);
        int teamToRemeber =  currentTeamLocation;
        //currentTeam = moveOrder[teamToRemeber];
        try{
            ChessPosition piecePosition = move.getStartPosition();
            ChessPiece pieceToTest = board.getPiece(piecePosition);
            //currentTeam = moveOrder[teamToRemeber];
            if(pieceToTest.getTeamColor() == currentTeam) {
            Collection<ChessMove> allowedMoves = validMoves(piecePosition);
            for (ChessMove m: allowedMoves) {
                //If any move works we need to avoid implementing the stuff at the end (which throws an error).
                if(m.equals(move)){
                    //Before we do anything, it is our turn again so we can no longer have an in-passing move take us in between turns... unless we make a move that allows that
                    //We should not have to worry about null HashSets, because the function above returned a move, so there must have been a piece on this team to move.
                    HashSet<ChessPosition> piecePositions = board.returnAllPiecesOnTeam(currentTeam);
                    for (ChessPosition p: piecePositions) {
                        board.getPiece(p).resetDeathMoves();
                    }

                    //This code is remarkably similar to the code in valid move.
                    //However rather than have valid move call this function that in turn calls valid move
                    //or having some strange third function to do the work for these two functions together,
                    //I determined this was the least confusing not totally inefficient solution.

                    //Get the positions we need to modify...
                    ChessPosition removePosition = m.getStartPosition();
                    ChessPosition addPosition = m.getEndPosition();
                    ChessPiece checkedPiece = board.getPiece(removePosition);
                    //if there is a premotion make the switch now
                    if(m.getPromotionPiece() != null) {
                        //if you want to implement early chess promote to enemy piece rules you will have to make a transforming piece type in piece (like a piece that calls set color on itself if it makes a move to the end of the board).
                    checkedPiece = new ChessPiece(checkedPiece.getTeamColor(), m.getPromotionPiece());
                    }
                    //check if we made a special move first
                    boolean moveWasSpecial = false;
                    if(pieceToTest.specialPieceMoves() != null) {
                        for (ChessMove M : pieceToTest.specialPieceMoves()) {
                            if (M.equals(move)) {
                                moveWasSpecial = true;
                            }
                        }
                    }
                    if(moveWasSpecial){
                        //move the second piece in this move first for simplicity, as it does not matter the move order (it was already legally checked)
                        //Iterate through the allied team looking for moves first...
                        HashSet<ChessPosition> chessTeam = board.returnAllPiecesOnTeam(currentTeam);
                        if(chessTeam != null) {
                            if(!chessTeam.isEmpty()){
                                for (ChessPosition teamMember : chessTeam) {
                                    ChessPiece member = board.getPiece(teamMember);
                                    boolean keepItUp = member.shouldIActivateSpecialMove(move);
                                    if(keepItUp){
                                        Collection<ChessMove> newMoves = member.specialPieceMoves();
                                        //There had better only be one of these, as the player cannot select from a list of them...
                                        for (ChessMove extra: newMoves) {
                                            //I unpack the movement before the removal so that self-deletion moves are possible.
                                            unpack(extra.getEndPosition(), board, member, true);
                                            unpack(extra.getStartPosition(), board, null, true);
                                        }
                                    }
                                }
                            }
                            //We will either update this team or every other team not both (this should only really be a problem in several player games...
                        } else {
                            do{
                                currentTeamLocation++;
                                if (currentTeamLocation > lastTeamLocation) {
                                    currentTeamLocation = 0;
                                }
                                if (moveOrder[currentTeamLocation] == null) {
                                    for (currentTeamLocation = currentTeamLocation; currentTeamLocation <= lastTeamLocation; currentTeamLocation++) {
                                        if (moveOrder[currentTeamLocation] != null) {
                                            currentTeam = moveOrder[currentTeamLocation];
                                            break;
                                            //If this worked return before we throw an error.
                                        }
                                    }
                                } else {
                                    currentTeam = moveOrder[currentTeamLocation];
                                    break;
                                }
                                chessTeam = board.returnAllPiecesOnTeam(currentTeam);
                                if(chessTeam != null) {
                                    if(!chessTeam.isEmpty()){
                                        for (ChessPosition teamMember : chessTeam) {
                                            ChessPiece member = board.getPiece(teamMember);
                                            boolean keepItUp = member.shouldIActivateSpecialMove(move);
                                            if(keepItUp){
                                                Collection<ChessMove> newMoves = member.specialPieceMoves();
                                                //There had better only be one of these, as the player cannot select from a list of them...
                                                for (ChessMove extra: newMoves) {
                                                    //To allow for opesent mearly have the move given be a move from the square you are on to it and it will delete yourself.
                                                    unpack(extra.getEndPosition(), board, member, true);
                                                    unpack(extra.getStartPosition(), board, null, true);
                                                }
                                            }
                                        }
                                    }
                                }
                            } while(currentTeamLocation != teamToRemeber);
                        }
                        currentTeamLocation = teamToRemeber;
                        currentTeam = moveOrder[currentTeamLocation];
                    }

                    //now unpack each one
                    unpack(addPosition, board, checkedPiece, true);
                    unpack(removePosition, board, null, true);
                    //return;
                    //If this worked return before we throw an error.
                    //remeber where we were and update

//                    currentTeamLocation++;
//                    if(currentTeamLocation > lastTeamLocation) {
//                        currentTeamLocation = 0;
//                    }
//                        if(moveOrder[currentTeamLocation] == null){
//                            for(currentTeamLocation = currentTeamLocation; currentTeamLocation <= lastTeamLocation; currentTeamLocation++){
//                                if(moveOrder[currentTeamLocation] != null) {
//                                    currentTeam = moveOrder[currentTeamLocation];
//
//                                    return;
//                                    //If this worked return before we throw an error.
//                                }
//                            }
//                        }
//                        else {
                            //if(moveWasSpecial) {
                                do{
                                    currentTeamLocation++;
                                    if (currentTeamLocation > lastTeamLocation) {
                                        currentTeamLocation = 0;
                                    }
                                    if (moveOrder[currentTeamLocation] == null) {
                                        for (currentTeamLocation = currentTeamLocation; currentTeamLocation <= lastTeamLocation; currentTeamLocation++) {
                                            if (moveOrder[currentTeamLocation] != null) {
                                                currentTeam = moveOrder[currentTeamLocation];
                                                break;
                                                //If this worked return before we throw an error.
                                            }
                                        }
                                    } else {
                                        currentTeam = moveOrder[currentTeamLocation];
                                        //break;
                                    }
                                    Collection<ChessPosition> chessTeam = board.returnAllPiecesOnTeam(currentTeam);
                                    //if(currentTeam == TeamColor.WHITE){
                                        //int kul = 21;
                                    //}
                                    if(chessTeam != null) {
                                        if(!chessTeam.isEmpty()){
                                            for (ChessPosition teamMember : chessTeam) {
                                                ChessPiece member = board.getPiece(teamMember);
                                                if(member.shouldIDie(move)){
                                                    //kill them
                                                    board.addPiece(teamMember, null);
                                                } else {
                                                boolean keepItUp = member.shouldIActivateSpecialMove(move);
                                                if(keepItUp){
                                                    //We might need to use it at some future date...
                                                boolean unused = member.shouldIActivateSpecialMove(move, board, teamMember);
                                                } else {
                                                    member.setSpecialMoves(false, currentTeam);
                                                    //member.resetDeathMoves();
                                                }
                                                }
                                            }
                                        }
                                    }
                                } while(currentTeamLocation != teamToRemeber);
                            //}
                            currentTeam = moveOrder[teamToRemeber];
                    currentTeamLocation++;
                    if(currentTeamLocation > lastTeamLocation) {
                        currentTeamLocation = 0;
                    }
                        if(moveOrder[currentTeamLocation] == null){
                            for(currentTeamLocation = currentTeamLocation; currentTeamLocation <= lastTeamLocation; currentTeamLocation++){
                                if(moveOrder[currentTeamLocation] != null) {
                                    currentTeam = moveOrder[currentTeamLocation];
                                    return;
                                   //If this worked return before we throw an error.
                               }
                           }
                        }
                            currentTeam = moveOrder[currentTeamLocation];
                            return;
  //                      }



                }
                //throw new InvalidMoveException();
            }
        }
            //might want to change to exception e as we are most likely to have the null move passed exception...
            //Now I have solved that with a simple if statement at the beginning of this function...
        }catch (Exception  InvalidMoveException) {
            //if we did not return yet we had an error and need to correct it.
            currentTeamLocation = (teamToRemeber);
            currentTeam = moveOrder[currentTeamLocation];
            throw new InvalidMoveException();
        }
        currentTeamLocation = (teamToRemeber);
        currentTeam = moveOrder[currentTeamLocation];
        throw new InvalidMoveException();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        HashSet<ChessPosition> rulingPieces = board.findRulingPieces(teamColor);
        //No King or similar piece, no check
        if (rulingPieces.isEmpty()) {
            return false;
        } else {
            return isInCheck(teamColor, board, rulingPieces);
        }
    }
    //here is a helper function
    public boolean isInCheck(TeamColor teamColor, ChessBoard checkBoard, HashSet<ChessPosition> KingPositions){
        if(checkBoard == null) {
            return false;
        }
        int numberOfIterations = checkBoard.getChessBoardSize();
        for(int row = 1; row<=numberOfIterations; row++){
            for(int col = 1; col<=numberOfIterations; col++){
                ChessPosition testPosition = new ChessPosition(row, col);
                ChessPiece testingPiece = checkBoard.getPiece(testPosition);
                if(testingPiece == null) {} else if(isAlliedPiece(testingPiece.getTeamColor(),teamColor)){} else {
                    for (ChessPosition kingPosition: KingPositions) {
                        //This will make multiple kings be a vulnerability, not a benifit, to change this you could have a counter tracking all kings on is in checkmate.
                    if(testingPiece.pieceMoves(checkBoard,testPosition) != null) {
                        var move = testingPiece.pieceMoves(checkBoard, testPosition);
                        if (!move.isEmpty()) {
                            for (ChessMove m : move) {
                                if (m.getEndPosition().equals(kingPosition)) {
                                    return true;
                                }
                            }
                        }
                    }


                    }
                }
            }
        }
        return false;
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
        HashSet<ChessPosition> MoveSetToCheck = board.returnAllPiecesOnTeam(teamColor);
        if(MoveSetToCheck.isEmpty()) {
            return true;
        }
        Collection<ChessMove> allMoves = new HashSet<ChessMove>();
        for (ChessPosition p: MoveSetToCheck) {
            Collection<ChessMove> moveSet = validMoves(p);
            for (ChessMove m: moveSet) {
                allMoves.add(m);
            }
        }
        if(allMoves.isEmpty()){
            return true;
        }
        return false;
        //throw new RuntimeException("Not implemented");
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

    //A helper function to let me reuse the function above in other contexts. (I will consider turning it private).
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
    //now different players can be on the same team

    public void setAlliances(TeamColor[] alliedTeams) {
        int i = 0;
        for (TeamColor player: alliedTeams) {
            i++;
        }
        int newLargestTeam = largestTeam;
        if (i>largestTeam-1){
            newLargestTeam = i;
        }
        TeamColor[][] alliancesCopy = new TeamColor[addingTeam][newLargestTeam];
        for(int j = i-1; j>=0; j--) {
            if(alliedTeams[j] != null){
                alliancesCopy[addingTeam][j] = alliedTeams[j];
            }
        }
        //work here
        if(addingTeam > 0) {
            for (int j = 0; j < (addingTeam - 1); j++) {
                for (int k = 0; j < largestTeam; k++) {
                    if (alliances[j][k] != null) {
                        alliancesCopy[j][k] = alliances[j][k];
                    } else {
                        alliancesCopy[j][k] = null;
                    }
                }
            }
        }
        addingTeam++;
        largestTeam = newLargestTeam;
        for (int j = 0; j < addingTeam; j++) {
            for (int k = 0; j < largestTeam; k++) {
                if (alliancesCopy[j][k] != null) {
                    alliances[j][k] = alliancesCopy[j][k];
                } else {
                    alliances[j][k] = null;
                }
            }
        }
    }

    //how to make sure you do not take yourself only call on a REAL piece
    public boolean isAlliedPiece(TeamColor firstPiece, TeamColor secondPiece){
        //most used function here
        if(firstPiece == secondPiece){
            return true;
        }
        //second most used
         else if(addingTeam == 0) {
            return false;
        }
        //now the crazy stuff
        boolean isAnAlly = false;

        for (int i = 0; i < addingTeam; i++) {
            boolean firstTeamPresent = false;
            boolean secondTeamPresent = false;
            for(int j = 0; j<largestTeam; j++) {
                if(alliances[i][j] != null){
                    if(alliances[i][j] == firstPiece){
                        firstTeamPresent = true;
                    }
                    if(alliances[i][j] == secondPiece){
                        secondTeamPresent = true;
                    }
                }
            }
            if(firstTeamPresent && secondTeamPresent){
                isAnAlly = true;
            }
        }
        return isAnAlly;

    }

}

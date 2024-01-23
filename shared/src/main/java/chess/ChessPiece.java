package chess;

import java.util.*;

import static chess.ChessPiece.PieceType.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    //private ChessBoard firstMoveChecker = new ChessBoard();

    public boolean hasThisPieceMoved(ChessPosition currentPosition, ChessBoard currentBoard) {
        //first we need to get the rest board checker from currentBoard
        ChessPiece peiceToTestAgainst = currentBoard.getInitalPiece(currentPosition);
        if(hasMoved) {
            return hasMoved;
        } else if (peiceToTestAgainst == null) {
            hasMoved = true;
            return hasMoved;
        } else if (this.type!=peiceToTestAgainst.getPieceType()){
            hasMoved = true;
            return hasMoved;
        } else if (this.pieceColor!=peiceToTestAgainst.getTeamColor()){
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
        //this.firstMoveChecker.resetBoard();
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
     * This is a surprisingly important function that determines if premotions are possible.
     * @return what pieces this piece can premote to.
     */
    private List<PieceType> canPremoteTo() {
        //First initialize the list
        List<PieceType> premotions = new ArrayList<>();
        //I think I should only include pieces that can premote here otherwise default is none.
        switch(type) {
            case PAWN:
            premotions.add(QUEEN);
            premotions.add(BISHOP);
            premotions.add(ROOK);
            premotions.add(KNIGHT);
            break;
            default:
                break;
        }
        return premotions;
    }

   //In the future we could get creative with how we determine premotion locations, but for now we can assume they are the ends of the board.
    private ArrayList<ChessPosition>findPremotionLocations(ChessBoard board){
        //First initialize the array.
        ArrayList<ChessPosition> premotionLocations = new ArrayList<ChessPosition>();
        //switch statement to allow multiple different teams in more than 2 player game.
        switch(pieceColor) {
            //Where White premotes (right now it assumes normal pawn premotions is all there is... could complicate it if I want
            case WHITE:
                int finalRank = board.getChessBoardSize();
                //If I make a non-square board, not only will I have to edit my code in ChessBoard and the edge calculater, but I will have
                //to update the middle part of this for loop to calculate for the correct number of squares on a rectangle at the end.
                for(int i = 0; i < finalRank; i++) {
                    ChessPosition premotionSquare = new ChessPosition(finalRank, i);
                    premotionLocations.add(premotionSquare);
                }
            break;
            //Where Black premotes (right now it assumes normal pawn premotions is all there is... could complicate it if I want
            case BLACK:
                //The different name for this basically identical number for tracking things opitimizes why I will have to change this for non-square boards.
                int finalColumn = board.getChessBoardSize();
                //here I update again...
                for(int i = 0; i < finalColumn; i++){
                    //only this line is currently different than above (and only barely).
                    ChessPosition premotionSquare = new ChessPosition(0, finalColumn);
                    premotionLocations.add(premotionSquare);
                }
            break;
            default:
            break;
        }
        return premotionLocations;
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
     * decides how to set up which peice to caculate movesets for
     * @return Collection of valid positions
     */
    private ArrayList<ChessPosition> whatIsMoving(ArrayList<ChessPosition> myMoves, ChessBoard board, ChessPosition myPosition, boolean canAttack, boolean canMove) {
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
        if(canAttack && canMove) {
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
            //Interestingly the knight only moves a single time in the given direction so the booleans above are only useful if you want to add knightriders
            case KNIGHT:
                Knightup++;
                Knightdown++;
                Knightleft++;
                Knightright++;
                Knightdrup++;
                Knightdlup++;
                Knightdrdo++;
                Knightdldo++;
                break;
            //Oddly enough the pawn can always only move to or attack to a space, never both to a space.
            case PAWN:
                break;
            default:
                break;
        }
        // The following cases are for if the piece can only attack or only move
        } else if (canMove) {
            switch(type) {
                //might want to add stuff here for castleing logic
                case KING:
                    break;
                case QUEEN:
                    break;
                case ROOK:
                    break;
                case KNIGHT:
                    break;
                case BISHOP:
                    break;
                    //For the PAWN I have to add an inner switch case to see if it is a black or white (or other colored) pawn then give rules.
                case PAWN:
                    switch (pieceColor) {
                        case WHITE -> {
                            //Oddly enough this non-preconfiqured code is probably best if I want to do things like add new pawn colors, as it would be hard to paramiterize the directions given how everything else is set up and easy to duplicate this code, which really should not fundamentally change (just make a new piece type if you want to do that).
                            Up++;
                            if (!hasThisPieceMoved(myPosition, board)) {
                                Up++;
                            }
                        }
                        case BLACK -> {
                            Down++;
                            if (!hasThisPieceMoved(myPosition, board)) {
                                Down++;
                            }
                        }
                        default -> {
                        }
                    }

                break;
                default:
                break;
            }
        } else if (canAttack) {
            switch (type) {
                case KING:
                    break;
                case QUEEN:
                    break;
                case ROOK:
                    break;
                case KNIGHT:
                    break;
                case BISHOP:
                    break;
                //might want to add some stuff here for opepsent logic
                case PAWN:
                    //Here we add the switch idea from above...
                    switch (pieceColor) {
                        case WHITE -> {
                            //Oddly enough this non-preconfiqured code is probably best if I want to do things like add new pawn colors, as it would be hard to paramiterize the directions given how everything else is set up and easy to duplicate this code, which really should not fundamentally change (just make a new piece type if you want to do that).
                            Drup++;
                            Dlup++;
                        }
                        case BLACK -> {
                            Drdo++;
                            Dldo++;
                        }
                        default -> {
                        }
                    }
                    break;
                default:
                    break;
            }
        } else {
            return null;
        }
        //If we got here we either can attack, move or attack/move, so we should now execute the code to check where those moves send us.
    myMoves = whereCanIGoInfinite(myMoves, board, myPosition, canAttack, canMove, up, down, left, right, drup, dlup, drdo, dldo, knightup, knightdown, knightleft, knightright, knightdrup, knightdlup, knightdrdo, knightdldo);
    myMoves = whereCanIGoFinite(myMoves, board, myPosition, canAttack, canMove, Up, Down, Left, Right, Drup, Dlup, Drdo, Dldo, Knightup, Knightdown, Knightleft, Knightright, Knightdrup, Knightdlup, Knightdrdo, Knightdldo);
    return myMoves;
    }

    /**
     * calculates possible moves based upon inputs, either for infinite movesets
     * @return Collection of valid positions
     */
    private ArrayList<ChessPosition> whereCanIGoFinite(ArrayList<ChessPosition> myMoves, ChessBoard board, ChessPosition myPosition, boolean canAttack, boolean canMove, int Up, int Down, int Left, int Right, int Drup, int Dlup, int Drdo, int Dldo, int Knightup, int Knightdown, int Knightleft, int Knightright, int Knightdrup, int Knightdlup, int Knightdrdo, int Knightdldo){
        //At bare minimum the King needs all of these, so I should keep them.
        if (Up >= 1){
            myMoves = Move(board, canMove, canAttack, 1, 0, myPosition, myMoves, Up);
        }
        if (Down >= 1){
            myMoves = Move(board, canMove, canAttack, -1, 0, myPosition, myMoves, Down);
        }
        if (Left >= 1){
            myMoves = Move(board, canMove, canAttack, 0, -1, myPosition, myMoves, Left);
        }
        if (Right >= 1){
            myMoves = Move(board, canMove, canAttack, 0, 1, myPosition, myMoves, Right);
        }
        if (Drup >= 1){
            myMoves = Move(board, canMove, canAttack, 1, 1, myPosition, myMoves, Drup);
        }
        if (Drdo >= 1){
            myMoves = Move(board, canMove, canAttack, -1, 1, myPosition, myMoves, Drdo);
        }
        if (Dlup >= 1){
            myMoves = Move(board, canMove, canAttack, 1, -1, myPosition, myMoves, Dlup);
        }
        if (Dldo >= 1){
            myMoves = Move(board, canMove, canAttack, -1, -1, myPosition, myMoves, Dldo);
        }
        //This next series of ifs actually does apply to the real knight so it is important this time
        if (Knightup >= 1){
            myMoves = Move(board, canMove, canAttack, 2, 1, myPosition, myMoves, Knightup);
        }
        if (Knightdown >= 1){
            myMoves = Move(board, canMove, canAttack, -2, 1, myPosition, myMoves, Knightdown);
        }
        if (Knightleft >= 1){
            myMoves = Move(board, canMove, canAttack, 1, -2, myPosition, myMoves, Knightleft);
        }
        if (Knightright >= 1){
            myMoves = Move(board, canMove, canAttack, 1, 2, myPosition, myMoves, Knightright);
        }
        if (Knightdrup >= 1){
            myMoves = Move(board, canMove, canAttack, 2, -1, myPosition, myMoves, Knightdrup);
        }
        if (Knightdrdo >= 1){
            myMoves = Move(board, canMove, canAttack, -2, -1, myPosition, myMoves, Knightdrdo);
        }
        if (Knightdlup >= 1){
            myMoves = Move(board, canMove, canAttack, -1, -2, myPosition, myMoves, Knightdlup);
        }
        if (Knightdldo >= 1){
            myMoves = Move(board, canMove, canAttack, -1, 2, myPosition, myMoves, Knightdldo);
        }
        return myMoves;
    }

        //Check all the move conditions
    /**
     * calculates possible moves based upon inputs, either for infinite movesets
     * @return Collection of valid positions
     */
    private ArrayList<ChessPosition> whereCanIGoInfinite(ArrayList<ChessPosition> myMoves, ChessBoard board, ChessPosition myPosition, boolean canAttack, boolean canMove, boolean up, boolean down, boolean left, boolean right, boolean drup, boolean dlup, boolean drdo, boolean dldo, boolean knightup, boolean knightdown, boolean knightleft, boolean knightright, boolean knightdrup, boolean knightdlup, boolean knightdrdo, boolean knightdldo) {

        //Check all the move conditions
        if (up){
            myMoves = Move(board, canMove, canAttack, 1, 0, myPosition, myMoves);
        }
        if (down){
            myMoves = Move(board, canMove, canAttack, -1, 0, myPosition, myMoves);
        }
        if (left){
            myMoves = Move(board, canMove, canAttack, 0, -1, myPosition, myMoves);
        }
        if (right){
            myMoves = Move(board, canMove, canAttack, 0, 1, myPosition, myMoves);
        }
        if (drup){
            myMoves = Move(board, canMove, canAttack, 1, 1, myPosition, myMoves);
        }
        if (drdo){
            myMoves = Move(board, canMove, canAttack, -1, 1, myPosition, myMoves);
        }
        if (dlup){
            myMoves = Move(board, canMove, canAttack, 1, -1, myPosition, myMoves);
        }
        if (dldo){
            myMoves = Move(board, canMove, canAttack, -1, -1, myPosition, myMoves);
        }
        //This next series of ifs is for the more complicated knight moves and thus will only be used if I add fantasy pieces.
        if (knightup){
            myMoves = Move(board, canMove, canAttack, 2, 1, myPosition, myMoves);
        }
        if (knightdown){
            myMoves = Move(board, canMove, canAttack, -2, 1, myPosition, myMoves);
        }
        if (knightleft){
            myMoves = Move(board, canMove, canAttack, 1, -2, myPosition, myMoves);
        }
        if (knightright){
            myMoves = Move(board, canMove, canAttack, 1, 2, myPosition, myMoves);
        }
        if (knightdrup){
            myMoves = Move(board, canMove, canAttack, 2, -1, myPosition, myMoves);
        }
        if (knightdrdo){
            myMoves = Move(board, canMove, canAttack, -2, -1, myPosition, myMoves);
        }
        if (knightdlup){
            myMoves = Move(board, canMove, canAttack, -1, -2, myPosition, myMoves);
        }
        if (knightdldo){
            myMoves = Move(board, canMove, canAttack, -1, 2, myPosition, myMoves);
        }
        return myMoves;
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
        ArrayList<ChessPosition> myMoves = new ArrayList<>();
        boolean canAttack = true;
        boolean canMove = true;
        //now we need to caculate moves under four conditions, when you can move/attack, when you can just move and when you can just attack. (I can add when you cannot do any of them for opestant/other special moves).
        myMoves = whatIsMoving(myMoves, board, myPosition, canAttack, canMove);
        canAttack = false;
        myMoves = whatIsMoving(myMoves, board, myPosition, canAttack, canMove);
        canAttack= true;
        canMove = false;
        myMoves = whatIsMoving(myMoves, board, myPosition, canAttack, canMove);



        //Check to see if this piece can premote
        List<PieceType> availablePremotions = canPremoteTo();

        //Now we need to make a collection of ChessMoves and fill it with a combination of our moves followed by where we move from
        Collection<ChessMove> possibleMoves = new HashSet<ChessMove>();
        //This needs to be here to allow for premotions

        //If there are available premotions, find out where.
        if(availablePremotions.size() > 0) {
            ArrayList<ChessPosition> premotionLocations = findPremotionLocations(board);
            //As long as I never premote an unpremoteable piece, this should work fine
            for(ChessPosition position: myMoves){
                for (ChessPosition premotion:premotionLocations) {
                    if(position.equals(premotion)) {
                        myMoves.remove(position);
                        for (PieceType p:availablePremotions) {
                            possibleMoves.add(new ChessMove(myPosition, position, p));
                        }

                    }
                }
            }
        }

        //We already handeled (only) premotions above, so this should work for all nonpremoted peices.
        for (ChessPosition p: myMoves) {
            possibleMoves.add(new ChessMove(myPosition, p, null));
        }
        return possibleMoves;
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
        //First double check that we are not on top of a peice (we can add an input to allow multiple takes later if we want.
        ChessPiece possibleObstruction = board.getPiece(myPosition);
        //our code works just fine for not allowing moves unto friendly pieces so we do not need to worry about friendlies here.
        if(possibleObstruction != null) {
            //we do need to worry about if our last iterative move was a take.
            if (possibleObstruction.pieceColor != this.pieceColor) {
                return null;
            }
        }
        //Otherwise keep going...

            // Calculate where the next move position would be
            int myNewXPosition = myPosition.getColumn() + deltaX;
            int myNewYPosition = myPosition.getRow() + deltaY;
            ChessPosition nextValidSpace = new ChessPosition(myNewYPosition, myNewXPosition);
            // We will not ask for spaces that are out of bounds
            if (((myNewXPosition > board.getChessBoardSize()) || (myNewYPosition > board.getChessBoardSize())) || ((myNewXPosition <= 0) || (myNewYPosition <= 0))) {
                return null;
                // next we will have to see if we can move to an empty space if the space is empty or a full space if it is full
            } else if (board.getPiece(nextValidSpace) == null) {
                if (canMove) {
                    return nextValidSpace;
                } else {
                    return null;
                }
            } else {
                ChessPiece obstruction = board.getPiece(nextValidSpace);
                if (obstruction.getTeamColor() == this.pieceColor) {
                    //change this statement by adding an inner statement and another boolean in this and the two functions that call it if you want to allow for peice switching or suicide.
                    return null;
                } else if (canAttack) {
                    return nextValidSpace;
                } else {
                    return null;
                }
            }

    }

    //In order for the system to realize that we have equivlent chessboards, we need a way for it to see that we have equivlient chess peices in them


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPiece that)) return false;
        return hasMoved == that.hasMoved && pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasMoved, pieceColor, type);
    }
}

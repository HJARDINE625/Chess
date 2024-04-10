package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class WebSocketHandler {

    private WebSocketFacade myWebSocket;

    private String myName;

    private int gameID;

    private ChessGame.TeamColor player;

    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    private ChessGame thisGame;

    public WebSocketHandler(String url, NotificationHandler notificationHandler, String myName, int gameID, ChessGame.TeamColor myColor) throws ReportingException {
        myWebSocket = new WebSocketFacade(url, notificationHandler);
        this.myName = myName;
        player = myColor;
        this.gameID = gameID;
    }

    //may have to add more of these if I add more colors later.
    private boolean amIBlack(){
        if(player == null){
            return false;
        } else {
            if (player == ChessGame.TeamColor.BLACK) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void getNextCommand(){
        ConsoleInput userInterface = new ConsoleInput();
        //ask for more commands
        //helpWords();
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print("Enter the number of an in game command or press 0 for help!\n");
        int nextCommand = userInterface.getNum();
        caculateNextCommand(nextCommand, userInterface);

    }

    //for getting chessspaces
    private ChessPosition getChessPosition(ConsoleInput userInterface){
        out.print(SET_TEXT_BOLD);
        out.print(SET_BG_COLOR_GREEN);
        int myRow = 0;
        boolean letterWorks = false;
        while(!letterWorks) {
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
            out.print("Please enter the row of the position as a letter\n");
            String row = userInterface.getString();
            myRow = letterFinder(row);
            if(myRow >= 0){
             letterWorks = true;
            } else {
                out.print(SET_BG_COLOR_YELLOW);
                out.print(SET_TEXT_COLOR_RED);
                out.print("Not a valid row! Try again!\n");
            }
        }
        out.print("Please enter the row of the column as a number\n");
        int column = userInterface.getNum();
        //this should work!
        return new ChessPosition(myRow, column);
    }

    //do the oppisit of this function to get the equivilent number.
    private String letterFiller(int location){
        String letterConversion = "";
        int intermediate = location/26;
        if(intermediate >= 1){
            letterConversion = letterFiller(intermediate);
        }
        int added = location%26;
        char starterLetter = 'A';
        char newLetter = (char) (starterLetter + added);
        letterConversion = letterConversion + newLetter;
        return letterConversion;
    }
    //here is the number function

    private int letterFinder(String letter){
        String letters = new String();
        if(letter.matches("[\n\t a-zA-Z]+")) {
            //if we only have those characters we just need to remove every character that is not a letter to get a useful string (when we change it to uppercase).
            for(int i = 0; i < letter.length(); i++){
                if(letter.charAt(i) != ' '){
                    if(letter.charAt(i) != '\n'){
                        if(letter.charAt(i) != '\t'){
                            letters = letters + letter.charAt(i);
                        }
                    }
                }
            }
            String lettersToFind = letters.toUpperCase();
            int currentPositionTest = 0;
            while (true) {
                if(letterFiller(currentPositionTest).equals(lettersToFind)) {
                    return currentPositionTest;
                } else {
                    currentPositionTest++;
                }
            }
        } else {
            //a less than zero return means failure here...
            return -1;
        }
    }

    private void caculateNextCommand(int nextCommand, ConsoleInput userInterface){
        if(((nextCommand > 5) || ((player != null) && (nextCommand == 5))) || (nextCommand < 0)){
            //invalid command... no need to do anything except inform the player!
            out.print(SET_TEXT_COLOR_MAGENTA);
            out.print(SET_TEXT_BOLD);
            out.print(SET_BG_COLOR_DARK_GREEN);
            out.print("INVALID COMMAND... valid commands follow");
            helpWords();
        } else {
            try {
                //must be a valid use of this based upon the previous check...
                if (nextCommand == 5) {
                    myWebSocket.leaveGame(myName, gameID, true);
                    out.print(SET_TEXT_COLOR_WHITE);
                    out.print(SET_TEXT_BOLD);
                    out.print(SET_BG_COLOR_GREEN);
                    out.print("Confirm resign by pressing 0\n");
                    if (userInterface.getNum() == 0) {
                        myWebSocket.leaveGame(myName, gameID, false);
                    } else {
                        helpWords();
                    }
                } else if (nextCommand == 4) {
                    myWebSocket.leaveGame(myName, gameID, false);
                } else if (nextCommand == 3) {
                    out.print(SET_TEXT_COLOR_WHITE);
                    out.print(SET_TEXT_BOLD);
                    out.print(SET_BG_COLOR_GREEN);
                    out.print("Please enter the rows and columns of the move\n");
                    out.print("Start with the starting location of the moving peice\n");
                    ChessPosition peice = getChessPosition(userInterface);
                    out.print("Now give that pieces ending location\n");
                    ChessPosition newLocation = getChessPosition(userInterface);
                    out.print("Now please either type out the name of the piece you want to promote to or type null for no promotion");
                    String newPiece = userInterface.getString();
                    if(!((newPiece.contains("NULL")) || (newPiece.contains("null")) || (newPiece.contains("Null")))) {
                        String filteredPiece = new String();
                        for(int i = 0; i<newPiece.length(); i++){
                            char checkChar = newPiece.charAt(i);
                            String CheckString = String.valueOf(checkChar);
                            if(CheckString.matches("[a-zA-Z]")){
                                CheckString = CheckString.toUpperCase();
                                filteredPiece = filteredPiece + CheckString;
                            }
                        }
                        if(filteredPiece.equals(new String())){
                            out.print(SET_TEXT_COLOR_RED);
                            out.print(SET_TEXT_BOLD);
                            out.print(SET_BG_COLOR_BLACK);
                            out.print("Inferring no promotion...\n");
                            ChessMove move = new ChessMove(peice, newLocation, null);
                            myWebSocket.gameMove(myName, gameID, move);
                        } else {
                            ChessPiece.PieceType kindOfPiece = ChessPiece.PieceType.valueOf(filteredPiece);
                            //ChessPiece premotion = new ChessPiece(thisGame.getBoard().getPiece(peice).getTeamColor(), kindOfPiece);
                            ChessMove move = new ChessMove(peice, newLocation, kindOfPiece);
                            myWebSocket.gameMove(myName, gameID, move);
                        }
                    } else {
                        ChessMove move = new ChessMove(peice, newLocation, null);
                        myWebSocket.gameMove(myName, gameID, move);
                    }
                } else if (nextCommand == 2) {
                    out.print(SET_TEXT_COLOR_WHITE);
                    out.print(SET_TEXT_BOLD);
                    out.print(SET_BG_COLOR_GREEN);
                    out.print("Please enter the rows and columns of the piece\n");
                    ChessPosition peice = getChessPosition(userInterface);
                    if (thisGame.getBoard().getPiece(peice) == null) {
                        out.print(SET_TEXT_COLOR_RED);
                        out.print(SET_TEXT_BOLD);
                        out.print(SET_BG_COLOR_BLACK);
                        out.print("THERE IS NO PIECE THERE!!!\n");
                    } else {
                        //I need to have a chessgame here to check this with
                        Collection<ChessMove> myMoves = thisGame.validMoves(peice);
                        //draw chessboard
                        ChessDrawer myPen = new ChessDrawer();
                        myPen.draw(thisGame, amIBlack(), !amIBlack(), peice);
                        //make sure that there is always a chessboard when I get here.
                    }
                } else if (nextCommand == 1) {
                    //draw chessboard
                    ChessDrawer myPen = new ChessDrawer();
                    //simple chessboard draw...
                    myPen.draw(thisGame, amIBlack(), !amIBlack());
                } else {
                    //next command == 0.
                    helpWords();
                }
            } catch(ReportingException r){
                out.print(SET_TEXT_COLOR_RED);
                out.print(SET_TEXT_BOLD);
                out.print(SET_BG_COLOR_BLACK);
                out.print(r.getMessage());
            }
        }
    }
    //add a move decompiler and some kind of space highlighter...
    //might need to add phantom red pieces... just for drawing...

    private void helpWords(){
        String finalPrint = "[0] : Help\n[1] : Redraw Chessboard\n[2] : See valid moves from location- Will need 1 String and 1 int - (1):letter (2):number\n[3] : Make Move- Will need 3 String and 2 ints - (1):letter (2):number (1):letter (2):number (3):promotion piece name or null\n[4] : Leave Game\n";
        if(player != null){
            finalPrint = finalPrint + "[5] : Resign\n";
        }
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(SET_TEXT_BOLD);
        out.print(SET_BG_COLOR_GREEN);
        out.print("Enter the following numbers to execute the following commands:\n");
        out.print(finalPrint);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }
}

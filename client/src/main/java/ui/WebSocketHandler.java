package ui;

import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class WebSocketHandler {

    private WebSocketFacade myWebSocket;

    private String myName;

    private int gameID;

    private ChessGame.TeamColor player;

    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    public WebSocketHandler(String url, NotificationHandler notificationHandler, String myName, int gameID, ChessGame.TeamColor myColor) throws ReportingException {
        myWebSocket = new WebSocketFacade(url, notificationHandler);
        this.myName = myName;
        player = myColor;
        this.gameID = gameID;
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

    private void caculateNextCommand(int nextCommand, ConsoleInput userInterface){
        if(((nextCommand > 5) || ((player != null) && (nextCommand == 5))) || (nextCommand < 0)){
            //invalid command... no need to do anything except inform the player!
            out.print(SET_TEXT_COLOR_MAGENTA);
            out.print(SET_TEXT_BOLD);
            out.print(SET_BG_COLOR_DARK_GREEN);
            out.print("INVALID COMMAND... valid commands follow");
            helpWords();
        } else {
            //must be a valid use of this based upon the previous check...
            if(nextCommand == 5){
                myWebSocket.leaveGame(myName, gameID, true);
                out.print(SET_TEXT_COLOR_WHITE);
                out.print(SET_TEXT_BOLD);
                out.print(SET_BG_COLOR_GREEN);
                out.print("Confirm resign by pressing 0\n");
                if(userInterface.getNum() == 0){
                    myWebSocket.leaveGame(myName, gameID, false);
                } else {
                    helpWords();
                }
            }else if(nextCommand == 4){
                myWebSocket.leaveGame(myName, gameID, false);
            } else if(nextCommand == 3){
                out.print(SET_TEXT_COLOR_WHITE);
                out.print(SET_TEXT_BOLD);
                out.print(SET_BG_COLOR_GREEN);
                out.print("Please enter the rows and columns of the move\n");
                String move = userInterface.getString();
            } else if(nextCommand == 2){
                out.print(SET_TEXT_COLOR_WHITE);
                out.print(SET_TEXT_BOLD);
                out.print(SET_BG_COLOR_GREEN);
                out.print("Please enter the rows and columns of the piece\n");
                String move = userInterface.getString();
            } else if(nextCommand == 1){
                //draw chessboard
            } else {
                //next command == 0.
                helpWords();
            }
        }
    }

    private void helpWords(){
        String finalPrint = "[0] : Help\n[1] : Redraw Chessboard\n[2] : See valid moves from location- Will need 1 String - (1):letter number: like so \"a4\"\n[3] : Make Move- Will need 1 String - (1):letter number, letter number: like so \"a4,a3\"\n[4] : Leave Game\n";
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

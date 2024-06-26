package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.NewGame;
import model.UserData;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;

import static ui.EscapeSequences.*;

public class ServerConnectorTester {

    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    private InputReader getUserInput = new ConsoleInput();

    private ChessDrawer pen = new ChessDrawer();

    private boolean tryConnecting = false;


    //need some kind of init and stop functions

    //might be called from another function in order to allow multiple inputs...


    //The way I set up my functions, this should be appropriate (ie declaring them here)...


    //I feel like these functions are both annoying and questionable... but I need them to properly test this code.
//    public CreateGame getGameCreator() {
//        return gameCreator;
//    }
//
//    public JoinGame getGameJoiner() {
//        return gameJoiner;
//    }
//
//    public Login getAuthValueGenerator() {
//        return authValueGenerator;
//    }
//
//    public Logout getAuthValueRemover() {
//        return authValueRemover;
//    }
//
//    public GetExample getGameInfo() {
//        return gameInfo;
//    }

    private CreateGame gameCreator = new CreateGame();
    private JoinGame gameJoiner = new JoinGame();
    private Login authValueGenerator = new Login();
    private Logout authValueRemover = new Logout();
    private GetExample gameInfo = new GetExample();

    //now I also need to record these cashed data values

    private AuthData[] authentications = new AuthData[1];

    private WebSocketHandler myHandle;

    //I need this to test the code
    public AuthData GetCurrentAuthentication(){
        return authentications[0];
    }

    private String urlString = new String();

    private String game = "/game";
    private String database = "/db";
    private String user = "/user";
    private String auth = "/session";

    private GameData [] games = new GameData[0];

    //I need this to test the code... update to test for null values somehow.
    public GameData [] getGames(){
        if(games == null) {
            return null;
        } else if (games.length == 0){
            return null;
        } else if (games[0] == null){
            return null;
        } else {
            return games;
        }
    }

    //The rest of our code will work if we initialize this to null and change it out everytime we need to
    public ServerConnectorTester(String host, String selector){
        authentications[0] = null;
        //add http:// and :.
        urlString = "http://" + host + ":" + selector;
    }

    //helper functions for incomplete passins
    public void completeAction(){
        if(tryConnecting){
            //this way we will automatically know if we logged out of the game... so we should only call this while logged into a game.
            tryConnecting = myHandle.getNextCommand();
        } else {
            String message = "Error : Missing Key Game Information! Try selecting game-joining Option 4 or 5 to fix issue!";
            out.print(SET_TEXT_COLOR_MAGENTA);
            out.print(SET_TEXT_BOLD);
            out.print(SET_BG_COLOR_DARK_GREEN);
            out.print(message);
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
        }
    }
    public void completeAction(int selector) throws IOException {
        completeAction(selector, 0);
    }

    public void completeAction(int selector, int otherNumber) throws IOException {
        completeAction(selector, otherNumber, null);
    }

    public void completeAction(int selector, String[] inputsOrdered) throws IOException {
        completeAction(selector, 0, inputsOrdered);
    }


    //just print out to console here, do not return anything...
    public void completeAction(int selector, int otherNumber, String[] inputsOrdered) throws IOException {
        //out.print(" \nnextCommand\n" + selector + "\n");
        String message = "Success!\n";
        boolean loggedIn = true;
        boolean addMoreAtEnd = false;
        if(authentications[0] == null){
            loggedIn = false;
        }
        if(selector == 0){
            message = help();
        } else if(selector == 1){
            if(!loggedIn){
                throw new RuntimeException("goodbye!");
            } else {
                try {
                    authValueRemover.doLogout(urlString + auth, authentications[0].authToken());
                    authentications[0] = null;
                } catch(ReportingException r) {
                    message = r.getMessage();
                }
                //make sure they know the new options
                addMoreAtEnd = true;
            }
        } else if(selector == 2){
            if(!loggedIn){
                if(inputsOrdered != null) {
                    if ((inputsOrdered[0] != null) && (inputsOrdered[1] != null) && (inputsOrdered[2] != null)) {
                        //find some way to get more input mid function here... or I could have an array of other inputs...
                        out.print("Enter Username\n");
                        String username = inputsOrdered[0];
                        out.print("Enter Password\n");
                        String password = inputsOrdered[1];
                        out.print("Enter Email\n");
                        String email = inputsOrdered[2];
                        //enter information

                        try {
                            authentications[0] = authValueGenerator.login(urlString + user, new UserData(username, password, email));
                            message = message + "Cashed\nusername : " + authentications[0].username() + "\nauthentication token : " + authentications[0].authToken() + "\n";
                        } catch (ReportingException r) {
                            message = r.getMessage();
                        }
                        //make sure they know the new options
                        addMoreAtEnd = true;
                    } else {
                        message = "Error: not enough information to process request!";
                    }
                } else {
                    message = "Error: not enough information to process request!";
                }
            }else {
                //find some way to get more input mid function here... or I could have an array of other inputs...
                out.print("Enter Game Name\n");
                //String gameName = getUserInput.getString();
                NewGame games = new NewGame(null,null, inputsOrdered[0]);
                try {
                    GameData newGame = gameCreator.create(urlString + game, authentications[0].authToken(), games);
                    message = message + "New Game Name : " + newGame.gameName() + "\nNew Game ID : " + newGame.gameID() + "\n";
                } catch(ReportingException r){
                    message = r.getMessage();
                }
            }
        } else if(selector == 3){
            if(!loggedIn){
                if(inputsOrdered != null) {
                    if ((inputsOrdered[0] != null) && (inputsOrdered[1] != null)) {
                        //find some way to get more input mid function here... or I could have an array of other inputs...
                        out.print("Enter Username\n");
                        String username = inputsOrdered[0];
                        out.print("Enter Password\n");
                        String password = inputsOrdered[1];
                        try {
                            authentications[0] = authValueGenerator.login(urlString + auth, new UserData(username, password, null));
                            message = message + "Cashed\nusername : " + authentications[0].username() + "\nauthentication token : " + authentications[0].authToken() + "\n";
                        } catch (ReportingException r) {
                            message = r.getMessage();
                        }
                        //make sure they know the new options
                        addMoreAtEnd = true;
                    } else {
                        message = "Error: not enough information to process request!";
                    }
                } else {
                    message = "Error: not enough information to process request!";
                }

                    } else {
                try {
                    games = gameInfo.doGet(urlString + game, authentications[0].authToken());
                    if(games != null) {
                        var json = new Gson().toJson(games, GameData[].class);
                        message = message + "From 0 up game by game the number to select each game is the order of appearance of the game in this list...\n";
                        int gameNumber = 0;
                        //here is how we will get the information we need from this particular function...
                        for (GameData g: games) {
                            message = message + "[" + gameNumber + "] : " + "(name) : ";
                            if(g.gameName() != null) {
                                message = message + g.gameName() + " (White Username) : ";
                            }  else {
                                message = message + "NONE!  (White Username) : ";
                            }
                            if(g.whiteUsername() != null) {
                                message = message + g.whiteUsername() + " (Black Username) : ";
                            }  else {
                                message = message + "NONE! (Black Username) : ";
                            }
                            if(g.blackUsername() != null) {
                                message = message + g.blackUsername() + " \n";
                            }  else {
                                message = message + "NONE! \n";
                            }
                            gameNumber++;
                        }
                        message = message + json;
                    } else {
                        message = message + "There is No GAME!\n";
                    }
                } catch(ReportingException r) {
                    message = r.getMessage();
                }
            }
        } else if (loggedIn) {
            if(selector == 4){
                //find some way to get more input mid function here... or I could have an array of other inputs...
                //out.print("Enter Game Number\n");
                int gameNumber = otherNumber;
                if(gameNumber >= games.length) {
                    message = " \nError: Not a valid game number!\n" + "Games and numbers are found above the error message.\n";
                    //this will make sure that above that message there is a list of valid games!
                    completeAction(3, 0, null);
                } else if(games[gameNumber] == null) {
                    message = " \nError: Not a valid game number!\n" + "Games and numbers are found above the error message.\n";
                    //this will make sure that above that message there is a list of valid games!
                    completeAction(3, 0, null);
                } else {
                    String IDString = String.valueOf(games[gameNumber].gameID());
                    //out.print("Enter color you wish to play as.\n");
                    if (inputsOrdered != null) {
                        if (inputsOrdered[0] != null) {
                            String playerColor = inputsOrdered[0];
                            try {
                                gameJoiner.join(urlString + game, authentications[0].authToken(), playerColor, IDString);
                                //for now we will do this this way...
                                ChessBoard newChess = new ChessBoard();
                                newChess.resetBoard();
                                ChessGame boardDrawer = new ChessGame();
                                boardDrawer.setBoard(newChess);
                                pen.draw(boardDrawer, true, true);
                                tryConnecting = true;
                                //draw(boardDrawer);
                                //message = message + "Cashed\nusername : " + authentications[0].username() + "authentication token : " + authentications[0].authToken() + "\n";
                            } catch (ReportingException r) {
                                tryConnecting = false;
                                message = r.getMessage();
                            }
                            if(tryConnecting){
                                joinWebSocket(games[gameNumber].gameID(), ChessGame.TeamColor.valueOf(playerColor));
                            }
                        }else {
                            message = "Error: not enough information to process request!";
                        }
                    } else {
                        message = "Error: not enough information to process request!";
                    }
                }
            }else if(selector == 5){
                //find some way to get more input mid function here... or I could have an array of other inputs...
                out.print("Enter Game Number\n");
                int gameNumber = otherNumber;
                if(gameNumber >= games.length) {
                    message = " \nError: Not a valid game number!\n" + "Games and numbers are found above the error message.\n";
                    //this will make sure that above that message there is a list of valid games!
                    completeAction(3, 0, null);
                } else if(games[gameNumber] == null) {
                    message = " \nError: Not a valid game number!\n" + "Games and numbers are found above the error message.\n";
                    //this will make sure that above that message there is a list of valid games!
                    completeAction(3, 0, null);
                } else {
                    String IDString = String.valueOf(games[gameNumber].gameID());
                    try {
                        gameJoiner.join(urlString + game, authentications[0].authToken(), null, IDString);
                        //for now we will do this this way...
                        ChessBoard newChess = new ChessBoard();
                        newChess.resetBoard();
                        ChessGame boardDrawer = new ChessGame();
                        boardDrawer.setBoard(newChess);
                        pen.draw(boardDrawer, true, true);
                        tryConnecting = true;
                        //draw(boardDrawer);
                        //message = message + "Cashed\nusername : " + authentications[0].username() + "authentication token : " + authentications[0].authToken() + "\n";
                    } catch(ReportingException r){
                        tryConnecting = false;
                        message = r.getMessage();
                    }
                    if(tryConnecting){
                        joinWebSocket(games[gameNumber].gameID(), null);
                    }
                }
            } else {
                message = "Error: that is not a valid option for this client condition!\n";
                //make sure they know the options
                addMoreAtEnd = true;
            }
        } else {
            message = "Error: that is not a valid option for this client condition!\n";
            //make sure they know the options
            addMoreAtEnd = true;
        }
        //Here I need to print out the message I have made... first check if we need new options
        if(addMoreAtEnd){
            completeAction(0, 0, null);
        }
        //Now print the message to terminal...
        out.print(SET_TEXT_COLOR_MAGENTA);
        out.print(SET_TEXT_BOLD);
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(message);
        out.print(" \nnextCommand\n" + selector + "\n");
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    //here I pass in a game specifically so I can do something more complicated if I choose to...
//    private void draw(ChessGame game) {
//        ChessBoard board = game.getBoard();
//        //give a line or so
//        out.print(SET_BG_COLOR_BLUE);
//        for(int size = 0; size < board.getChessBoardSize(); ++size) {
//            out.print(EMPTY);
//        }
//        boolean blackSpace = false;
//        //print right side up board
//        for(int space = 0; space < board.getChessBoardSize(); ++space) {
//            for(int width = 0; width < board.getChessBoardSize(); ++width) {
//                if(blackSpace){
//                    blackSpace = false;
//                    out.print(SET_BG_COLOR_WHITE);
//                } else {
//                    blackSpace = true;
//                    out.print(SET_BG_COLOR_BLACK);
//                }
//                if (board.getPiece(new ChessPosition(space, width)) != null) {
//                    ChessPiece myPiece = board.getPiece(new ChessPosition(space, width));
//                    ChessGame.TeamColor color = myPiece.getTeamColor();
//                    ChessPiece.PieceType piece = myPiece.getPieceType();
//                    replace(color.name(), piece.name());
//                } else {
//                    out.print(EMPTY);
//                }
//            }
//        }
//        //give a line or so
//        out.print(SET_BG_COLOR_BLUE);
//        for(int size = 0; size < board.getChessBoardSize(); ++size) {
//            out.print(EMPTY);
//        }
//        blackSpace = false;
//        //print upside down board
//        for(int space = board.getChessBoardSize(); space > 0; --space) {
//            for(int width = board.getChessBoardSize(); width > 0; --width) {
//                if(blackSpace){
//                    blackSpace = false;
//                    out.print(SET_BG_COLOR_WHITE);
//                } else {
//                    blackSpace = true;
//                    out.print(SET_BG_COLOR_BLACK);
//                }
//                if (board.getPiece(new ChessPosition(space, width)) != null) {
//                    ChessPiece myPiece = board.getPiece(new ChessPosition(space, width));
//                    ChessGame.TeamColor color = myPiece.getTeamColor();
//                    ChessPiece.PieceType piece = myPiece.getPieceType();
//                    replace(color.name(), piece.name());
//                } else {
//                    out.print(EMPTY);
//                }
//            }
//        }
//    }
//
//    private void replace(String color, String piece){
//        //right now we only have escape sequences for black and white...
//        if(color.equals("BLACK")) {
//            if(piece.equals("PAWN")){
//                out.print(BLACK_PAWN);
//            } else if(piece.equals("KNIGHT")){
//                out.print(BLACK_KNIGHT);
//            } else if(piece.equals("BISHOP")){
//                out.print(BLACK_BISHOP);
//            } else if(piece.equals("ROOK")){
//                out.print(BLACK_ROOK);
//            } else if(piece.equals("QUEEN")){
//                out.print(BLACK_QUEEN);
//            } else if(piece.equals("KING")){
//                out.print(BLACK_KING);
//            }
//        } else if(color.equals("WHITE")){
//            if(piece.equals("PAWN")){
//                out.print(WHITE_PAWN);
//            } else if(piece.equals("KNIGHT")){
//                out.print(WHITE_KNIGHT);
//            } else if(piece.equals("BISHOP")){
//                out.print(WHITE_BISHOP);
//            } else if(piece.equals("ROOK")){
//                out.print(WHITE_ROOK);
//            } else if(piece.equals("QUEEN")){
//                out.print(WHITE_QUEEN);
//            } else if(piece.equals("KING")){
//                out.print(WHITE_KING);
//            }
//        }
//    }

    private void joinWebSocket(int gameID, ChessGame.TeamColor myColor){
        //call after adding to the game.
        //use a WebSocketFasade handler class to make moves, exct by calling the WebSocket Facade class after login.
        try{
            NotificationHandler myHandler = new Notifier();
            myHandle = new WebSocketHandler(urlString, myHandler, authentications[0].username(), gameID, myColor);
        } catch (ReportingException e) {
            out.print(SET_TEXT_COLOR_RED);
            out.print(SET_TEXT_BOLD);
            out.print(SET_BG_COLOR_BLACK);
            out.print(e.getMessage());
            tryConnecting = false;
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
        }
        //we must have disconected or never have been connected
    }

    private String help(){
        String returnSupport = new String();
        returnSupport = "Enter the following numbers to execute the following commands:\n[0] : Help\n";
        if(authentications[0] == null){
            return returnSupport + "[1] : Quit\n[2] : Register a new Chess Account and Login - Will need 3 Strings - (1):username (2):password (3):email\n[3] : Login to an old Chess Account - Will need 2 Strings - (1):username (2):password\n";
        } else {
            return returnSupport + "[1] : Logout of this Chess Account\n[2] : Create a new Chess Game - Will need 1 String - (1):gameName\n[3] : List all Chess Games that currently exist on this Server\n[4] : Join an available Chess Game - Will need one number - gameNumber\n[5] : Join as an observer of any Chess Game - Will need one number - gameNumber - Will need 1 String - (1) - joinColor: IMPORTANT! just click enter with no inputs if you want to join as an observer, otherwise the system will interpret your response as a team in chess\n";
        }
    }


    public boolean isConnected() {
        return tryConnecting;
    }
}


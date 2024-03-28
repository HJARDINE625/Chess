package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
//import exception.ResponseException;
//import webSocketMessages.Action;
//import webSocketMessages.Notification;
import webSocketMessages.serverMessages.Load;
import webSocketMessages.serverMessages.Message;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.Join;
import webSocketMessages.userCommands.Leave;
import webSocketMessages.userCommands.Move;
import webSocketMessages.userCommands.UserGameCommand;
//import EscapeSequences;

import javax.websocket.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    //probably will be unused here...
    private InputReader getUserInput = new ConsoleInput();

    private ChessDrawer pen = new ChessDrawer();

    private ChessGame.TeamColor myColor;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ReportingException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    var notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);
                    switch(notification.getServerMessageType()){
                        case ERROR -> reportError(new Gson().fromJson(message, Message.class));
                        case NOTIFICATION -> reportSucess(new Gson().fromJson(message, Message.class));
                        case LOAD_GAME -> drawBoard(new Gson().fromJson(message, Load.class), myColor);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ReportingException("Error: 500 " + ex.getMessage());
        }
    }
    //here are the methods mentioned above...
    private void reportSucess(Message report){
        out.print(SET_TEXT_COLOR_YELLOW);
        out.print(SET_TEXT_BOLD);
        out.print(SET_BG_COLOR_GREEN);
        out.print(report.getMessage());
    }

    private void reportError(Message report){
        out.print(SET_TEXT_COLOR_RED);
        out.print(SET_TEXT_BOLD);
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(report.getMessage());
    }

    private void drawBoard(Load boardStuff, ChessGame.TeamColor myColor){
        //we might want another way to see what team we are on... for now this lets us test something.
        ChessGame gameInQuestion = boardStuff.getGame();
        //confusing and unused.
        ChessBoard implementation = gameInQuestion.getBoard();
        //for now hardcoded for two teams
        if(myColor != null){
            if(myColor.toString().equals("BLACK")){
                pen.draw(gameInQuestion, true, false);
                //that is all
                return;
            }
        }
        //they want a white configuration...
        pen.draw(gameInQuestion, false, true);
        //good
        return;
    }



    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    //TODO: here we need to change these methods and update them so that they get called properly and include all the data they need to create
    //TODO: one of the UserGameCommands completely (whichever one is appropriate for the method).

//    public void enterPetShop(String visitorName) throws ReportingException {
//        try {
//            var action = new UserGameCommand(UserGameCommand.commandType.ENTER, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//        } catch (IOException ex) {
//            throw new ReportingException("Error: 500 " + ex.getMessage());
//        }
//    }
//
//    public void leavePetShop(String visitorName) throws ReportingException {
//        try {
//            var action = new UserGameCommand(UserGameCommand.commandType.EXIT, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//            this.session.close();
//        } catch (IOException ex) {
//            throw new ReportingException("Error: 500 " + ex.getMessage());
//        }
//    }

    public void joinGame(String visitorName, int gameID, ChessGame.TeamColor myColor) throws ReportingException {
        try {
            var action = new Join(visitorName, myColor, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ReportingException("Error: 500 " + ex.getMessage());
        }
        //no exception is good, we want to veiw the board from this direction...
        this.myColor = myColor;
    }

    public void joinGame(String visitorName, int gameID) throws ReportingException {
        joinGame(visitorName, gameID, null);
    }

    public void leaveGame(String visitorName, int gameID, boolean didResign) throws ReportingException {
        try {
            var action = new Leave(visitorName, didResign, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new ReportingException("Error: 500 " + ex.getMessage());
        }
    }

    public void gameMove(String visitorName, int gameID, ChessMove theMove) throws ReportingException {
        try {
            var action = new Move(visitorName, theMove, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new ReportingException("Error: 500 " + ex.getMessage());
        }
    }

}

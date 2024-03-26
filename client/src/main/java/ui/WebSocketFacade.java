package ui;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
//import exception.ResponseException;
//import webSocketMessages.Action;
//import webSocketMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.Join;
import webSocketMessages.userCommands.Leave;
import webSocketMessages.userCommands.Move;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


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
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ReportingException("Error: 500 " + ex.getMessage());
        }
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

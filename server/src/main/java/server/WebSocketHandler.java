package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.*;
import webSocketMessages.serverMessages.Message;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        var action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.type()) {
            case ENTER -> enter(action.visitorName(), session);
            case EXIT -> exit(action.visitorName());
        }
    }

    private void enter(String visitorName, Session session) throws IOException {
        connections.add(visitorName, session);
        var message = String.format("%s is in the shop", visitorName);
        var notification = new ServerMessage(ServerMessage.Type.ARRIVAL, message);
        connections.broadcast(visitorName, notification);
    }

    private void exit(String visitorName) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s left the shop", visitorName);
        var notification = new ServerMessage(ServerMessage.Type.DEPARTURE, message);
        connections.broadcast(visitorName, notification);
    }

    public void makeNoise(String visitorsName, String sound) throws IOException {
        try {
            var message = String.format("%s says %s", visitorsName, sound);
            var notification = new ServerMessage(ServerMessage.Type.NOISE, message);
            connections.broadcast("", notification);
        } catch (Exception ex) {
            Message error = new Message(ServerMessage.ServerMessageType.ERROR, "Error : 500 " + ex.getMessage());
            connections.narrowcast(visitorsName, error);
        }
    }
}
package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataBaseAccesser;
import org.eclipse.jetty.websocket.api.Session;
import server.Connection;
//import webSocketMessages.Notification;
import service.WebSocketServices;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String visitorName, Session session) {
        var connection = new Connection(visitorName, session);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    //do not reference different games...
    //can add a check function called by broadcast and narrow cast that only transmits to people in the game as an observer or player.
    private boolean validConnection(String visitorsName, int gameID){
        WebSocketServices myService = new WebSocketServices();
        DataBaseAccesser myNewDatabase = new DataBaseAccesser();
        //Unnecessary
        //ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        //ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;
        if(myService.findPosition(visitorsName, myNewDatabase, gameID) != null){
            return true;
        } else {
            //see if we are an observer...
            return myService.amIInPosition(visitorsName, myNewDatabase, null, gameID);
        }
    }


    public void broadcast(String excludeVisitorName, ServerMessage notification, int gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName)) {
                    if(validConnection(c.visitorName, gameID)) {
                        var message = new Gson().toJson(notification);
                        c.send(message);
                    }
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }

    //we need this too...
    public void narrowcast(String vistorsName, ServerMessage error) throws IOException {
        var groupList = new ArrayList<Connection>();
        for (var c: connections.values()) {
            if(c.session.isOpen()){
                if(c.visitorName.equals(vistorsName)){
                    var message = new Gson().toJson(error);
                    c.send(message);
                }
            }
        }
    }
}

package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import server.Connection;
//import webSocketMessages.Notification;
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

    public void broadcast(String excludeVisitorName, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName)) {
                    var message = new Gson().toJson(notification);
                    c.send(message);
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

package server;

import spark.Spark;
import org.eclipse.jetty.websocket.api.Session;

public class WebSocketServer {

    public WebSocketServer(int port) {
        Spark.port(port);
        Spark.webSocket("/connect", WebSocketServer.class);
        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }

    public void getSession(Session session, String message ){
        Session.getRemote().sendString("WebSocket response: " + message);
    }

}

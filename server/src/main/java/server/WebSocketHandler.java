package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DataBaseAccesser;
import model.AuthDataInt;
import model.Responses;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.*;
import service.GameServices;
import service.RegistrationServices;
import service.WebSocketServices;
import webSocketMessages.serverMessages.Message;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.Join;
import webSocketMessages.userCommands.Leave;
import webSocketMessages.userCommands.Move;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.userCommands.UserGameCommand.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {
    private GameServices thisGame =  new GameServices();
    private RegistrationServices findPeople = new RegistrationServices();

    private WebSocketServices extraHelp = new WebSocketServices();

    private DataBaseAccesser myDataStorageDevice = new DataBaseAccesser();
    private final ConnectionManager connections = new ConnectionManager();

    //TODO: I need to discover how to get a new connection from the place the server actually is to
    //TODO: initate this websocket routine. I also need to change out the cases for my cases here (see usergame commands for examples) and
    //TODO: add actual logic to decide if they throw an error or not.
    //TODO: finally I need to fiqure out if I should update all observers in a new function in the server (probably) and add a makemove/remove player/observer command...
    //TODO: if all of that works, including in the client, then I am probably ready for real testing...
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        var action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case JOIN_PLAYER -> joinGame(new Gson().fromJson(message, Join.class), session, true);
            case JOIN_OBSERVER -> joinGame(new Gson().fromJson(message, Join.class), session, false);
            case MAKE_MOVE -> causeMove(new Gson().fromJson(message, Move.class), session);
            case LEAVE -> leaveGame(new Gson().fromJson(message, Leave.class), session);
            case RESIGN -> surrender(new Gson().fromJson(message, Leave.class), session);
//            case ENTER -> enter(action.visitorName(), session);
//            case EXIT -> exit(action.visitorName());
        }
    }

    private void leaveGame(Leave instuctions, Session session) throws DataAccessException, IOException {
        //Probably will not come up...
        //String httpLink = newHttpLinker(session) + "/game";
        if(instuctions.getAuthString() == null){
            Message error = new Message(ServerMessage.ServerMessageType.ERROR, "Error : 500 Not Authorized... so no idea who to send this message too");
            throw new DataAccessException(error.getMessage());
        } else {
               String auth = instuctions.getAuthString();
               String name = new String();
               try {
                   name = extraHelp.getName(auth, myDataStorageDevice);
               } catch(DataAccessException e){
                   Message error = new Message(ServerMessage.ServerMessageType.ERROR, "Error : 500 Not Authorized... so no idea who to send this message too");
                   throw new DataAccessException(error.getMessage());
               }
               //kind of hard coded for now...
            ChessGame.TeamColor team = extraHelp.findPosition(name, myDataStorageDevice, instuctions.getGameID());
            Responses removedGame = extraHelp.removeName(auth, myDataStorageDevice, team, instuctions.getGameID());
            if(removedGame.getMyException() != null){
                //something went wrong... narrowcast it
                Message error = new Message(ServerMessage.ServerMessageType.ERROR, "Error : " + removedGame.getNumericalCode() + removedGame.getMyException());
                //removedGame.getMyException().getMessage();
                connections.narrowcast(name, error);
            } else {
                //broadcast the new update
                Message finalServerMessage = new Message(ServerMessage.ServerMessageType.NOTIFICATION, name + " has left the game");
                connections.broadcast(name, finalServerMessage);
            }
        }


    }

    private void joinGame(Join instuctions, Session session, boolean isPlaying) throws IOException, DataAccessException {
        String httpLink = newHttpLinker(session) + "/game";
        String color;
        if(isPlaying) {
            color = instuctions.getTeamColor();
        } else {
            color = null;
        }
        String auth = instuctions.getAuthString();
        String gameID = Integer.toString(instuctions.getGameID());
        boolean worked = false;
        try{
            join(httpLink, auth, color, gameID);
            worked = true;
        } catch(DataAccessException access){
            Message error = new Message(ServerMessage.ServerMessageType.ERROR, "Error : 500 " + access.getMessage());
            String broadcastName = extraHelp.getName(auth, myDataStorageDevice);
            connections.narrowcast(broadcastName, error);
            worked = false;
        }
        if(worked == true){
            RegistrationServices personFinder = new RegistrationServices();
            if(color != null) {
                String broadcastName = extraHelp.getName(auth, myDataStorageDevice);
                Message finalServerMessage = new Message(ServerMessage.ServerMessageType.NOTIFICATION, broadcastName + " has joined the game as " + color);
                connections.broadcast(broadcastName, finalServerMessage);
            } else {
                String broadcastName = extraHelp.getName(auth, myDataStorageDevice);
                Message finalServerMessage = new Message(ServerMessage.ServerMessageType.NOTIFICATION, broadcastName + " has joined the game as an observer");
                connections.broadcast(broadcastName, finalServerMessage);
            }
        }
    }

    private String newHttpLinker(Session session){
        int port = session.getLocalAddress().getPort();
        String host = session.getLocalAddress().getHostString();
        return "http://" + host + ":" + Integer.toString(port);
    }

    public void join(String urlString, String authentication, String color, String gameID) throws DataAccessException, IOException {
        URL url = new URL(urlString);
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print("Color " + color + " ID" + " " + gameID);
        new AuthDataInt(gameID, color);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(100000);
        connection.setRequestMethod("PUT");
        //out.print("HERE!!!");

        //connection.setRequestMethod("PUT");

        //gives body issues...
//        var outputStream = connection.getOutputStream();
//        var json = new Gson().toJson(new AuthDataInt(gameID, color));
//        outputStream.write(json.getBytes());

        // Set HTTP request headers, if necessary
        // connection.addRequestProperty("Accept", "text/html");
        connection.addRequestProperty("Authorization", authentication);

//        var outputStream = connection.getOutputStream();
//        var json = new Gson().toJson(new AuthDataInt(gameID, color));
//        outputStream.write(json.getBytes());
        connection.setDoOutput(true);
        //connection.setRequestMethod("PUT");
        try (var outputStream = connection.getOutputStream()) {
            var json = new Gson().toJson(new AuthDataInt(gameID, color));
            outputStream.write(json.getBytes());
        }

        connection.connect();
        //out.print("Connected... ");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            out.print("OK!\n");
            // Get HTTP response headers, if necessary
            // Map<String, List<String>> headers = connection.getHeaderFields();
//            var input = connection.getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(input);
//            var Gson = new Gson().fromJson(inputStreamReader, GameData.class);
//            //process these responses differently to allow for a proper string output...
//            return Gson;
            connection.disconnect();
            //return;
            // OR

            //connection.getHeaderField("Content-Length");

            //InputStream responseBody = connection.getInputStream();
            // Read and process response body from InputStream ...
        } else {
            try {
                //out.print("NOOO!\n");

                var input = connection.getInputStream();
                try(input) {
                    InputStreamReader inputStreamReader = new InputStreamReader(input);
                    try(inputStreamReader) {
                        var Gson = new Gson().fromJson(inputStreamReader, DataAccessException.class);
                        connection.disconnect();
                        throw Gson;
                    }
                }
            } catch(IOException e){
                ExceptionInterpreter error = new ExceptionInterpreter();
                error.transform(e);
                connection.disconnect();
                out.print("ERROR!\n");
                //in case there is not an available transformation...
                throw e;
            }
            // SERVER RETURNED AN HTTP ERROR
        }
    }


//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(visitorName, session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new ServerMessage(ServerMessage.Type.ARRIVAL, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new ServerMessage(ServerMessage.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String visitorsName, String sound) throws IOException {
//        try {
//            var message = String.format("%s says %s", visitorsName, sound);
//            var notification = new ServerMessage(ServerMessage.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            Message error = new Message(ServerMessage.ServerMessageType.ERROR, "Error : 500 " + ex.getMessage());
//            connections.narrowcast(visitorsName, error);
//        }
//    }
}
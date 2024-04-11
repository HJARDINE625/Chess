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
import service.GameServices;
import service.RegistrationServices;
import service.WebSocketServices;
import webSocketMessages.serverMessages.Load;
import webSocketMessages.serverMessages.Message;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.Join;
import webSocketMessages.userCommands.Leave;
import webSocketMessages.userCommands.Move;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


@WebSocket
public class WebSocketHandler {

    public WebSocketHandler(){
        thisGame =  new GameServices();
        findPeople = new RegistrationServices();
        extraHelp = new WebSocketServices();
        myDataStorageDevice = new DataBaseAccesser();
        connections = new ConnectionManager();
    }
    private GameServices thisGame =  new GameServices();
    private RegistrationServices findPeople = new RegistrationServices();

    private WebSocketServices extraHelp = new WebSocketServices();

    private DataBaseAccesser myDataStorageDevice = new DataBaseAccesser();
    private ConnectionManager connections;

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

    private void surrender(Leave instructions, Session session) throws DataAccessException, IOException {
        if (instructions.getAuthString() == null) {
            Message error = new Message(ServerMessage.ServerMessageType.ERROR, "Error : 500 Not Authorized... so no idea who to send this message too");
            throw new DataAccessException(error.getMessage());
        } else {
            String auth = instructions.getAuthString();
            String name = new String();
            try {
                name = extraHelp.getName(auth, myDataStorageDevice);
            } catch (DataAccessException e) {
                Message error = new Message(ServerMessage.ServerMessageType.ERROR, "Error : 500 Not Authorized... so no idea who to send this message too");
                throw new DataAccessException(error.getMessage());
            }
            //if we made it this far... then we can try to check the game itself.
            Responses stillTimeToGiveUp = extraHelp.giveUp(auth, myDataStorageDevice, instructions.getGameID());
            if(stillTimeToGiveUp.getMyException() != null){
                //something went wrong... narrowcast it
                Message error = new Message(ServerMessage.ServerMessageType.ERROR, "Error : " + stillTimeToGiveUp.getNumericalCode() + stillTimeToGiveUp.getMyException());
                //removedGame.getMyException().getMessage();
                connections.narrowcast(name, error);
            } else{
                //looks like we got what we wanted so we should have two things...
                Message finalServerMessage = new Message(ServerMessage.ServerMessageType.NOTIFICATION, "GAME OVER! " + name + " surrendered.");
                connections.broadcast(name, finalServerMessage, instructions.getGameID());
                finalServerMessage = new Message(ServerMessage.ServerMessageType.NOTIFICATION, "GAME OVER! You gave up.");
                connections.narrowcast(name, finalServerMessage);
                connections.broadcast(name, new Load(ServerMessage.ServerMessageType.LOAD_GAME, stillTimeToGiveUp.getMyGameData().implementation()), instructions.getGameID());
                connections.narrowcast(name, new Load(ServerMessage.ServerMessageType.LOAD_GAME, stillTimeToGiveUp.getMyGameData().implementation()));
            }
        }
    }

//similar to the surrender one, but a little different...
    private void causeMove(Move instructions, Session session) throws DataAccessException, IOException {
        if(instructions.getAuthString() == null){
            Message error = new Message(ServerMessage.ServerMessageType.ERROR, "Error : 500 Not Authorized... so no idea who to send this message too");
            throw new DataAccessException(error.getMessage());
        }else {
            String auth = instructions.getAuthString();
            String name = new String();
            try {
                name = extraHelp.getName(auth, myDataStorageDevice);
            } catch (DataAccessException e) {
                Message error = new Message(ServerMessage.ServerMessageType.ERROR, "Error : 500 Not Authorized... so no idea who to send this message too");
                throw new DataAccessException(error.getMessage());
            }
            //if we made it this far... then we can try to check the game itself.
            Responses gameMove = extraHelp.moveMaker(auth, myDataStorageDevice, instructions.getMyMove(), instructions.getGameID());
            if(gameMove.getMyException() != null){
                //something went wrong... narrowcast it
                Message error = new Message(ServerMessage.ServerMessageType.ERROR, "Error : " + gameMove.getNumericalCode() + gameMove.getMyException());
                //removedGame.getMyException().getMessage();
                connections.narrowcast(name, error);
            } else{
                //looks like we got what we wanted so we should have two things...
                Message finalServerMessage = new Message(ServerMessage.ServerMessageType.NOTIFICATION, name + " made move " + instructions.getMyMove().toString() + ".");
                connections.broadcast(name, finalServerMessage, instructions.getGameID());
                connections.broadcast(name, new Load(ServerMessage.ServerMessageType.LOAD_GAME, gameMove.getMyGameData().implementation()), instructions.getGameID());
                connections.narrowcast(name, new Load(ServerMessage.ServerMessageType.LOAD_GAME, gameMove.getMyGameData().implementation()));
                //addeed here to allow for more messages if need arises here
                boolean check = false;
                boolean trapped = false;
                //check for stalemate and check and checkmate
                //hardcoded for now... an impossible name
                String othername = ")--";
                if(gameMove.getMyGameData().implementation().getTeamTurn().equals(ChessGame.TeamColor.WHITE)) {
                    othername = gameMove.getMyGameData().whiteUsername();
                }
                if(gameMove.getMyGameData().implementation().getTeamTurn().equals(ChessGame.TeamColor.BLACK)) {
                    othername = gameMove.getMyGameData().blackUsername();
                }
                if(othername.equals(")--")){
                    othername = "Some coward who quit before they could lose";
                }
                if(gameMove.getMyGameData().implementation().isInCheck(gameMove.getMyGameData().implementation().getTeamTurn())){
                    check = true;
                }
                if(gameMove.getMyGameData().implementation().isInStalemate(gameMove.getMyGameData().implementation().getTeamTurn())){
                    trapped = true;
                }
                if(trapped){
                    //checkmate
                    if(check){
                        finalServerMessage = new Message(ServerMessage.ServerMessageType.NOTIFICATION, "GAME OVER! " + othername + " is in checkmate!!");
                        connections.broadcast(name, finalServerMessage, instructions.getGameID());
                        connections.narrowcast(name, finalServerMessage);
                    }
                    //stalemate
                    else{
                        finalServerMessage = new Message(ServerMessage.ServerMessageType.NOTIFICATION, "GAME OVER! " + othername + " is in stalemate!?");
                        connections.broadcast(name, finalServerMessage, instructions.getGameID());
                        connections.narrowcast(name, finalServerMessage);
                    }
                    //check
                } else if(check){
                    finalServerMessage = new Message(ServerMessage.ServerMessageType.NOTIFICATION, othername + " is in check!");
                    connections.broadcast(name, finalServerMessage, instructions.getGameID());
                    connections.narrowcast(name, finalServerMessage);
                }
//none!
            }
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
                connections.broadcast(name, finalServerMessage, instuctions.getGameID());
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
                connections.broadcast(broadcastName, finalServerMessage, instuctions.getGameID());
            } else {
                String broadcastName = extraHelp.getName(auth, myDataStorageDevice);
                Message finalServerMessage = new Message(ServerMessage.ServerMessageType.NOTIFICATION, broadcastName + " has joined the game as an observer");
                connections.broadcast(broadcastName, finalServerMessage, instuctions.getGameID());
            }
        }
    }

    private String newHttpLinker(Session session){
        int port = session.getLocalAddress().getPort();
        String host = session.getLocalAddress().getHostString();
        return "http://" + host + ":" + Integer.toString(port);
    }

    //login from server... could try from client side.
    //test 
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
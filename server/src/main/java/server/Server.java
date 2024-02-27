package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.DataAccesser;
import model.*;
import service.ContolServices;
import service.GameServices;
import service.RegistrationServices;
import spark.*;
import spark.Response;

import java.nio.file.Paths;

public class Server {

    private DataAccesser myDataStorageDevice = new DataAccess();
    private ContolServices reset = new ContolServices();
    private RegistrationServices loginStuff = new RegistrationServices();
    private GameServices gameDataAccess = new GameServices();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        //I appear to be getting an invalid path, invalid length/CHAR error here... I wonder if I should get my path some other way...
        var webDir = Paths.get(Server.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "web");
        Spark.externalStaticFileLocation(webDir.toString());

        //First get request...
        //(will need to convert a json from gson and a json to gson to allow for the request and response type values in spark (not to be confused with my own requests and responses)...
        Spark.post("/user", this::addUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.put("/game", this::joinGame);
        Spark.post("/game", this::createGame);
        //I intentionally changed capitalization on this last one to emphasize how final it is...
        Spark.delete("/db", this::DeleteALL);
        //Set up rotes and get responses (turn my responses into a json string and give response class). and return them...

        // Register your endpoints and handle exceptions here.

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }

    private Object addUser(Request req, Response res) {
        //Get a serializer...
        var serializer = new Gson();

        //now get the data we need for a response and then check it before passing it into a service...
        UserData newUser = serializer.fromJson(req.body(), UserData.class);
        Responses response;
        if ((newUser.username() == null) || (newUser.password() == null) || (newUser.email() == null)) {
            response = new Responses(400);
            response.setMyException(new DataAccessException("Error: invalid request"));
        } else {

            RegistrationServices service = new RegistrationServices();
            response = service.register(newUser, myDataStorageDevice);
        }
        return new Gson().toJson(response);

    }

    private Object loginUser(Request req, Response res) {
        //Get a serializer...
        var serializer = new Gson();

        //now get the data we need for a response and then check it before passing it into a service...
        UserData newUser = serializer.fromJson(req.body(), UserData.class);
        Responses response;
        if ((newUser.username() == null) || (newUser.password() == null)) {
            response = new Responses(400);
            response.setMyException(new DataAccessException("Error: invalid request"));
        } else {
            RegistrationServices service = new RegistrationServices();
            response = service.login(newUser.username(), newUser.password(), myDataStorageDevice);
        }
        return new Gson().toJson(response);

    }

    private Object logoutUser(Request req, Response res) {
        //Get a serializer...
        var serializer = new Gson();

        //now get the data we need for a response and then check it before passing it into a service...
        Responses response;
        AuthData oldAuthentication = serializer.fromJson(req.body(), AuthData.class);
        if ((oldAuthentication.username() == null) || (oldAuthentication.authToken() == null)) {
            response = new Responses(400);
            response.setMyException(new DataAccessException("Error: invalid request"));
        } else {
            RegistrationServices service = new RegistrationServices();
            response = service.logout(oldAuthentication, myDataStorageDevice);
        }

        return new Gson().toJson(response);

    }

    private Object listGames(Request req, Response res) {
        var serializer = new Gson();
        AuthData authentication = serializer.fromJson(req.body(), AuthData.class);
        Responses response;
        if ((authentication.username() == null) || (authentication.authToken() == null)) {
            response = new Responses(400);
            response.setMyException(new DataAccessException("Error: invalid request"));
        } else {
            GameServices service = new GameServices();
            response = service.listGames(authentication, myDataStorageDevice);
        }
        return new Gson().toJson(response);

    }

    private Object joinGame(Request req, Response res) {
        var serializer = new Gson();
        //This function needs to check for a null gameID passed in, so it is a little more complicated first
        //making a "new game" with a string representing an int, then making the correct data type to take
        //parts of to make a join game request.
        AuthDataInt newGame = serializer.fromJson(req.body(), AuthDataInt.class);

        Responses response;

        if ((newGame.username() == null) || (newGame.authToken() == null) || (newGame.gameID() == null)) {
            response = new Responses(400);
            response.setMyException(new DataAccessException("Error: invalid request"));
        } else {
            GameServices service = new GameServices();
            AuthDataName game = serializer.fromJson(req.body(), AuthDataName.class);
            AuthData authentication = new AuthData(game.authToken(), game.username());
            response = service.joinGame(authentication, game.gameID(), game.color(), myDataStorageDevice);
        }
        return new Gson().toJson(response);

    }

    private Object createGame(Request req, Response res) {
        var serializer = new Gson();
        NewGame newGame = serializer.fromJson(req.body(), NewGame.class);
        Responses response;
        if ((newGame.username() == null) || (newGame.authToken() == null) || (newGame.gameName() == null)) {
            response = new Responses(400);
            response.setMyException(new DataAccessException("Error: invalid request"));
        } else {
            GameServices service = new GameServices();
            AuthData authentication = new AuthData(newGame.authToken(), newGame.username());
            response = service.createGame(authentication, newGame.gameName(), myDataStorageDevice);
        }
        return new Gson().toJson(response);

    }

    //The last and craziest of all the functions, this does not need to make anything with the input, making it the simplest as well...
    private Object DeleteALL(Request req, Response res) {

        //I actually do not think I need to make any object here...
        //var serializer = new Gson();
        //UserData newUser = serializer.fromJson(req.body(), UserData.class);

        ContolServices service = new ContolServices();
        Responses response = service.DeleteALL(myDataStorageDevice);

        return new Gson().toJson(response);

    }

}

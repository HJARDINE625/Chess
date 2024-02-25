package server;

import dataAccess.DataAccess;
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

        var webDir = Paths.get(Server.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "web");
        Spark.externalStaticFileLocation(webDir.toString());

        //First get request...
        //(will need to convert a json from gson and a json to gson to allow for the request and response type values in spark (not to be confused with my own requests and responses)...
        Spark.post("/user", this::addUser);
        Spark.post("/user", this::loginUser);
        Spark.post("/user", this::logoutUser);
        Spark.post("/user", this::listGames);
        Spark.post("/user", this::joinGame);
        Spark.post("/user", this::createGame);
        //I intentionally changed capitalization on this last one to emphasize how final it is...
        Spark.post("/user", this::DeleteALL);
        //Set up rotes and get responses (turn my responses into a json string and give response class). and return them...

        // Register your endpoints and handle exceptions here.

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }

    private Object addUser(Request req, Response res) {
        UserData newUser = (UserData)Gson.fromJson(req, UserData.class);

        RegistrationServices service = new RegistrationServices();
        Responses response = service.register(newUser, myDataStorageDevice);

        return Gson.toJson(response);

    }

    private Object loginUser(Request req, Response res) {
        UserData newUser = (UserData)Gson.fromJson(req, UserData.class);

        RegistrationServices service = new RegistrationServices();
        Responses response = service.login(newUser.username(), newUser.password(), myDataStorageDevice);

        return Gson.toJson(response);

    }

    private Object logoutUser(Request req, Response res) {
        AuthData oldAuthentication = (AuthData)Gson.fromJson(req, AuthData.class);

        RegistrationServices service = new RegistrationServices();
        Responses response = service.logout(oldAuthentication, myDataStorageDevice);

        return Gson.toJson(response);

    }

    private Object listGames(Request req, Response res) {
        AuthData authentication = (AuthData)Gson.fromJson(req, AuthData.class);

        GameServices service = new GameServices();
        Responses response = service.listGames(authentication, myDataStorageDevice);

        return Gson.toJson(response);

    }

    private Object joinGame(Request req, Response res) {
        AuthDataName newGame = (AuthDataName)Gson.fromJson(req, AuthDataName.class);

        GameServices service = new GameServices();
        AuthData authentication = new AuthData(newGame.authToken(), newGame.username());
        Responses response = service.joinGame(authentication, newGame.gameID(), newGame.color(), myDataStorageDevice);

        return Gson.toJson(response);

    }

    private Object createGame(Request req, Response res) {
        NewGame newGame = (NewGame)Gson.fromJson(req, NewGame.class);

        GameServices service = new GameServices();
        AuthData authentication = new AuthData(newGame.authToken(), newGame.username());
        Responses response = service.createGame(authentication, newGame.gameName(), myDataStorageDevice);

        return Gson.toJson(response);

    }

    //The last and craziest of all the functions, this does not need to make anything with the input, making it the simplest as well...
    private Object DeleteALL(Request req, Response res) {
        UserData newUser = (UserData)Gson.fromJson(req, UserData.class);

        ContolServices service = new ContolServices();
        Responses response = service.DeleteALL(myDataStorageDevice);

        return Gson.toJson(response);

    }

}

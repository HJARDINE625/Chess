package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.DataAccesser;
import dataAccess.DataBaseAccesser;
import model.*;
import service.ContolServices;
import service.GameServices;
import service.RegistrationServices;
import spark.*;
import spark.Response;

import java.nio.file.Paths;
import java.util.Map;

public class Server {

    public Server() {
        webSocketHandler = new WebSocketHandler();
    }

    private DataBaseAccesser myDataStorageDevice = new DataBaseAccesser();
    private ContolServices reset = new ContolServices();
    private RegistrationServices loginStuff = new RegistrationServices();
    private GameServices gameDataAccess = new GameServices();

    private WebSocketHandler webSocketHandler;

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        //I appear to be getting an invalid path, invalid length/CHAR error here... I wonder if I should get my path some other way...
        var webDir =  "web";
        //try this?
        // Register a directory for hosting static files
        //Spark.externalStaticFileLocation("public");
        //this next code crashed the internet until I closed this program!
        Spark.staticFiles.location(webDir.toString());
        Spark.webSocket("/connect", webSocketHandler);


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

        //here is the new connect endpoint.
        //supposedly this is the best way to implement it... not sure what exactaly to do with it...
        //Spark.webSocket("/connect", webSocketHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }

    //private Object handleWebSocket(Request req, Response res){

    //}

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
    //Gson.toJson(response) is causing the errors...
        if(response.getMyException() == null) {
            //This new method implies implicitly knowing what kind of response does exist... but that is an okay amount
            //of coupling because it is implicit (I guess)...
            var finalReturn = new Gson().toJson(response.getMyAuthData());
            return finalReturn;
        } else {
            return errorHandler(response, req, res);
        }

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
        if(response.getMyException() == null) {
            //This new method implies implicitly knowing what kind of response does exist... but that is an okay amount
            //of coupling because it is implicit (I guess)...
            var finalReturn = new Gson().toJson(response.getMyAuthData());
            return finalReturn;
        } else {
            return errorHandler(response, req, res);
        }

    }

    private Object logoutUser(Request req, Response res) {
        //Get a serializer...
        var serializer = new Gson();

        //now get the data we need for a response and then check it before passing it into a service...
        Responses response;
        //AuthData oldAuthentication = serializer.fromJson(req.body(), AuthData.class);
        String authentication = req.headers("authorization");
        if (authentication == null) {
            response = new Responses(400);
            response.setMyException(new DataAccessException("Error: invalid request"));
        } else {
            RegistrationServices service = new RegistrationServices();
            response = service.logout(authentication, myDataStorageDevice);
        }
        if(response.getMyException() == null) {
            //no real return value here...
            return "{}";
        } else {
            return errorHandler(response, req, res);
        }

    }

    private Object listGames(Request req, Response res) {
        var serializer = new Gson();
        //AuthData authentication = serializer.fromJson(req.body(), AuthData.class);
        String authentication = req.headers("authorization");
        Responses response;
        if (authentication == null) {
            response = new Responses(400);
            response.setMyException(new DataAccessException("Error: invalid request"));
        } else {
            GameServices service = new GameServices();
            response = service.listGames(authentication, myDataStorageDevice);
        }
        if(response.getMyException() == null) {
            //This new method implies implicitly knowing what kind of response does exist... but that is an okay amount
            //of coupling because it is implicit (I guess)...
            var finalReturn = new Gson().toJson(response.getAllGames());
            //now I need to specifically check if finalReturn is null... because if it is I need to return something a little different due to
            //My hardcoded Json fix...
            //I need to specifically compare to the "null" string as this is how the Json shows that it found nothing...
            if(finalReturn.equals("null")){
                //the hardcoded nothing to show response...
                return "{ \"games\":" + "[]" + "}";
            } else {
                //Here we do have values...
                //because I stored my values in a standard array, I need to add these Json signifiers after conversion...
                finalReturn = "{ \"games\":" + finalReturn.toString() + "}";
                //var finalCheck = new Gson().toJson(response);
                GameDecomplier gameDecompiler = new GameDecomplier(response.getAllGames());
                var finalCheck = new Gson().toJson(gameDecompiler);
                return finalCheck;
            }
        } else {
            return errorHandler(response, req, res);
        }

    }

    private Object joinGame(Request req, Response res) {
        var serializer = new Gson();
        //This function needs to check for a null gameID passed in, so it is a little more complicated first
        //making a "new game" with a string representing an int, then making the correct data type to take
        //parts of to make a join game request.
        //we represent the int game in capitals to differentiate it from the string...
        AuthDataInt Game = serializer.fromJson(req.body(), AuthDataInt.class);
        String auth = req.headers("authorization");

        Responses response;

        if ((auth == null) || (Game == null)) {
            response = new Responses(400);
            response.setMyException(new DataAccessException("Error: invalid request"));
        } else if(Game.gameID() != null){
            GameServices service = new GameServices();
            AuthDataName game = serializer.fromJson(req.body(), AuthDataName.class);
            String color;
            if(game.color() == null){
                color = Game.color();
            } else {
                color = game.color();
            }
            //AuthData authentication = new AuthData(game.authToken(), game.username());
            response = service.joinGame(auth, game.gameID(), color, myDataStorageDevice);
        } else {
            //We actually still did not have everything we needed...
            response = new Responses(400);
            response.setMyException(new DataAccessException("Error: invalid request"));
        }
        if(response.getMyException() == null) {
            //This new method implies implicitly knowing what kind of response does exist... but that is an okay amount
            //of coupling because it is implicit (I guess)...
            //var finalReturn = new Gson().toJson(response.getMyAuthData());
            return "{}";
        } else {
            return errorHandler(response, req, res);
        }

    }

    private Object createGame(Request req, Response res) {
        var serializer = new Gson();
        NewGame newGame = serializer.fromJson(req.body(), NewGame.class);
        String authentication = req.headers("authorization");
        Responses response;
        if ((authentication == null) || (newGame == null)) {
            response = new Responses(400);
            response.setMyException(new DataAccessException("Error: invalid request"));
        } else if (newGame.gameName() != null){
            GameServices service = new GameServices();
            //AuthData authentication = new AuthData(authentication, newGame.username());
            response = service.createGame(authentication, newGame.gameName(), myDataStorageDevice);
        } else {
            //We have an empty thing, so we still did not find it...
            response = new Responses(400);
            response.setMyException(new DataAccessException("Error: invalid request"));
        }
        if(response.getMyException() == null) {
            //This new method implies implicitly knowing what kind of response does exist... but that is an okay amount
            //of coupling because it is implicit (I guess)...
            var finalReturn = new Gson().toJson(response.getMyGameData());
            return finalReturn;
        } else {
            return errorHandler(response, req, res);
        }


    }

    //The last and craziest of all the functions, this does not need to make anything with the input, making it the simplest as well...
    private Object DeleteALL(Request req, Response res) {

        //I actually do not think I need to make any object here...
        //var serializer = new Gson();
        //UserData newUser = serializer.fromJson(req.body(), UserData.class);

        ContolServices service = new ContolServices();
        Responses response = service.DeleteALL(myDataStorageDevice);

        if(response.getMyException() == null) {
            return "{}";
        } else {
            return errorHandler(response, req, res);
        }

    }
// no longer needed, the errorHandler below makes the code work well it seems...
//    private String unpackException(Responses response) {
//        String errorString = response.getMyException().getMessage();
//        int code = response.getNumericalCode();
//        String myError = String.valueOf(code);
//        String overallCode = "[" + myError + "] { message:" + errorString + " }";
//        return overallCode;
//    }

    public Object errorHandler(Responses response, Request req, Response res) {
        Exception e = response.getMyException();
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.status(response.getNumericalCode());
        res.body(body);
        return body;
    }


}

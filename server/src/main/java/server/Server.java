package server;

import dataAccess.DataAccess;
import dataAccess.DataAccesser;
import model.Responses;
import model.UserData;
import service.ContolServices;
import service.GameServices;
import service.RegistrationServices;
import spark.*;

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

}

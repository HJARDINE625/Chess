package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class ServerConnector {

    //need some kind of init and stop functions

    //might be called from another function in order to allow multiple inputs...


    //The way I set up my functions, this should be appropriate (ie declaring them here)...

    private CreateGame gameCreator = new CreateGame();
    private JoinGame gameJoiner = new JoinGame();
    private Login authValueGenerator = new Login();
    private Logout authValueRemover = new Logout();
    private GetExample gameInfo = new GetExample();

    //now I also need to record these cashed data values

    private AuthData[] authentications = new AuthData[1];

    private String urlString = new String();

    private String game = "/game";
    private String database = "/db";
    private String user = "/user";
    private String auth = "/auth";

    private GameData [] games = new GameData[0];

    //The rest of our code will work if we initialize this to null and change it out everytime we need to
    public ServerConnector(){
        authentications[0] = null;
    }

    //just print out to console here, do not return anything...
    public void completeAction(int selector) throws IOException {
        String message = "Success!\n";
        boolean loggedIn = true;
        boolean addMoreAtEnd = false;
        if(authentications[0] == null){
            loggedIn = false;
        }
        if(selector == 0){
            message = help();
        } else if(selector == 1){
            if(!loggedIn){
                throw new RuntimeException("goodbye!");
            } else {
                try {
                    authValueRemover.doLogout(urlString + auth, authentications[0].authToken());
                    authentications[0] = null;
                    //make sure they know the new options
                    addMoreAtEnd = true;
                } catch(ReportingException r) {
                        message = r.getMessage();
                }
            }
        } else if(selector == 2){
            if(!loggedIn){
                //find some way to get more input mid function here... or I could have an array of other inputs...
                //make sure they know the new options
                addMoreAtEnd = true;
            } else {
                //find some way to get more input mid function here... or I could have an array of other inputs...
            }
        } else if(selector == 3){
            if(!loggedIn){
                //find some way to get more input mid function here... or I could have an array of other inputs...
                //make sure they know the new options
                addMoreAtEnd = true;
            } else {
                try {
                    games = gameInfo.doGet(urlString + game, authentications[0].authToken());
                    if(games != null) {
                        var json = new Gson().toJson(games, GameData[].class);
                        message = message + "From 0 up game by game the number to select each game is the order of appearance of the game in this list...\n";
                        int gameNumber = 0;
                        //here is how we will get the information we need from this particular function...
                        for (GameData g: games) {
                            message = message + "[" + gameNumber + "] : " + "(name) : ";
                            if(g.gameName() != null) {
                                message = message + g.gameName() + " ";
                            }  else {
                                message = message + "NONE! ";
                            }
                            if(g.whiteUsername() != null) {
                                message = message + g.whiteUsername() + " ";
                            }  else {
                                message = message + "NONE! ";
                            }
                            if(g.blackUsername() != null) {
                                message = message + g.blackUsername() + " ";
                            }  else {
                                message = message + "NONE! ";
                            }
                        }
                        message = message + json;
                    } else {
                        message = message + "There is No GAME!\n";
                    }
                } catch(ReportingException r) {
                    message = r.getMessage();
                }
                }
            } else if (loggedIn) {
            if(selector == 4){
                //find some way to get more input mid function here... or I could have an array of other inputs...
            } else if(selector == 5){
                //find some way to get more input mid function here... or I could have an array of other inputs...
            } else {
                message = "Error: that is not a valid option for this client condition!\n";
                //make sure they know the options
                addMoreAtEnd = true;
            }
        } else {
            message = "Error: that is not a valid option for this client condition!\n";
            //make sure they know the options
            addMoreAtEnd = true;
        }
        //Here I need to print out the message I have made... first check if we need new options
        if(addMoreAtEnd){
            completeAction(0);
        }
        //Now print the message to terminal...
    }

    private String help(){
        String returnSupport = new String();
        returnSupport = "Enter the following numbers to execute the following commands:\n[0] : Help\n";
        if(authentications[0] == null){
            return returnSupport + "[1] : Quit\n[2] : Register a new Chess Account and Login\n [3] : Login to an old Chess Account\n";
        } else {
            return returnSupport + "[1] : Logout of this Chess Account\n[2] : Create a new Chess Game\n[3] : List all Chess Games that currently exist on this Server\n[4] : Join an available Chess Game\n [5] : Join as an observer of any Chess Game\n";
        }
    }




}

package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashSet;
import java.util.UUID;

public class DataAccess implements DataAccesser{

    private HashSet<UserData> users = new HashSet<UserData>();

    private HashSet<AuthData> authentications = new HashSet<AuthData>();

    private HashSet<GameData> games = new HashSet<GameData>();
    @Override
    public boolean clear() {
        //reset the three databases by defining them as new databases with nothing inside.
        users = new HashSet<UserData>();
        authentications = new HashSet<AuthData>();
        games = new HashSet<GameData>();
        //now it worked, so return.
        return true;
    }

    //I am using the booleans later to check for errors, so this will only be called if it can work...
    @Override
    public UserData createUser(String username, String password, String email) {
        UserData newUser = new UserData(username, password, email);
        users.add(newUser);
        return newUser;
        //now call login separately in RegistrationService...
    }

    @Override
    public UserData getUser(String username, String password) {
        for (UserData user: users) {
            if(user.username() == username){
                if(user.password() == password){
                    return user;
                }
                //make the main method find this and if it is found throw incorrect password error...
                return null;
            }
        }
        //make the main method find this and if it is found throw incorrect password error...
        return null;
    }

    @Override
    public GameData createGame(String gameName) {
        return null;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public GameData[] listGames() {
        return new GameData[0];
    }

    @Override
    public GameData updateGame(String gameName) {
        return null;
    }

    //only call this after calling get user and checking for non-null responses...
    @Override
    public AuthData createAuth(String username) {
        //some formula for making new authentications
        boolean newUniqueValueFound = false;
        String authValue = new String();
        while(newUniqueValueFound == false) {
            authValue = UUID.randomUUID().toString();
            newUniqueValueFound = true;
            if (!authentications.isEmpty()){
                for (AuthData authenticator: authentications) {
                    if (authenticator.authToken().equals(authValue)) {
                        newUniqueValueFound = false;
                        break;
                    }
                }
            }
        }
        AuthData authenticator = new AuthData(authValue, username);
        authentications.add(authenticator);
        return authenticator;

    }

    //Might not be necessary in this implementation...
    @Override
    public AuthData getAuth() {
        return null;
    }

    //Can call this function without calling function below...
    @Override
    public boolean deleteAuth(AuthData authenticator) {
        if(authentications.isEmpty()){
            return false;
        } else {
            if (authentications.contains(authenticator)) {
                authentications.remove(authenticator);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean checkAuthorization(AuthData authenticator) {
        if(authentications.isEmpty()){
            return false;
        } else {
            if (authentications.contains(authenticator)) {
                return true;
            } else {
                return false;
            }
        }
    }

    //only call the color checkers after calling locate game id, and call first noSuchColor then colorTaken...
    @Override
    public boolean colorExists(String color, int gameID) {
        //For now I have implemented this in a way that is fairly easy to check.
        if(color != null) {
            if ((color.equals("WHITE")) || (color.equals("BLACK"))) {
                return true;
                //however at some time, if I implement more teams than just black or white, I will want another checker that is more intelligent, I can ask about this...
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    //call this check last...
    @Override
    public boolean colorNotTaken(String color, int gameID) {
        if(color == null){
            return true;
        } else {
            for (GameData chessGame: games) {
                //remember if we implemented everything else right, there should only be one of these!
                if(chessGame.gameID() == gameID){
                    //so far this will work, update to add more players...
                    if(color.equals("WHITE")){
                        if(chessGame.whiteUsername() == null){
                            return true;
                        } else {
                            return false;
                        }
                    } else if(color.equals("BLACK")){
                        if(chessGame.blackUsername() == null){
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
            return false;
        }
    }

    @Override
    public boolean locateUsername(String username) {
        if(users.isEmpty()){
            return false;
        } else {
            //This will work just fine, as long as no one ever glitches the system to accept a faulty user with no username, assuming this will not happen should be a fine assumption.
            for (UserData user: users) {
                if(user.username() == username){
                    return true;
                }
            }
            return false;
        }
    }
    //repitiious to what is above, but the Professor said that repition here was fine...

    @Override
    public boolean locateGameID(int gameID) {
        if(games.isEmpty()){
            return false;
        } else {
            //This will work just fine, as long as no one ever glitches the system to accept a faulty game with no ID, assuming this will not happen should be a fine assumption.
            for (GameData game: games) {
                if(game.gameID() == gameID){
                    return true;
                }
            }
            return false;
        }
    }


}

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
            if(user.username().equals(username)){
                if(user.password().equals(password)){
                    return user;
                }
                //make the main method find this and if it is found throw incorrect password error...
                return null;
            }
        }
        //make the main method find this and if it is found throw incorrect password error...
        return null;
    }

    //only call if locate game ID does not return an error

    @Override
    public GameData createGame(String gameName) {
        //some formula for making new games
        boolean newUniqueValueFound = false;
        //capitalized to prevent mix-up with element gameID
        int GameID = 0;
        while(newUniqueValueFound == false) {
            GameID = UUID.randomUUID().hashCode();
            newUniqueValueFound = true;
            if (!games.isEmpty()){
                for (GameData game: games) {
                    if (game.gameID() == GameID) {
                        newUniqueValueFound = false;
                        break;
                    }
                }
            }
        }
        //The game will be very incomplete so far as no-one has joined as white or black... but it will need a new ChessGame
        ChessGame gameOfChess = new ChessGame();
        GameData newGame = new GameData(GameID, null, null, gameName, gameOfChess);
        games.add(newGame);
        return newGame;
    }

    //call after proving game exists...
    @Override
    public GameData getGame(int gameID) {
        for (GameData game:games) {
            if(game.gameID() == gameID){
                return game;
            }
        }
        //if we did not find it something went very wrong
        return null;
    }

    @Override
    public GameData[] listGames() {
        //look for null response and other response from this function
        if(games.isEmpty()){
            return null;
        } else {
            int sizeOfReturnArray = games.size();
            GameData[] returnArray = new GameData[sizeOfReturnArray];
            int currentArrayLocation = 0;
            for (GameData game: games) {
                returnArray[currentArrayLocation] = game;
                currentArrayLocation++;
            }
            //we added every game
            return returnArray;
        }
    }

    //First check this person is authorized then that the game exists then that color is available... Then call this function...
    @Override
    public GameData updateGame(int gameID, String clientColor, String username) {
        //add the new game to return
        GameData newGame = new GameData(0, null, null, null, null);
        //and the game to watch
        GameData oldGame = new GameData(0, null, null, null, null);
        //We have already checked that the username exists, so add them as the user...
        for (GameData chessGame : games) {
            //remember if we implemented everything else right, there should only be one of these!
            if (chessGame.gameID() == gameID) {
                //watch this game
                oldGame = chessGame;
                //add the otherplayer  string definition
                String otherPlayer = null;
                //so far this will work, update to add more players...
                if (clientColor.equals("WHITE")) {
                    //first get the black username (regardless of it is null)...
                    if (chessGame.blackUsername() != null) {
                        otherPlayer = chessGame.blackUsername();
                    }
                    //add a white player
                    newGame = new GameData(gameID, username, otherPlayer, chessGame.gameName(), chessGame.implementation());
                } else if (clientColor.equals("BLACK")) {
                    //first get the white username (regardless of it is null)...
                    if (chessGame.whiteUsername() != null) {
                        otherPlayer = chessGame.whiteUsername();
                    }
                    //add a black player
                    newGame = new GameData(gameID, otherPlayer, username, chessGame.gameName(), chessGame.implementation());
                } else {
                    //I am not sure that we need to do anything to add a new observer....
                    return chessGame;
                }
            }
        }
        //we must not have changed nothing (otherwise we would have returned), so let us update the chart with one old game gone and a new one in the place of it.
        games.remove(oldGame);
        games.add(newGame);
        return newGame;
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
                if(user.username().equals(username)){
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

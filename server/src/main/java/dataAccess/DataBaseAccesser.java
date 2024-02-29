package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Properties;
import java.util.UUID;

public class DataBaseAccesser implements DataAccesser{

    private HashSet<UserData> users = new HashSet<UserData>();

    private HashSet<AuthData> authentications = new HashSet<AuthData>();

    private HashSet<GameData> games = new HashSet<GameData>();

    //private DatabaseManager databaseCreator;

    private RowManager rowUpdater;

    private TableManager tableCreator;

    private Connection conn;

    private String userTable = "user";
    private String authTable = "auth";
    private String gameTable = "game";


    public DataBaseAccesser(){
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) throw new Exception("Unable to laod db.properties");
            Properties props = new Properties();
            props.load(propStream);
            DatabaseManager.createDatabase();
            conn = DatabaseManager.getConnection();
            tableCreator = new TableManager();
            tableCreator.configureDatabase(conn,props.getProperty("db.name"));
            rowUpdater = new RowManager();
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean clear() {
        //reset the three databases by defining them as new databases with nothing inside.
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to laod db.properties");
                Properties props = new Properties();
                props.load(propStream);
                tableCreator.truncateALLTables(conn,props.getProperty("db.name"));
            } catch (DataAccessException e) {
                throw new RuntimeException(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        //now it worked, so return.
        return true;
    }

    //I am using the booleans later to check for errors, so this will only be called if it can work...
    @Override
    public UserData createUser(String username, String password, String email) {
        UserData newUser = new UserData(username, password, email);
        try {
            rowUpdater.insert(username, 1, conn, userTable);
            rowUpdater.update(password, 2, username, conn, userTable);
            rowUpdater.update(email, 3, username, conn, userTable);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
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
            //negative and zero hashes are invalid, for some reason...
            if (GameID <= 0) {
                //this is an odd way to do this, but it seems efficient enough...
                newUniqueValueFound = false;
                //we will only continue if we have a valid hashcode...
            } else {
                //now we need to make sure that this hash does not already exist in the database...
                if (!games.isEmpty()) {
                    for (GameData game : games) {
                        if (game.gameID() == GameID) {
                            newUniqueValueFound = false;
                            break;
                        }
                    }
                }
            }
        }
        //The game will be very incomplete so far as no-one has joined as white or black... but it will need a new ChessGame
        ChessGame gameOfChess = new ChessGame();
        GameData newGame = new GameData(GameID, null, null, gameName, gameOfChess);
        var chessGame = new Gson().toJson(gameOfChess);
        try {
            rowUpdater.insert(GameID, 1, conn, gameTable);
            rowUpdater.update(null, 2, GameID, conn, gameTable);
            rowUpdater.update(null, 3, GameID, conn, gameTable);
            rowUpdater.update(gameName, 4, GameID, conn, gameTable);
            rowUpdater.update(chessGame, 5, GameID, conn, gameTable);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
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
    public GameData updateGame(int gameID, String clientColor, String auth) {
        //We only call this at appropriate times (when the user has been found), so I think it is fine to do this...
        String username = "";
        for (AuthData user: authentications) {
            if(user.authToken().equals(auth)) {
                username = user.username();
                break;
            }
        }
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
                if(clientColor != null) {
                    if (clientColor.equals("WHITE")) {
                        //first get the black username (regardless of it is null)...
                        if (chessGame.blackUsername() != null) {
                            otherPlayer = chessGame.blackUsername();
                        }
                        //add a white player
                        newGame = new GameData(gameID, username, otherPlayer, chessGame.gameName(), chessGame.implementation());
                        try {
                            rowUpdater.update(username, 2, gameID, conn, gameTable);
                        } catch (DataAccessException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    } else if (clientColor.equals("BLACK")) {
                        //first get the white username (regardless of it is null)...
                        if (chessGame.whiteUsername() != null) {
                            otherPlayer = chessGame.whiteUsername();
                        }
                        //add a black player
                        newGame = new GameData(gameID, otherPlayer, username, chessGame.gameName(), chessGame.implementation());
                        try {
                            rowUpdater.update(username, 3, gameID, conn, gameTable);
                        } catch (DataAccessException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    }
                }else {
                    //I am not sure that we need to do anything to add a new observer....
                    return chessGame;
                }
            }
        }
        //we must not have changed nothing (otherwise we would have returned), so let us update the chart with one old game gone and a new one in the place of it.
        //games.remove(oldGame);
        //games.add(newGame);
        //done above...
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
        try {
            rowUpdater.insert(authValue, 1, conn, authTable);
            rowUpdater.update(username, 2, authValue, conn, gameTable);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
        //uthentications.add(authenticator);
        return authenticator;

    }

    //Might not be necessary in this implementation...
    //@Override
    //public AuthData getAuth() {
    // return null;
    // }

    //Can call this function without calling function below...
    @Override
    public boolean deleteAuth(String authenticator) {
        if(authentications.isEmpty()){
            return false;
        } else {
            boolean foundIt = false;
            AuthData authTokenToDelete = new AuthData(null, null);
            for (AuthData authenticate: authentications) {
                if(authenticate.authToken().equals(authenticator)) {
                    foundIt = true;
                    authTokenToDelete = authenticate;
                    break;
                }
            } if (foundIt) {
                try {
                    rowUpdater.delete(authenticator, 1, conn, authTable);
                } catch (DataAccessException e) {
                    throw new RuntimeException(e.getMessage());
                }
                //authentications.remove(authTokenToDelete);
                return true;
            }else {
                return false;
            }
        }
    }

    @Override
    public boolean checkAuthorization(String authenticator) {
        if(authentications.isEmpty()){
            return false;
        } else {
            boolean foundIt = false;
            for (AuthData authenticate: authentications) {
                if(authenticate.authToken().equals(authenticator)) {
                    foundIt = true;
                    break;
                }
            }
            return foundIt;
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


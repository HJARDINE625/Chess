package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

public interface DataAccesser {
    public boolean clear();
    public UserData createUser(String username, String password, String email) throws DataAccessException;
    public UserData getUser(String username, String password) throws DataAccessException;
    public GameData createGame(String gameName) throws DataAccessException;

    //only call if there is a game
    boolean subtractWatcher(int gameID, String watcherName) throws DataAccessException;

    //only call if there is a game
    boolean addWatcher(int gameID, String watcherName) throws DataAccessException;

    //only call if there is a game
    String[] getWatchers(int gameID) throws DataAccessException;

    public GameData getGame(int gameID) throws DataAccessException;
    public GameData[] listGames() throws DataAccessException;
    public GameData updateGame(int gameID, String clientColor, String auth) throws DataAccessException;

    public GameData updateGame(String username, String clientColor, int gameID) throws DataAccessException;

    //NOTE: for the breakdown of steps I have broken up the overall pieces of this project into parts here and parts elsewhere... DO NOT CALL THIS FUNCTION UNTIL YOU CHECKED THAT THE CHANGE MAKES SENSE ELSEWHERE!!!
    GameData modifyGameState(int gameID, ChessGame implementation) throws DataAccessException;

    public AuthData createAuth(String username) throws DataAccessException;

    public String usernameFinder(String auth) throws DataAccessException;
    //public AuthData getAuth();
    public boolean deleteAuth(String authenticator) throws DataAccessException;
    public boolean checkAuthorization(String authenticator) throws DataAccessException;
    public boolean colorExists(String color, int gameID);
    public boolean colorNotTaken(String color, int gameID) throws DataAccessException;
    public boolean locateUsername(String username) throws DataAccessException;
    public boolean locateGameID(int gameID) throws DataAccessException;
}

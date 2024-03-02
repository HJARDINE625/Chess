package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface DataAccesser {
    public boolean clear();
    public UserData createUser(String username, String password, String email);
    public UserData getUser(String username, String password);
    public GameData createGame(String gameName);
    public GameData getGame(int gameID);
    public GameData[] listGames();
    public GameData updateGame(int gameID, String clientColor, String auth);
    public AuthData createAuth(String username) throws DataAccessException;
    //public AuthData getAuth();
    public boolean deleteAuth(String authenticator) throws DataAccessException;
    public boolean checkAuthorization(String authenticator);
    public boolean colorExists(String color, int gameID);
    public boolean colorNotTaken(String color, int gameID);
    public boolean locateUsername(String username);
    public boolean locateGameID(int gameID);
}

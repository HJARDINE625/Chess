package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface DataAccesser {
    public boolean clear();
    public UserData createUser();
    public UserData getUser();
    public GameData createGame();
    public GameData getGame();
    public GameData[] listGames();
    public GameData updateGame();
    public AuthData createAuth();
    public AuthData getAuth();
    public boolean deleteAuth();
    public boolean checkAuthorization();
    public boolean noSuchColor();
    public boolean colorTaken();
    public boolean locateUsername();
    public boolean locateGameID();
}

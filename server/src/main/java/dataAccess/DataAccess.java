package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashSet;

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

    @Override
    public UserData createUser() {
        return null;
    }

    @Override
    public UserData getUser() {
        return null;
    }

    @Override
    public GameData createGame() {
        return null;
    }

    @Override
    public GameData getGame() {
        return null;
    }

    @Override
    public GameData[] listGames() {
        return new GameData[0];
    }

    @Override
    public GameData updateGame() {
        return null;
    }

    @Override
    public AuthData createAuth() {
        return null;
    }

    @Override
    public AuthData getAuth() {
        return null;
    }

    @Override
    public boolean deleteAuth() {
        return false;
    }

    @Override
    public boolean checkAuthorization() {
        return false;
    }

    @Override
    public boolean noSuchColor() {
        return false;
    }

    @Override
    public boolean colorTaken() {
        return false;
    }

    @Override
    public boolean locateUsername() {
        return false;
    }

    @Override
    public boolean locateGameID() {
        return false;
    }
}

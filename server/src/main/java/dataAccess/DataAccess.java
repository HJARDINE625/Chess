package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashSet;

public class DataAccess implements DataAccesser{

    private HashSet<UserData> users = new HashSet<UserData>();

    private HashSet<AuthData> authentications = new HashSet<AuthData>();

    private HashSet<GameData> Games = new HashSet<GameData>();
    @Override
    public boolean clear() {
        return false;
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

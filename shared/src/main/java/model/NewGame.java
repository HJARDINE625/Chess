package model;

import java.util.Objects;

public class NewGame {
    private final String authToken;
    private final String username;

    private final String gameName;

    public NewGame(String authToken, String username, String gameName) {
        this.authToken = authToken;
        this.username = username;
        this.gameName = gameName;
    }

    public String authToken() {
        return authToken;
    }

    public String username() {
        return username;
    }

    public String gameName() {return gameName;}

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (NewGame) obj;
        return Objects.equals(this.authToken, that.authToken) &&
                Objects.equals(this.username, that.username) &&
                Objects.equals(this.gameName, that.gameName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, username, gameName);
    }

    @Override
    public String toString() {
        return "AuthData[" +
                "authToken=" + authToken + ", " +
                "username=" + username + ']' +
                "gamename=" + gameName;
    }
}

package model;

import java.util.Objects;

public class AuthDataName {
    private final String authToken;
    private final String username;

    private final String joinColor;

    private final int gameID;

    public AuthDataName(String authToken, String username, int gameID, String joinColor) {
        this.authToken = authToken;
        this.username = username;
        this.gameID = gameID;
        this.joinColor = joinColor;
    }

    public String authToken() {
        return authToken;
    }

    public String username() {
        return username;
    }

    public int gameID() {return gameID;}

    public String color() {return joinColor;}

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AuthDataName) obj;
        return Objects.equals(this.authToken, that.authToken) &&
                Objects.equals(this.username, that.username) &&
                Objects.equals(this.gameID, that.gameID) &&
                Objects.equals(this.joinColor, that.joinColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, username, gameID, joinColor);
    }

    @Override
    public String toString() {
        return "AuthData[" +
                "authToken=" + authToken + ", " +
                "username=" + username + ']' +
                "gameID=" + gameID +
                "color=" + joinColor;
    }
}

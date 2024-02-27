package model;

import java.util.Objects;

public class AuthDataName {

    private final String playerColor;

    private final int gameID;

    public AuthDataName(String authToken, String username, int gameID, String playerColor) {
        this.gameID = gameID;
        this.playerColor = playerColor;
    }


    public int gameID() {return gameID;}

    public String color() {return playerColor;}

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AuthDataName) obj;
        return Objects.equals(this.gameID, that.gameID) &&
                Objects.equals(this.playerColor, that.playerColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, playerColor);
    }

    @Override
    public String toString() {
        return "gameID=" + gameID +
                "playerColor=" + playerColor;
    }
}

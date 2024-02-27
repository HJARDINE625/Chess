package model;

import java.util.Objects;

public class AuthDataName {

    private final String joinColor;

    private final int gameID;

    public AuthDataName(String authToken, String username, int gameID, String joinColor) {
        this.gameID = gameID;
        this.joinColor = joinColor;
    }


    public int gameID() {return gameID;}

    public String color() {return joinColor;}

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AuthDataName) obj;
        return Objects.equals(this.gameID, that.gameID) &&
                Objects.equals(this.joinColor, that.joinColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, joinColor);
    }

    @Override
    public String toString() {
        return "gameID=" + gameID +
                "color=" + joinColor;
    }
}

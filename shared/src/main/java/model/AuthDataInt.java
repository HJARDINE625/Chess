package model;

import java.util.Objects;

public class AuthDataInt {

    private final String joinColor;

    private final String gameID;

    public AuthDataInt(String gameID, String joinColor) {
        this.gameID = gameID;
        this.joinColor = joinColor;
    }



    public String gameID() {return gameID;}

    public String color() {return joinColor;}

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AuthDataInt) obj;
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

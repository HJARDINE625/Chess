package model;

import java.util.Objects;

public class NewGame {

    private final String gameName;

    public NewGame(String authToken, String username, String gameName) {
        this.gameName = gameName;
    }


    public String gameName() {return gameName;}

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (NewGame) obj;
        return Objects.equals(this.gameName, that.gameName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameName);
    }

    @Override
    public String toString() {
        return "gamename=" + gameName;
    }
}

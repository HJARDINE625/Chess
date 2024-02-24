package model;

public class Responses {
    private final int numericalCode;
    private AuthData myAuthData;
    private GameData myGameData;

    private GameData[] allGames;
    private UserData myUserData;
    private Exception myException;

    public Responses(int numericalCode) {
        this.numericalCode = numericalCode;
    }

    public Exception getMyException() {
        return myException;
    }

    public void setMyException(Exception myException) {
        this.myException = myException;
    }

    public UserData getMyUserData() {
        return myUserData;
    }

    public void setMyUserData(UserData myUserData) {
        this.myUserData = myUserData;
    }

    public GameData[] getAllGames() {
        return allGames;
    }

    public void setAllGames(GameData[] allGames) {
        this.allGames = allGames;
    }

    public GameData getMyGameData() {
        return myGameData;
    }

    public void setMyGameData(GameData myGameData) {
        this.myGameData = myGameData;
    }

    public AuthData getMyAuthData() {
        return myAuthData;
    }

    public void setMyAuthData(AuthData myAuthData) {
        this.myAuthData = myAuthData;
    }
}

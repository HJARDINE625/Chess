package model;

//The way this function is set up, it can have any number of different response types, but should only have one at a
//time. It must be unpacked based upon the booleans for what it has and then it can be converted into an object and then
//a Json...

public class Response {

    private final int numericalCode;
    private AuthData myAuthData;
    private GameData myGameData;

    private GameData[] allGames;
    private UserData myUserData;
    private Exception myException;

    private boolean error;
    private boolean hasUserData;
    private boolean hasAuthData;
    private boolean hasSingleGameData;
    private boolean hasSeveralGameData;

    public Response(int numericalCode) {
        this.error = false;
        this.hasUserData = false;
        this.hasAuthData = false;
        this.hasSingleGameData = false;
        this.hasSeveralGameData = false;
        this.numericalCode = numericalCode;
    }

    public Exception getMyException() {
        return myException;
    }

    public void setMyException(Exception myException) {
        this.myException = myException;
        this.error = true;
    }

    public UserData getMyUserData() {
        return myUserData;
    }

    public void setMyUserData(UserData myUserData) {
        this.myUserData = myUserData;
        this.hasUserData = true;
    }

    public GameData[] getAllGames() {
        return allGames;
    }

    public void setAllGames(GameData[] allGames) {
        this.allGames = allGames;
        this.hasSeveralGameData = true;
    }

    public GameData getMyGameData() {
        return myGameData;
    }

    public void setMyGameData(GameData myGameData) {
        this.myGameData = myGameData;
        this.hasSingleGameData = true;
    }

    public AuthData getMyAuthData() {
        return myAuthData;
    }

    public void setMyAuthData(AuthData myAuthData) {
        this.myAuthData = myAuthData;
        this.hasAuthData = true;
    }

    public boolean isError() {
        return error;
    }

    public boolean isHasUserData() {
        return hasUserData;
    }

    public boolean isHasAuthData() {
        return hasAuthData;
    }

    public boolean isHasSingleGameData() {
        return hasSingleGameData;
    }

    public int getNumericalCode() {
        return numericalCode;
    }

    public boolean isHasSeveralGameData() {
        return hasSeveralGameData;
    }
}

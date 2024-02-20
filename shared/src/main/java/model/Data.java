package model;
import java.util.Objects;
public class Data {
    Data(){}
    public AuthData getMyAuthData() {
        return myAuthData;
    }

    public void setMyAuthData(AuthData myAuthData) {
        this.myAuthData = myAuthData;
    }

    public GameData getMyGameData() {
        return myGameData;
    }

    public void setMyGameData(GameData myGameData) {
        this.myGameData = myGameData;
    }

    public UserData getMyUserData() {
        return myUserData;
    }

    public void setMyUserData(UserData myUserData) {
        this.myUserData = myUserData;
    }

    public Exception getMyException() {
        return myException;
    }

    public void setMyException(Exception myException) {
        this.myException = myException;
    }


    private AuthData myAuthData;
    private GameData myGameData;
    private UserData myUserData;
    private Exception myException;

}

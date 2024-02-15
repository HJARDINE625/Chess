package service;

import dataAccess.DataBaseAccess;
import model.AuthData;
import model.Data;
import model.UserData;

public class RegistrationService {

        public DataBaseAccess myDataBaseAcess;
    public AuthData register(UserData user){
        //somehow we need to access the same database every time we add a user.
            Data userInfo = myDataBaseAcess.Add(user.username(), "UserName", true);
            if(user.getMyUserData() == null){
                Throw Error;
            }


    }

}

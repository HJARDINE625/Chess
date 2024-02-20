package service;

import dataAccess.DataBaseAccess;
import model.AuthData;
import model.Data;
import model.UserData;

public class RegistrationService {

        public DataBaseAccess myDataBaseAcess;
    public AuthData register(UserData user){
        //somehow we need to access the same database every time we add a user.

        //this step was done previously in the web server step, remove
        //check that we have everything a user should have

            //now check that the data works
            Data userName = myDataBaseAcess.Add(user.username(), "UserName", true);
            if(userName.getMyUserData() == null){
                //update this to properly form and use the error
                Throw userName.Error;
            } else {
                //To make this work, we will need to change DataBaseAccess to allow for multiple inputs to allow for an add...
                //or maybe we simply need to modify the entry we made above, adding a email and a password section...
                //Three and Two can change, they are were I currently expect email and password to be kept.
            Data userEmail = myDataBaseAcess.Alter(user.username(), "Email", user.email(), 3);
            Data password = myDataBaseAcess.Alter(user.username(), "Password", user.password(), 2);
                if((userEmail.getMyUserData() != null) && (password.getMyUserData() != null)){
                    return this.login(user);
                } else {
                    throw new Exception();
                }
            }

    }

    //this returns an authtoken
    public AuthData login(UserData user){
        //I think there will only be two elements in this database (having not yet made it)
        Data userCheck = myDataBaseAcess.Select(user.username(), "Password");
        if(userCheck.getMyUserData().password() == null){
            throw new Exception("unathorized");
        }
        if(userCheck.getMyUserData().password() == user.password()) {
            Data userLogin = myDataBaseAcess.Generate(user.username(), "Authentication");
            if (userLogin.getMyAuthData() == null) {
                throw userLogin.getMyException();
            } else {
                return userLogin.getMyAuthData();
            }
        } else {
            throw new Exception("unathorized");
        }
    }

    //this destroys an authtoken...
    public void logout(AuthData authToken){
        if(authToken.authToken() == null){
            throw new Exception("unathorized");
        } else{
            Data foundSomething = myDataBaseAcess.Select(authToken.authToken(), "Authentication");
            //Make sure we can find something... if we can, good work!
            if(foundSomething.getMyAuthData() != null) {
                if (foundSomething.getMyAuthData().authToken() != null) {
                    myDataBaseAcess.Delete(authToken.authToken(), "Authentication");
                    return;
                }
            }
            //we may want to change this Exception at some point, for now it helps show the skeleton of what this is supposed to do.
            throw new Exception("unathorized");
        }
    }


}

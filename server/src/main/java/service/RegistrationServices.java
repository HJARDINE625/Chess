package service;

import dataAccess.DataAccessException;
import dataAccess.DataAccesser;
import dataAccess.DataAccess;
import model.*;

public class RegistrationServices {
    public Responses register(UserData user, DataAccesser myDatabase){
        //somehow we need to access the same database every time we add a user.

        //this step was done previously in the web server step, remove
        //check that we have everything a user should have

        //now check that the data works
        if(!myDatabase.locateUsername(user.username())){
            UserData newUser = myDatabase.createUser(user.username(), user.password(), user.email());
            AuthData loginAuthentication = myDatabase.createAuth(newUser.username());
            //Now we have the user input to the database and a new login token for them, so we should return it in a response...
            Responses successResponse = new Responses(200);
            successResponse.setMyAuthData(loginAuthentication);
            return successResponse;
        } else {
            //If we got here we already have that user
            Responses faliureResponse = new Responses(403);
            faliureResponse.setMyException(new DataAccessException("Error: already taken"));
            return faliureResponse;
        }

    }

    //this returns an authtoken
    //Can either leave email null and use a user or make a new data class
    public Responses login(String username, String password, DataAccesser myDatabase){
        //I think there will only be two elements used from this database (having not yet made it)
        //Then it will add an AuthToken to a new database based off of it, but only if the username and password match...
        UserData thisUser = myDatabase.getUser(username, password);
        //check if we got a null user, if so this actually just threw an error...
        if(thisUser == null){
            Responses faliureResponse = new Responses(401);
            faliureResponse.setMyException(new DataAccessException("Error: unauthorized"));
            return faliureResponse;
        } else{
            //We got something back, which means the person was found...
            AuthData authenticationToken = myDatabase.createAuth(username);
            Responses successResponse = new Responses(200);
            successResponse.setMyAuthData(authenticationToken);
            return successResponse;
        }

    }

    //this destroys an authtoken...
    public Responses logout(String authentication, DataAccesser myDatabase){
        //We need to make sure that the authToken is real and really applies to the person... cancel the second part...
        //they do not seem to care about mentioning the person beyond authentication...
        //We can do this at the same time as removing that authorization by using a special DataAccessor function
        if(myDatabase.deleteAuth(authentication)){
            return new Responses(200);
        } else {
            //clearly we could not find the thing to delete
            //Strictly speaking, I think this is the wrong error, but it is the only named kind they wanted us to return...
            Responses mistake = new Responses(401);
            mistake.setMyException(new DataAccessException("Error: unauthorized"));
            return mistake;
        }
    }


}

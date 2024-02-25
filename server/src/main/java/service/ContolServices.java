package service;

import dataAccess.DataAccess;
import dataAccess.DataAccesser;
import model.Response;
import model.Responses;

public class ContolServices {
    //ALL intentionally capitalized
    public Responses DeleteALL(DataAccesser myDatabase){
        //for now I will assume these are my databases
        myDatabase.clear();
        //add the Response
        return new Responses(200);
       //That is actually it for now...
    }
}

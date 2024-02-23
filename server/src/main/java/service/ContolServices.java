package service;

import dataAccess.DataAccess;
import model.Response;

public class ContolServices {
    //ALL intentionally capitalized
    public Response DeleteALL(DataAccess myDatabase){
        //for now I will assume these are my databases
        myDatabase.clear();
        //add the Response
        return new Response(200);
       //That is actually it for now...
    }
}

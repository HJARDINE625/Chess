package service;

import dataAccess.DataBaseAccess;

public class ControlService {
    public DataBaseAccess myDataBaseAcess;


    //ALL intentionally capitalized
    public void DeleteALL(){
        //for now I will assume these are my databases
        myDataBaseAcess.DeleteALL("Password");
        myDataBaseAcess.DeleteALL("Email");
        myDataBaseAcess.DeleteALL("Authentication");
        myDataBaseAcess.DeleteALL("Games");
        myDataBaseAcess.DeleteALL("UserName");
    }
}

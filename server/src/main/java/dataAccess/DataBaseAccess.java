package dataAccess;

import model.Data;

public class DataBaseAccess {
    //we need to be able to use the database directly and it ought to be the same one each time...
private DatabaseManager myDataBaseAccess;

//this function simply finds the item from the selected database... this does require a little bit of coupling, because you need to know what kind of database to search for, but it removes some by allowing a generic "data" return type.
public Data Select(String findMe, String database) throws DataAccessException {

    return null;
}

//find the selected data and then alter it, otherwise passup the not-found data error on the chain.
public Data Alter(String findMe, String database, String becomeMe) throws DataAccessException {

    //call the other one effecting only the main selection line
    return (Alter(findMe, database, becomeMe, 1));
}

//add to a new section in Alter
public Data Alter(String findMe, String database, String becomeMe, int thisElement) throws DataAccessException {
    Select(findMe, database);

    return null;
}

//this may be unessisary if we set up the database generation properties correctly
public Data Generate(String findMe, String database) throws DataAccessException {
    Select(findMe, database);
    //if this does not exist create it...
    //Add(findMe, database);
    //Now create a new Authentication associtated with it
    //Add (or something)
    //now return
    return null;
}



//find and destory the selected data.

public Data Delete(String findMe, String database) throws DataAccessException {
    Alter(findMe, database, null);

    return null;
}

//call this multiple times to allow for unpacking each part of the database...
public Data Add(String thingToAdd, String database, boolean singularItem) throws DataAccessException {
    if(singularItem){
        if(Select(thingToAdd, database) != null){
            //throw the already exists error!
            return null;
        } else {
            //add it
            return null;
        }
    } else {
        //add it, we do not need to check for duplication.
        return null;
    }
}


//YES I made the ALL ALL caps, this is to remind people of how crazy of an action they are doing
public Data DeleteALL(String database) throws DataAccessException {
    return null;
}

//this destores everything in the selected database, call it a few times to destory all three databases.
public Data[] SelectALL(String database) throws DataAccessException {
    return null;
}

//find out what is in the DATA that is passed around.

private String unpack() throws DataAccessException  {
    return null;
}




}

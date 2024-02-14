package dataAccess;

import model.Data;

public class DataBaseAccess {

public Data Select(String findMe, String database){

    return null;
}

public Data Alter(String findMe, String database, String becomeMe){
    Select(findMe, database);

    return null;
}

public Data Delete(String findMe, String database) {
    Alter(findMe, database, null);

    return null;
}

//call this multiple times to allow for unpacking each part of the database...
public Data Add(String thingToAdd, String database, boolean singularItem){
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
public Data DeleteALL(String database){
    return null;
}

public Data[] SelectALL(String database){
    return null;
}

private String unpack() {
    return null;
}




}

package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.DataAccesser;
import model.GameData;
import model.Responses;

public class WebSocketServices {

    public String getName(String auth, DataAccesser myDatabase) throws DataAccessException {
        return myDatabase.usernameFinder(auth);
    }

    public Responses removeName(String auth, DataAccesser myDatabase, ChessGame.TeamColor team, int gameID) {
        try {
            if (!myDatabase.locateGameID(gameID)) {
                Responses faliureResponse = new Responses(400);
                //I think this is the kind of error they want, they may want 500 or something...
                faliureResponse.setMyException(new DataAccessException("Error: bad request"));
                return faliureResponse;
            }
            if(team != null) {
                if (myDatabase.colorExists(team.toString(), gameID)) {
                    //The exception thrown by the next line should be impossible to actually get if this is only called at appropriate times...
                    if (!myDatabase.colorNotTaken(team.toString(), gameID)) {

                    } else {
                        Responses faliureResponse = new Responses(400);
                        //I think this is the kind of error they want, they may want 500 or something...
                        faliureResponse.setMyException(new DataAccessException("Error: bad request"));
                        return faliureResponse;
                    }
                } else {
                    //The player is not taken and thus cannot log out...
                    Responses faliureResponse = new Responses(401);
                    //I think this is the kind of error they want, they may want 500 or something...
                    faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
                    return faliureResponse;
                }
            } else{
                //find the name in the observers list...
            }
        }catch (DataAccessException e){
            Responses faliureResponse = new Responses(500);
            faliureResponse.setMyException(e);
            return faliureResponse;
        }
    }

    //can also check if game exists here... nonexistant game is also forbidden... for now...
    private boolean amIInPosition(String name, DataAccesser myDatabase, ChessGame.TeamColor team, int gameID){
        try{
            if(myDatabase.locateGameID(gameID)) {
                //check for players
                if(team != null) {
                    if (myDatabase.colorExists(team.toString(), gameID)) {
                        if (!myDatabase.colorNotTaken(team.toString(), gameID)) {
                            GameData theGame = myDatabase.getGame(gameID);
                            //for now I hardcode this... update if more teams emerge...
                            if(team.toString().equals("WHITE")){
                                if(theGame.whiteUsername().equals(name)){
                                    return true;
                                } else {
                                    //wrong person
                                    return false;
                                }
                            } else if(team.toString().equals("BLACK")){
                                if(theGame.blackUsername().equals(name)){
                                    return true;
                                } else {
                                    //wrong person
                                    return false;
                                }
                                //this should not be an error, but until we add more team colors and/or fix this... it is
                            } else {
                                //not a color somehow... fix if I got here because it was a color somehow...
                                return false;
                            }

                        } else {
                            //unclaimed position cannot be mine...
                            return false;
                        }
                    } else {
                        //nonexistent color cannot be claimed.
                        return false;
                    }
                        //check for observers...
                } else {
                //TODO:
                }
            } else {
                //no one is in nonexistent position...
                return false;
            }
        }
    }

}

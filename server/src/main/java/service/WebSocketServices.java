package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.DataAccesser;

public class WebSocketServices {

    public String getName(String auth, DataAccesser myDatabase) throws DataAccessException {
        return myDatabase.usernameFinder(auth);
    }

    public boolean removeName(String auth, DataAccesser myDatabase, ChessGame.TeamColor team){

    }

}

package service;

import chess.ChessGame;
import dataAccess.DataBaseAccess;
import model.AuthData;
import model.Data;
import model.GameData;
import model.UserData;

public class GameService {
    public DataBaseAccess myDataBaseAcess;

    //we will need to check (over and over again) if we are authorized to access anything
    public void verify(AuthData authToken){
        if(authToken.authToken() == null){
            throw new Exception("unathorized");
        } else{
            Data userCheck = myDataBaseAcess.Select(authToken.authToken(), "Authentication");
            if(userCheck.getMyAuthData() == null){
                throw userCheck.getMyException();
            } else if(userCheck.getMyAuthData().authToken() == null){
                throw new Exception("unathorized");
            } else {
                //we are authorized
                return;
            }
        }
    }

    //return all games that are currently in the database.
    public GameData[] listGames(AuthData authToken){
        //This line, BY ITSELF, should be able to verify if I can look at this data
        verify(authToken);
        //now we need to get all games returned
        Data[] allGames = myDataBaseAcess.SelectALL("Games");
        //transfer them
        if(allGames != null){
            GameData[] allgames = new GameData[allGames.length];
            int currentGameLocation = 0;
            for (Data game: allGames) {
                allgames[currentGameLocation] = game.getMyGameData();
                currentGameLocation++;
            }
            return allgames;
        } else {
            //may need to throw an error above for this to work properly...
            return null;
        }
    }

    //create a new game...
    public GameData createGame(AuthData authToken, String name){
        //This line, BY ITSELF, should be able to verify if I can look at this data
        verify(authToken);
        //Now I need to make the new game
        Data game = myDataBaseAcess.Generate(name, "Games");
        if (game.getMyGameData() == null) {
            throw game.getMyException();
        } else {
            //this should return all the correct values for the game
            return game.getMyGameData();
        }
    }

    //Finally join a game!
    public GameData joinGame(AuthData authToken, String gameID){
        //This line, BY ITSELF, should be able to verify if I can look at this data
        verify(authToken);
        //now I need to make sure that the game asked for exists.
        Data test = myDataBaseAcess.Select(gameID, "Games");
        if(test == null){
            return new Exception("Game does not exist");
        } else if(test.getMyGameData() == null) {
            //we can fiqure out exactly what went wrong...
            return test.getMyException();
        } else {
            //We must have the data we need, so we can return it.
            return test.getMyGameData();
        }
    }




}

package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.DataAccesser;
import model.AuthData;
import model.Data;
import model.GameData;
import model.Response;

public class GameServices {
    //return all games that are currently in the database.
    public Response listGames(AuthData authToken, DataAccesser myDatabase){
        //This line, BY ITSELF, should be able to verify if I can look at this data
        if(myDatabase.checkAuthorization(authToken)){
            Response faliureResponse = new Response(401);
            faliureResponse.setMyException(new DataAccessException("Error: unauthorized"));
            return faliureResponse;
        }
        //If we are still here then we are authorized...
        //now we need to get all games returned
        GameData[] allGames = myDatabase.listGames();
        //If there are no games, send back what was asked for... nothing...
        if(allGames == null){
            Response emptyResponse = new Response(200);
            return emptyResponse;
        }
        //otherwise transfer the games...
        Response sucessResponse = new Response(200);
        sucessResponse.setAllGames(allGames);
        return sucessResponse;
    }

    //create a new game...
    public Response createGame(AuthData authToken, String name, DataAccesser myDatabase){
        //This line, BY ITSELF, should be able to verify if I can look at this data
        if(myDatabase.checkAuthorization(authToken)){
            Response faliureResponse = new Response(401);
            faliureResponse.setMyException(new DataAccessException("Error: unauthorized"));
            return faliureResponse;
        }
        //Now I need to make the new game
        GameData game = myDatabase.createGame(name);
        //and now I need to make the response method...
        Response sucessResponse = new Response(200);
        sucessResponse.setMyGameData(game);
        return sucessResponse;
    }

    //Finally join a game!
    public Response joinGame(AuthData authToken, int gameID, String joinAsColor, DataAccesser myDatabase){
        //This line, BY ITSELF, should be able to verify if I can look at this data
        if(myDatabase.checkAuthorization(authToken)){
            Response faliureResponse = new Response(401);
            faliureResponse.setMyException(new DataAccessException("Error: unauthorized"));
            return faliureResponse;
        }
        //now I need to make sure that the game asked for exists.
        if(!myDatabase.locateGameID(gameID)){
            Response faliureResponse = new Response(400);
            //I think this is the kind of error they want, they may want 500 or something...
            faliureResponse.setMyException(new DataAccessException("Error: bad request"));
            return faliureResponse;
        }
        //Looks like both the game and the AuthToken check out...

        //Now we need to see if the game can be joined as asked...
        if(!myDatabase.colorExists(joinAsColor, gameID)){
            Response faliureResponse = new Response(500);
            //I think this is the kind of error they want, they may want 400 or something...
            faliureResponse.setMyException(new DataAccessException("Error: no such team color!"));
            return faliureResponse;
        }
        //The color exists... is it available?
        if(!myDatabase.colorNotTaken(joinAsColor, gameID)){
            Response faliureResponse = new Response(403);
            //I think this is the kind of error they want, they may want 500 or something...
            faliureResponse.setMyException(new DataAccessException("Error: already taken"));
            return faliureResponse;
        }
        //Apparently it is available... now I should return something...
        GameData updatedGame = myDatabase.updateGame(gameID, joinAsColor, authToken.username());
        Response successResponse = new Response(200);
        successResponse.setMyGameData(updatedGame);
        return successResponse;
    }
}

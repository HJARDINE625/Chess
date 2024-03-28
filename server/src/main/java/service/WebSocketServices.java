package service;

import chess.*;
import dataAccess.DataAccessException;
import dataAccess.DataAccesser;
import dataAccess.DataBaseAccesser;
import model.GameData;
import model.Responses;
import model.UserData;

import java.util.Collection;

public class WebSocketServices {

    //use this to give up...
    public Responses giveUp(String auth, DataAccesser myDatabase, int gameID){
        Responses firstPart = firstFewChecks(auth, myDatabase, gameID);
        if(firstPart.getMyException() != null) {
            //already failed...
            return firstPart;
        } else {
            //unpack
            GameData checkGame = firstPart.getMyGameData();
            ChessGame aGame = checkGame.implementation();
            UserData personOfInterest = firstPart.getMyUserData();
            String name = personOfInterest.username();
            //begin main checks...
            ChessGame.TeamColor myTeam = findPosition(name, myDatabase, gameID);
            GameData newGame;
            if(myTeam == null){
                Responses faliureResponse = new Responses(401);
                //I think this is the kind of error they want, they may want 500 or something...
                faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
                return faliureResponse;
            } else {
                //we need whoever they are to give up...
                aGame.gameOver();
                try {
                    newGame = new GameData(gameID, checkGame.whiteUsername(), checkGame.blackUsername(), checkGame.gameName(), aGame);
                    myDatabase.modifyGameState(gameID, aGame);
                } catch (DataAccessException e) {
                    //something went wrong...
                    Responses faliureResponse = new Responses(500);
                    faliureResponse.setMyException(e);
                    return faliureResponse;
                }
                //if we made it here, everything went well
                Responses success = new Responses(200);
                //we are kind of overstocking here... but it will help...
                success.setMyUserData(new UserData(name, null, null));
                success.setMyGameData(newGame);
                return success;
            }
        }

    }


    private Responses firstFewChecks(String auth, DataAccesser myDatabase, int gameID){
        try {myDatabase.checkAuthorization(auth);} catch (DataAccessException e) {
            Responses faliureResponse = new Responses(401);
            //I think this is the kind of error they want, they may want 500 or something...
            faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
            return faliureResponse;
        }
        try {
            if (!myDatabase.locateGameID(gameID)) {
                Responses faliureResponse = new Responses(400);
                //I think this is the kind of error they want, they may want 500 or something...
                faliureResponse.setMyException(new DataAccessException("Error: bad request"));
                return faliureResponse;
            }
        } catch (DataAccessException e) {
            Responses faliureResponse = new Responses(400);
            //I think this is the kind of error they want, they may want 500 or something...
            faliureResponse.setMyException(new DataAccessException("Error: bad request"));
            return faliureResponse;
        }
        String name = new String();
        try{
            name = getName(auth, myDatabase);
        } catch (DataAccessException e) {
            Responses faliureResponse = new Responses(401);
            //I think this is the kind of error they want, they may want 500 or something...
            faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
            return faliureResponse;
        }
        GameData checkGame = null;
        try {
            checkGame = myDatabase.getGame(gameID);
        } catch (DataAccessException e) {
            Responses faliureResponse = new Responses(401);
            //I think this is the kind of error they want, they may want 500 or something...
            faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
            return faliureResponse;
        }
        ChessGame aGame = checkGame.implementation();
        if(aGame.isGameOver()){
            Responses faliureResponse = new Responses(401);
            //I think this is the kind of error they want, they may want 500 or something...
            faliureResponse.setMyException(new DataAccessException("Error: the game is already over... you cannot do anything now!"));
            return faliureResponse;
        } else {
            //here I pass quite a bit of data back...
            Responses goodData = new Responses(200);
            goodData.setMyGameData(checkGame);
            goodData.setMyUserData(new UserData(name, null, null));
            return goodData;
        }
    }


    //only call after it is confirmed that the move can be made.
    private Responses makeMove(DataAccesser myDatabase, ChessMove move, int gameID, ChessGame game, GameData gameHolder) {
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            Responses faliureResponse = new Responses(401);
            //I think this is the kind of error they want, they may want 500 or something...
            faliureResponse.setMyException(new DataAccessException("Error: not a valid move!"));
            return faliureResponse;
        }
        try {
            myDatabase.modifyGameState(gameID, game);
        } catch (DataAccessException e) {
            Responses faliureResponse = new Responses(500);
            faliureResponse.setMyException(e);
            return faliureResponse;
        }
        //if we made it here that is good news...
        Responses success = new Responses(200);
        success.setMyGameData(new GameData(gameHolder.gameID(), gameHolder.whiteUsername(), gameHolder.blackUsername(), gameHolder.gameName(), game));
        return success;
    }


    public Responses moveMaker(String auth, DataAccesser myDatabase, ChessMove checkMe, int gameID){
//        try {myDatabase.checkAuthorization(auth);} catch (DataAccessException e) {
//            Responses faliureResponse = new Responses(401);
//            //I think this is the kind of error they want, they may want 500 or something...
//            faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
//            return faliureResponse;
//        }
//        try {
//            if (!myDatabase.locateGameID(gameID)) {
//                Responses faliureResponse = new Responses(400);
//                //I think this is the kind of error they want, they may want 500 or something...
//                faliureResponse.setMyException(new DataAccessException("Error: bad request"));
//                return faliureResponse;
//            }
//        } catch (DataAccessException e) {
//            Responses faliureResponse = new Responses(400);
//            //I think this is the kind of error they want, they may want 500 or something...
//            faliureResponse.setMyException(new DataAccessException("Error: bad request"));
//            return faliureResponse;
//        }
//        String name = new String();
//        try{
//            name = getName(auth, myDatabase);
//        } catch (DataAccessException e) {
//            Responses faliureResponse = new Responses(401);
//            //I think this is the kind of error they want, they may want 500 or something...
//            faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
//            return faliureResponse;
//        }
//        GameData checkGame = null;
//        try {
//            checkGame = myDatabase.getGame(gameID);
//        } catch (DataAccessException e) {
//            Responses faliureResponse = new Responses(401);
//            //I think this is the kind of error they want, they may want 500 or something...
//            faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
//            return faliureResponse;
//        }
//        ChessGame aGame = checkGame.implementation();
//        if(aGame.isGameOver()){
//            Responses faliureResponse = new Responses(401);
//            //I think this is the kind of error they want, they may want 500 or something...
//            faliureResponse.setMyException(new DataAccessException("Error: the game is already over... you cannot do anything now!"));
//            return faliureResponse;
//        } else {
        Responses firstPart = firstFewChecks(auth, myDatabase, gameID);
        if(firstPart.getMyException() != null) {
            //already failed...
        return firstPart;
        } else {
            //unpack everything...
            GameData checkGame = firstPart.getMyGameData();
            ChessGame aGame = checkGame.implementation();
            UserData personOfInterest = firstPart.getMyUserData();
            String name = personOfInterest.username();
            //now use it for the final move checks...
            Collection<ChessMove> finalMoves = aGame.validMoves(checkMe.getStartPosition());
            if(finalMoves.isEmpty()){
                Responses faliureResponse = new Responses(401);
                //I think this is the kind of error they want, they may want 500 or something...
                faliureResponse.setMyException(new DataAccessException("Error: not a valid move!"));
                return faliureResponse;
                //now actually check the move...
            } else {
                ChessBoard theBoard = aGame.getBoard();
                ChessPiece pieceInQuestion = theBoard.getPiece(checkMe.getStartPosition());
                if(amIInPosition(name, myDatabase, pieceInQuestion.getTeamColor(), gameID)) {
                    for (ChessMove move : finalMoves) {
                        if (move.equals(checkMe)) {
                                return makeMove(myDatabase, checkMe, gameID, aGame, checkGame);
                        }
                    }
                    Responses faliureResponse = new Responses(401);
                    //I think this is the kind of error they want, they may want 500 or something...
                    faliureResponse.setMyException(new DataAccessException("Error: not a valid move!"));
                    return faliureResponse;
                } else {
                    Responses faliureResponse = new Responses(401);
                    //I think this is the kind of error they want, they may want 500 or something...
                    faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
                    return faliureResponse;
                }
            }
        }

    }

    public String getName(String auth, DataAccesser myDatabase) throws DataAccessException {
        return myDatabase.usernameFinder(auth);
    }

    public Responses removeName(String auth, DataAccesser myDatabase, ChessGame.TeamColor team, int gameID) {
        //just make sure here...
        try {myDatabase.checkAuthorization(auth);} catch (DataAccessException e) {
            Responses faliureResponse = new Responses(401);
            //I think this is the kind of error they want, they may want 500 or something...
            faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
            return faliureResponse;
        }
        try {
            if (!myDatabase.locateGameID(gameID)) {
                Responses faliureResponse = new Responses(400);
                //I think this is the kind of error they want, they may want 500 or something...
                faliureResponse.setMyException(new DataAccessException("Error: bad request"));
                return faliureResponse;
            }
            String name = new String();
            try{
                name = getName(auth, myDatabase);
            }
            catch(DataAccessException e){
                Responses faliureResponse = new Responses(400);
                //I think this is the kind of error they want, they may want 500 or something...
                faliureResponse.setMyException(new DataAccessException("Error: bad request"));
                return faliureResponse;
            }
            //done in lower function...
//            if(team != null) {
//                if (myDatabase.colorExists(team.toString(), gameID)) {
//                    //The exception thrown by the next line should be impossible to actually get if this is only called at appropriate times...
//                    if (!myDatabase.colorNotTaken(team.toString(), gameID)) {
//                       String name = getName(auth, myDatabase);
//                       GameData thisGame = myDatabase.getGame(gameID);
//                       String comparisionName = null;
//                       //hard coded... for now...
//                       if(team.toString().equals("WHITE")){
//                           comparisionName = thisGame.whiteUsername();
//                       } else if(team.toString().equals("BLACK")){
//                           comparisionName = thisGame.blackUsername();
//                       }
//                       if(comparisionName == null){
//                           Responses faliureResponse = new Responses(401);
//                           //I think this is the kind of error they want, they may want 500 or something...
//                           faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
//                           return faliureResponse;
//                       } else {
//                           if(name.equals(comparisionName)){
//                               //replace with no one (ie, remove, as we are quiting).
//                               GameData modified = myDatabase.updateGame(null, team.toString(), gameID);
//                               Responses SuccessResponse = new Responses(200);
//                               SuccessResponse.setMyGameData(modified);
//                               return SuccessResponse;
//                           } else {
//                               Responses faliureResponse = new Responses(401);
//                               //I think this is the kind of error they want, they may want 500 or something...
//                               faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
//                               return faliureResponse;
//                           }
//                       }
//                    } else {
//                        Responses faliureResponse = new Responses(400);
//                        //I think this is the kind of error they want, they may want 500 or something...
//                        faliureResponse.setMyException(new DataAccessException("Error: bad request"));
//                        return faliureResponse;
//                    }
//                } else {
//                    //The player is not taken and thus cannot log out...
//                    Responses faliureResponse = new Responses(401);
//                    //I think this is the kind of error they want, they may want 500 or something...
//                    faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
//                    return faliureResponse;
//                }
//            } else{
//                //find the name in the observers list...
//            }
        if(amIInPosition(name, myDatabase, team, gameID)){
            if(name != null) {
                GameData newGame = myDatabase.updateGame(null, team.toString(), gameID);
                Responses SuccessResponse = new Responses(200);
                SuccessResponse.setMyGameData(newGame);
                return SuccessResponse;
            } else {
                if(myDatabase.subtractWatcher(gameID, name)){
                    GameData game = myDatabase.getGame(gameID);
                    Responses SuccessResponse = new Responses(200);
                    SuccessResponse.setMyGameData(game);
                    return SuccessResponse;
                } else {
                    Responses faliureResponse = new Responses(401);
                    //I think this is the kind of error they want, they may want 500 or something...
                    faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
                    return faliureResponse;
                }
            }
        } else {
            Responses faliureResponse = new Responses(401);
            //I think this is the kind of error they want, they may want 500 or something...
            faliureResponse.setMyException(new DataAccessException("Error: Forbidden"));
            return faliureResponse;
        }
        }catch (DataAccessException e){
            Responses faliureResponse = new Responses(500);
            faliureResponse.setMyException(e);
            return faliureResponse;
        }
    }

    //here is another one
    public ChessGame.TeamColor findPosition(String name, DataAccesser myDatabase, int gameID){
//        if(amIInPosition(name, myDatabase, null, gameID)){
//            return null;
//            //hard coded for only two teams right now...
//        }
        if (amIInPosition(name, myDatabase, ChessGame.TeamColor.WHITE, gameID)) {
            return ChessGame.TeamColor.WHITE;
        } else if (amIInPosition(name, myDatabase, ChessGame.TeamColor.WHITE, gameID)){
            return ChessGame.TeamColor.BLACK;
        } else {
            //This may actually not be where they are either... but we will fiqure that out if we call another function or are called by one...
            return null;
        }

    }


    //can also check if game exists here... nonexistant game is also forbidden... for now... do not call until checking that name != null (by finding it in other function).
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
                  String[] observers = myDatabase.getWatchers(gameID);
                    for (String observer: observers) {
                        //found them
                        if(name.equals(observer)){
                            return true;
                        }
                    }
                    //we did not find them in the whole list
                    return false;
                }
            } else {
                //no one is in nonexistent position...
                return false;
            }
        } catch (DataAccessException e) {
            //something needed does not even exist!
            return false;
        }
    }

}

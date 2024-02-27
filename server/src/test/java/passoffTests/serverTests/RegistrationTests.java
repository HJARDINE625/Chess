package passoffTests.serverTests;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.DataAccesser;
import model.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RegistrationTests {



        //We will use this to test that our insert method is working and failing in the right ways

        private DataAccesser myDataStorage = new DataAccess();

        private UserData Kevin;

        private RegistrationServices myRegister = new RegistrationServices();

        private ContolServices ender = new ContolServices();

        private GameServices myGameService = new GameServices();

        private UserData Clue;

        private AuthData loginToken;

        private ChessGame newChessGame = new ChessGame();

        private int ChessGameID;

        private String ChessGameName = "New";


        @BeforeEach
        public void setUp() throws DataAccessException {
            // Here we can set up any classes or variables we will need for each test

            // Here is a real user
            Kevin = new UserData("Flynn", "annoynmus", "Isolated.Grid/sector4.net");

            //Create a new person so we can compare them with the user.
            Clue = new UserData(null, null, null);

        }

        @AfterEach
        public void tearDown() {
            //we do not need to do anything, here for now...
        }

        @Test
        public void loginFail() throws DataAccessException {
            // Let's use a login method to find someone we have not entered.
            Responses authDataHolder = myRegister.login(Kevin.username(), Kevin.password(), myDataStorage);
            // First lets see if our find method found anything at all. If it did not then we know that we got
            // only an error back from our database.
            assertNull(authDataHolder.getMyAuthData());
            assertNotNull(authDataHolder.getMyException());
            // Now lets make sure that Kevin does exist, just not in the database.
            assertNotNull(Kevin);
        }
        @Test
        public void logoutFail() throws DataAccessException {
            //check that we cannot log out, before we login...
            Responses authDataLessHolder = myRegister.logout(loginToken, myDataStorage);
            //With a non-existant login and no one currently logged in, this should lead to an error
            assertNull(authDataLessHolder.getMyAuthData());
            assertNotNull(authDataLessHolder.getMyException());
            assertEquals(authDataLessHolder.getNumericalCode(), 401);
        }
        @Test
        public void insertPass() throws DataAccessException {
            // Start by inserting a user into the grid database.
            myRegister.register(Kevin, myDataStorage);
            // Let's try to login now (if we cannot we did not insert anything
            Responses authDataHolder = myRegister.login(Kevin.username(), Kevin.password(), myDataStorage);
            // First lets see if our find method found anything at all. If it did then we know that we got
            // something back from our database.
            assertNotNull(authDataHolder.getMyAuthData());
            assertNull(authDataHolder.getMyException());
        }

        @Test
        public void loginPass() throws DataAccessException {
            // Let's use a find method to get the user that we previously put in back out.
            loginToken = myRegister.login(Kevin.username(), Kevin.password(), myDataStorage).getMyAuthData();
            // First lets see if our find method found anything at all. If it did then we know that we got
            // something back from our database.
            assertNotNull(loginToken);
            //now let us makesure that another login will not make the same login token
            AuthData compareTest = myRegister.login(Kevin.username(), Kevin.password(), myDataStorage).getMyAuthData();
            //did we get a login?
            assertNotNull(compareTest);
            //Is it the same?
            assertNotEquals(compareTest, loginToken);
        }
        //now let us add in the game tests

    //We no longer use the subfunction verify in this implementation, so these are unnecessary...
//        @Test
//        public void verifyPass() throws DataAccessException {
//            // Lets make sure verify has been working this whole time
//            assertDoesNotThrow(Throwable, myGameService.verify(loginToken));
//        }
//        //to continue
//        @Test
//        public void verifyFail() throws DataAccessException {
//            // Lets make sure verify has been working this whole time
//            //a null token should never work!
//            assertThrows(Throwable, myGameService.verify(null));
//        }
        //here are the more straightforward tests
        @Test
        public void addGamePass() throws DataAccessException {
            // Lets try to have a new Game added
            myGameService.createGame(loginToken, ChessGameName, myDataStorage);
            // Let's make sure that there is a game now
            Responses gameList = myGameService.listGames(loginToken, myDataStorage);
            GameData myGameTest = gameList.getAllGames()[0];
            assertNotNull(myGameTest);
            //now let us make sure it is the right one
            assertEquals(myGameTest.gameName(), ChessGameName);
            //ok let us save the ID for later
            ChessGameID = myGameTest.gameID();
        }

        @Test
        public void listGamePass() throws DataAccessException {
            // Lets check if we can list games again and get the same output
            Responses gameList = myGameService.listGames(loginToken, myDataStorage);
            GameData myGameTest = gameList.getAllGames()[0];
            assertNotNull(myGameTest);
            //now let us make sure it is the right one
            assertEquals(myGameTest.gameName(), ChessGameName);
            //ok let us make sure the ID is still the same
            assertEquals(ChessGameID, myGameTest.gameID());
            //Now make sure the response did not have an error token and gave 200
            assertNull(gameList.getMyException());
            assertEquals(gameList.getNumericalCode(), 200);
        }

        @Test
        public void joinGameFail() throws DataAccessException {
            // Lets join a game as a fake color now
            Responses nonGame = myGameService.joinGame(loginToken, ChessGameID, "siliver", myDataStorage);
            //Make sure we did not get any game and that there was no ok code.
            assertNull(nonGame.getMyGameData());
            assertNotEquals(200, nonGame.getNumericalCode());
        }

        @Test
        public void joinGamePass() throws DataAccessException {
            // Lets join the real game now
            Responses game = myGameService.joinGame(loginToken,ChessGameID, null, myDataStorage);
            assertNotNull(game.getMyGameData());
            assertEquals(200, game.getNumericalCode());
            //now let us make sure it is the right one
            GameData myGameTest = game.getMyGameData();
            assertEquals(myGameTest.gameName(), ChessGameName);
            //ok let us make sure the ID is still the same
            assertEquals(ChessGameID, myGameTest.gameID());
        }

        @Test
        public void logoutPass() throws DataAccessException {
            //check that Kevin is still logged in
            assertNotNull(loginToken);
            //now that we have logged in Kevin, lets log him out
            Responses gettingOut = myRegister.logout(loginToken, myDataStorage);
            //did we get out
            assertEquals(gettingOut.getNumericalCode(), 200);
            //now make sure that he has logged out
            //I may need to ask for something from login token instead to make sure this works
            Responses alreadyOut = myRegister.logout(loginToken, myDataStorage);
            //make sure they are different...
            assertNotEquals(gettingOut, alreadyOut);
            assertNotNull(alreadyOut.getMyException());
        }

        @Test
        public void addGameFail() throws DataAccessException {
            // Lets try to have a new Game added with a useless login token
            Responses cannotMakeWithoutAuth = myGameService.createGame(loginToken, ChessGameName, myDataStorage);
            //Let us make sure it did not work
            assertNull(cannotMakeWithoutAuth.getMyGameData());
            assertNotNull(cannotMakeWithoutAuth.getMyException());
            assertNotEquals(200, cannotMakeWithoutAuth.getNumericalCode());

        }

        @Test
        public void listGameFail() throws DataAccessException {
            // Lets check if we can list games with a null token this time
            //This should never work!!!
            Responses cannotSeeWithoutAuth = myGameService.listGames(null, myDataStorage);
            //Let us make sure it did not work
            assertNull(cannotSeeWithoutAuth.getAllGames());
            assertNotNull(cannotSeeWithoutAuth.getMyException());
            assertNotEquals(200, cannotSeeWithoutAuth.getNumericalCode());
        }

        @Test
        public void destroyNothing() throws DataAccessException {
            Responses nonExistantEmptyDatabase = ender.DeleteALL(new DataAccess());
            //Let us make sure that the delete of nothing worked... for the useless database...
            assertEquals(nonExistantEmptyDatabase.getNumericalCode(), 200);
            assertNull(nonExistantEmptyDatabase.getMyException());
            //We deleted an empty database, so this should still exist...
            assertTrue(myDataStorage.locateUsername(Kevin.username()));
        }

        @Test
        public void destroy() throws DataAccessException {
            //check that we can delete everything.
            Responses emptyDatabase = ender.DeleteALL(myDataStorage);
            assertEquals(emptyDatabase.getNumericalCode(), 200);
            assertFalse(myDataStorage.locateUsername(Kevin.username()));
            assertFalse(myDataStorage.locateGameID(ChessGameID));
            assertFalse(myDataStorage.checkAuthorization(loginToken));
        }
    }



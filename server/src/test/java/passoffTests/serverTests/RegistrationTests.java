package passoffTests.serverTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import model.UserData;
import model.AuthData;
import model.GameData;
import model.Data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ControlService;
import service.GameService;
import service.RegistrationService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RegistrationTests {



        //We will use this to test that our insert method is working and failing in the right ways

        private UserData Kevin;

        private RegistrationService myRegister = new RegistrationService();

        private ControlService ender = new ControlService();

        private GameService myGameService = new GameService();

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
            AuthData compareTest = myRegister.login(Kevin);
            // First lets see if our find method found anything at all. If it did not then we know that we got
            // nothing back from our database.
            assertNull(compareTest);
            // Now lets make sure that Kevin does exist, just not in the database.
            assertNotEquals(Kevin, compareTest);
        }
        @Test
        public void insertPass() throws DataAccessException {
            // Start by inserting a user into the grid database.
            myRegister.register(Kevin);
            // Let's try to login now (if we cannot we did not insert anything
            AuthData compareTest = myRegister.login(Kevin);
            // First lets see if our find method found anything at all. If it did then we know that we got
            // something back from our database.
            assertNotNull(compareTest);
        }

        @Test
        public void loginPass() throws DataAccessException {
            // Let's use a find method to get the user that we previously put in back out.
            loginToken = myRegister.login(Kevin);
            // First lets see if our find method found anything at all. If it did then we know that we got
            // something back from our database.
            assertNotNull(loginToken);
            //now let us makesure that another login will not make the same login token
            AuthData compareTest = myRegister.login(Kevin);
            //did we get a login?
            assertNotNull(compareTest);
            //Is it the same?
            assertNotEquals(compareTest, loginToken);
        }
        //now let us add in the game tests

        @Test
        public void verifyPass() throws DataAccessException {
            // Lets make sure verify has been working this whole time
            assertDoesNotThrow(Throwable, myGameService.verify(loginToken));
        }
        //to continue
        @Test
        public void verifyFail() throws DataAccessException {
            // Lets make sure verify has been working this whole time
            //a null token should never work!
            assertThrows(Throwable, myGameService.verify(null));
        }
        //here are the more straightforward tests
        @Test
        public void addGamePass() throws DataAccessException {
            // Lets try to have a new Game added
            myGameService.createGame(loginToken, ChessGameName);
            // Let's make sure that there is a game now
            GameData myGameTest = myGameService.listGames(loginToken)[0];
            assertNotNull(myGameTest);
            //now let us make sure it is the right one
            assertEquals(myGameTest.gameName(), ChessGameName);
            //ok let us save the ID for later
            ChessGameID = myGameTest.gameID();
        }

        @Test
        public void listGamePass() throws DataAccessException {
            // Lets check if we can list games again and get the same output
            GameData myGameTest = myGameService.listGames(loginToken)[0];
            assertNotNull(myGameTest);
            //now let us make sure it is the right one
            assertEquals(myGameTest.gameName(), ChessGameName);
            //ok let us make sure the ID is still the same
            assertEquals(ChessGameID, myGameTest.gameID());
        }

        @Test
        public void joinGameFail() throws DataAccessException {
            // Lets join a fake game now
            GameData myGameTest = myGameService.joinGame(loginToken,"siliver");
            assertNull(myGameTest);
        }

        @Test
        public void joinGamePass() throws DataAccessException {
            // Lets join the real game now
            GameData myGameTest = myGameService.joinGame(loginToken,ChessGameID);
            assertNotNull(myGameTest);
            //now let us make sure it is the right one
            assertEquals(myGameTest.gameName(), ChessGameName);
            //ok let us make sure the ID is still the same
            assertEquals(ChessGameID, myGameTest.gameID());
        }

        @Test
        public void logoutPass() throws DataAccessException {
            //check that Kevin is still logged in
            assertNotNull(loginToken);
            //now that we have logged in Kevin, lets log him out
            myRegister.logout(loginToken);
            //now make sure that he has logged out
            //I may need to ask for something from login token instead to make sure this works
            assertNull(loginToken);
        }

        @Test
        public void addGameFail() throws DataAccessException {
            // Lets try to have a new Game added with a useless login token
            assertThrows(Throwable, myGameService.createGame(loginToken, ChessGameName));

        }

        @Test
        public void listGameFail() throws DataAccessException {
            // Lets check if we can list games with a null token this time
            //This should never work!!!
            assertThrows(Throwable, myGameService.listGames(null, ChessGameName));

        }

        @Test
        public void logoutFail() throws DataAccessException {
            //check that we throw an error if we logout twice
            assertThrows(DataAccessException, myRegister.logout(loginToken));
        }

        @Test
        public void destroy() throws DataAccessException {
            //check that we can delete everything.
            assertDoesNotThrow(Throwable, ender.DeleteALL());
        }
    }



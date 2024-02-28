package passoffTests.serverTests;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.DataAccesser;
import model.*;

import org.junit.jupiter.api.*;
import service.*;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class DAOUnitTests {
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

    private String otherGame;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Here we can set up any classes or variables we will need for each test

        // Here is a real user
        Kevin = new UserData("Flynn", "annoynmus", "Isolated.Grid/sector4.net");

        //Create a new person so we can compare them with the user.
        Clue = new UserData("null", "annoynmues", "Isolated.Grid/sector1.net");

        //Create a new game name
        otherGame = "Battle of the Century";


    }
    //Here are the methods I still need to implement positives and negative for (these are actually easier... as they are the code that checks for errors in inputs with booleans, so they have clear fail states).
    public boolean deleteAuth(String authenticator);
    public boolean checkAuthorization(String authenticator);
    public boolean colorExists(String color, int gameID);
    public boolean colorNotTaken(String color, int gameID);
    public boolean locateUsername(String username);
    public boolean locateGameID(int gameID);

    @AfterEach
    public void tearDown() {
        ender.DeleteALL(myDataStorage);
        //we do not need to do anything, here for now...
    }
    @Order(1)
    @Test
    public void getUserFail() throws DataAccessException {
        // Let's use a login method to find someone we have not entered.
        Responses authDataHolder = myRegister.login(Kevin.username(), Kevin.password(), myDataStorage);
        // First lets see if our find method found anything at all. If it did not then we know that we got
        // only an error back from our database.
        assertNull(authDataHolder.getMyAuthData());
        assertNotNull(authDataHolder.getMyException());
        // Now lets make sure that Kevin does exist, just not in the database.
        assertNotNull(Kevin);
        //now we should not be able to find him this way
        assertNull(myDataStorage.getUser(Kevin.username(), Kevin.password()));
    }

    @Order(2)
    @Test
    public void getUserPass() throws DataAccessException {
        // Let's use a login method to find someone we have not entered.
        Responses authDataHolder = myRegister.register(Kevin, myDataStorage);
        // First lets see if our find method found anything at all. If it did not then we know that we got
        // no error back
        assertNotNull(authDataHolder.getMyAuthData());
        assertNull(authDataHolder.getMyException());

        //now we should be able to find him this way
        assertNotNull(myDataStorage.getUser(Kevin.username(), Kevin.password()));
        assertEquals(myDataStorage.getUser(Kevin.username(), Kevin.password()), Kevin);
    }
    @Order(3)
    @Test
    public void createUserPass() throws DataAccessException {
        //Lets create a new user
        myDataStorage.createUser(Kevin.username(), Kevin.password(), Kevin.email());
        //now lets make sure we can find him
        assertNotNull(myDataStorage.getUser(Kevin.username(), Kevin.password()));
        assertEquals(myDataStorage.getUser(Kevin.username(), Kevin.password()), Kevin);
    }

    //This error is more of an error in getting a user after a different one is created, but most of the actual logic
    //for when to call these functions is elsewhere, so I really was not sure what to put for the negative
    //test on many of them...
    @Order(4)
    @Test
    public void createUserFail() throws DataAccessException {
        //Lets create a new user
        myDataStorage.createUser(Kevin.username(), Kevin.password(), Kevin.email());
        //now lets make sure we can find him
        assertNotNull(myDataStorage.getUser(Kevin.username(), Kevin.password()));
        assertEquals(myDataStorage.getUser(Kevin.username(), Kevin.password()), Kevin);
        //now lets make sure we cannot find the other user
        assertNull(myDataStorage.getUser(Clue.username(), Clue.password()));
        //We cannot find a user we did not properly create.
    }
    @Order(5)
    @Test
    public void createAuthPass() throws DataAccessException {
        //Lets create a new user
        myDataStorage.createUser(Kevin.username(), Kevin.password(), Kevin.email());
        //now lets make sure we can find him
        assertNotNull(myDataStorage.getUser(Kevin.username(), Kevin.password()));
        assertEquals(myDataStorage.getUser(Kevin.username(), Kevin.password()), Kevin);
        //now let us get an authToken for him
        AuthData myResult = myDataStorage.createAuth(Kevin.username());
        //Check that it exists
        assertNotNull(myResult);
        assertNotNull(myResult.authToken());
        assertNotNull(myResult.username());
    }

    //In practice passing these values into the function should not be possible, but I suspect that it would lead to an
    //exception if java is working the way I am thinking it is.
    @Order(6)
    @Test
    public void createAuthFail() throws DataAccessException {
        //Lets create a false new user
        myDataStorage.createUser(null, null, null);
        //now let us get an authToken for him
        boolean weGotAwayWithNonsense = true
        try {
            AuthData myResult = myDataStorage.createAuth(Kevin.username());
        } catch (Exception e) {
            //This should work, provided that the database properly clears at the end of this
            weGotAwayWithNonsense = false;
        }
        //Check that it threw an error meaning that this query does not work...
        assertFalse(weGotAwayWithNonsense);
    }

    //This will just check that a game created with no name has no name (a random error that should never come up).
    @Order(7)
    @Test
    public void createGameFail() throws DataAccessException {
        //Lets create a false new game
        GameData myNonsenseGame = myDataStorage.createGame(null);
        //Now check that it has a null name (which is a mistake here).
        assertNull(myNonsenseGame.gameName());
    }

    @Order(8)
    @Test
    public void createGamePass() throws DataAccessException {
        //Lets create a false new game
        GameData theTrueGame = myDataStorage.createGame(otherGame);
        //Now check that it has a null name (which is a mistake here).
        assertNotNull(theTrueGame.gameName());
        assertNotNull(theTrueGame.gameID());
        //Now make sure we can find it
        GameData returnOfTheTrueGame = myDataStorage.getGame(theTrueGame.gameID());
        assertNotNull(returnOfTheTrueGame);
        //Is it the same game?
        assertEquals(theTrueGame, returnOfTheTrueGame);
    }

   //This still works just fine, but here we will make sure that nothing is returned when nothing is in the list...
    //you could argue that is a failure from a usage/design standpoint.
    @Order(9)
    @Test
    public void ListGameFail() throws DataAccessException {
        //Lets list all the games
        GameData[] mySoCalledGames = myDataStorage.listGames();
        //there should be none, preventing the for loop from running.
        boolean thereIsNoGame = true;
        for (GameData d: mySoCalledGames) {
            thereIsNoGame = false;
        }
        //If there are no games, we successfully failed to use our database to the fullest.
        assertTrue(thereIsNoGame);
    }

    @Order(10)
    @Test
    public void ListGamePass() throws DataAccessException {
        //Lets create a false new game
        GameData theTrueGame = myDataStorage.createGame(otherGame);
        //Now check that it has a null name (which is a mistake here).
        assertNotNull(theTrueGame.gameName());
        assertNotNull(theTrueGame.gameID());
        //Now we can add another game
        GameData someGame = myDataStorage.createGame(ChessGameName);
        //did it work?
        assertNotNull(someGame);
        //now get all the games back
        GameData[] allTheGamesThatMatter = myDataStorage.listGames();
        //check that they exist
        assertNotNull(allTheGamesThatMatter);
        assertNotNull(allTheGamesThatMatter[0]);
        assertNotNull(allTheGamesThatMatter[1]);
    }

    //the faliure here is that the user will have a "" string as their name... because they do not exist
    @Order(11)
    @Test
    public void updateGameFail() throws DataAccessException {
        //Lets insert the greatest game
        GameData theGreatGame = myDataStorage.createGame(otherGame);
        //check that it exists
        assertNotNull(theGreatGame);
        //now Lets join it... as no one real!
        GameData tragedy = myDataStorage.updateGame(theGreatGame.gameID(), "BLACK", "LetMEINNOW!!!");
        //make sure that the username was arbitrary set to a useless one that is not a login...
        assertEquals("", tragedy.blackUsername());
    }

    @Order(12)
    @Test
    public void updateGamePass() throws DataAccessException {
        //Lets add the good game
        GameData theGreatGame = myDataStorage.createGame(otherGame);
        assertNotNull(theGreatGame);
        //now get Flynn and Clue in
        myDataStorage.createUser(Kevin.username(), Kevin.password(), Kevin.email());
        myDataStorage.createUser(Clue.username(), Clue.password(), Clue.email());
        //Get logins for both of them
        AuthData playerOne = myDataStorage.createAuth(Kevin.username());
        AuthData playerTwo = myDataStorage.createAuth(Clue.username());
        //now update the game with them
        myDataStorage.updateGame(theGreatGame.gameID(), "WHITE", playerOne.authToken());
        myDataStorage.updateGame(theGreatGame.gameID(), "BLACK", playerTwo.authToken());
        //now see if the new game is properly set up
        GameData theWarAsItStands = myDataStorage.getGame(theGreatGame.gameID());
        //specifically check if the players have been added
        assertNotNull(theWarAsItStands);
        assertNotEquals(theGreatGame, theWarAsItStands);
        assertEquals(theWarAsItStands.blackUsername(), Clue.username());
        assertEquals(theWarAsItStands.whiteUsername(), Kevin.username());

    }

    @Order(13)
    @Test
    public void getGameFail() throws DataAccessException {
        //Lets insert the greatest game
        GameData theGreatGame = myDataStorage.createGame(otherGame);
        //now Lets delete everything in the database
        myDataStorage.clear();
        //now we can add a less cool game
        myDataStorage.createGame(ChessGameName);
        //lets see if we can find the game we deleted.
        assertNull(myDataStorage.getGame(theGreatGame.gameID()));

    }

    @Order(14)
    @Test
    public void getGamePass() throws DataAccessException {
        //Lets add the good game
        GameData theGreatGame = myDataStorage.createGame(otherGame);
        //now we can add the other game
        GameData foolsQuest = myDataStorage.createGame(ChessGameName);
        //make sure the games are different to prove the next test works.
        assertNotEquals(theGreatGame, foolsQuest);
        //lets see if we find the right ones
        assertEquals(foolsQuest, myDataStorage.getGame(foolsQuest.gameID()));
        assertEquals(theGreatGame, myDataStorage.getGame(theGreatGame.gameID()));
    }






    @Order(23)
    @Test
    public void failDestroy() throws DataAccessException {
        // Start by inserting a user into the grid database.
        myRegister.register(Kevin, myDataStorage);
        // Let's use a find method to get the user that we previously put in back out.
        loginToken = myRegister.login(Kevin.username(), Kevin.password(), myDataStorage).getMyAuthData();
        // Lets try to have a new Game added
        myGameService.createGame(loginToken.authToken(), ChessGameName, myDataStorage);
        //now we update the ID
        Responses list = myGameService.listGames(loginToken.authToken(), myDataStorage);
        GameData test = list.getAllGames()[0];
        ChessGameID = test.gameID();
        //check that we can delete everything.
        DataAccess myAccesser = new DataAccess();
        //but delete it from the wrong place.
        myAccesser.clear();

        //If we still can find anything, then it worked.
        assertTrue(myDataStorage.locateUsername(Kevin.username()));
        assertTrue(myDataStorage.locateGameID(ChessGameID));
        assertTrue(myDataStorage.checkAuthorization(loginToken.authToken()));
    }
    @Order(24)
    @Test
    public void destroy() throws DataAccessException {
        // Start by inserting a user into the grid database.
        myRegister.register(Kevin, myDataStorage);
        // Let's use a find method to get the user that we previously put in back out.
        loginToken = myRegister.login(Kevin.username(), Kevin.password(), myDataStorage).getMyAuthData();
        // Lets try to have a new Game added
        myGameService.createGame(loginToken.authToken(), ChessGameName, myDataStorage);
        //now we update the ID
        Responses list = myGameService.listGames(loginToken.authToken(), myDataStorage);
        GameData test = list.getAllGames()[0];
        ChessGameID = test.gameID();
        //check that we can delete everything.
        myDataStorage.clear();

        //If we cannot find anything, then it worked.
        assertFalse(myDataStorage.locateUsername(Kevin.username()));
        assertFalse(myDataStorage.locateGameID(ChessGameID));
        assertFalse(myDataStorage.checkAuthorization(loginToken.authToken()));
    }
}

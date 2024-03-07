package passoffTests.serverTests;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.DataAccesser;
import dataAccess.DataBaseAccesser;
import model.*;

import org.junit.jupiter.api.*;
import service.*;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class DAOUnitTests {
    private DataAccesser myDataStorage = new DataBaseAccesser();

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

    private int antiGameID = -1111;

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

    //None!

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
        //Lets create a new user
        UserData user = myDataStorage.createUser(Kevin.username(), Kevin.password(), Kevin.email());
        //now let us get an authToken for someone else
        AuthData hack = myDataStorage.createAuth(Clue.username());
        Responses itShouldExistTo = myRegister.login(Clue.username(), Clue.password(), myDataStorage);
        //we get the first auth, but not the second...
        assertNotNull(hack);
        assertNull(itShouldExistTo.getMyAuthData());
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
        if(mySoCalledGames != null) {
            if (mySoCalledGames.length != 0) {
                    thereIsNoGame = false;
            }
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

    //These functions are much easier to check, because they are meant to return a boolean signifying that they worked.
    //ie: they are the actual functions called to test for errors before the others are called.
    @Order(15)
    @Test
    public void checkAuthorizationPass() throws DataAccessException {
        //create our user and log him in
        myDataStorage.createUser(Kevin.username(), Kevin.password(), Kevin.email());
        AuthData user = myDataStorage.createAuth(Kevin.username());
        //now see if he exists
        assertNotNull(user);
        //because he exists we should be able to find him...
        assertTrue(myDataStorage.checkAuthorization(user.authToken()));
    }

    @Order(16)
    @Test
    public void deleteAuthFail() throws DataAccessException {
        //create our user and log him in
        myDataStorage.createUser(Kevin.username(), Kevin.password(), Kevin.email());
        AuthData user = myDataStorage.createAuth(Kevin.username());
        //now see if he exists
        assertNotNull(user);
        //and his authentication
        assertNotNull(user.authToken());
        //Now crash the grid
        myDataStorage.clear();
        //Now see if we can delete an authentication that no longer exists
        myDataStorage.deleteAuth(user.authToken());
    }

    @Order(17)
    @Test
    public void checkAuthorizationFail() throws DataAccessException {
        //create our user but do not log him in (he is actually a program who does not think he needs to).
        UserData program = myDataStorage.createUser(Clue.username(), Clue.password(), Clue.email());
        //let's make sure there is a program in the database and that it is the same as what we put in...
        assertNotNull(program);
        assertEquals(program, Clue);
        //let's check his authorization using the data he gave
        assertFalse(myDataStorage.checkAuthorization(Clue.username()));
        assertFalse(myDataStorage.checkAuthorization(Clue.password()));
        assertFalse(myDataStorage.checkAuthorization(Clue.email()));
    }

    @Order(18)
    @Test
    public void deleteAuthPass() throws DataAccessException {
        //well this guy is a faker, so we will log him in, but then expel him.
        UserData program = myDataStorage.createUser(Clue.username(), Clue.password(), Clue.email());
        AuthData soCalledUser = myDataStorage.createAuth(Clue.username());
        //let's make sure there is a program in the database and that it is the same as what we put in...
        assertNotNull(program);
        assertEquals(program, Clue);
        //also check the AuthData
        assertNotNull(soCalledUser);
        assertNotNull(soCalledUser.authToken());
        assertNotNull(soCalledUser.username());
        //alright, he is in, kick him out and check it worked
        assertTrue(myDataStorage.deleteAuth(soCalledUser.authToken()));
        //we should not be able to find him now
        assertFalse(myDataStorage.checkAuthorization(soCalledUser.authToken()));
    }

    @Order(19)
    @Test
    public void locateUsernamePass() throws DataAccessException {
        //get a new user...
        UserData user = myDataStorage.createUser(Kevin.username(), Kevin.password(), Kevin.email());
        //let's make sure there is a user in the database and that it is the same as what we put in...
        assertNotNull(user);
        assertEquals(user, Kevin);
        //now lets see if we can find him
        assertTrue(myDataStorage.locateUsername(Kevin.username()));
    }

    @Order(20)
    @Test
    public void locateGameIDFail() throws DataAccessException {
        //fill up our games
        myDataStorage.createGame(otherGame);
        myDataStorage.createGame(ChessGameName);
        //Now lets see if we can find a game from a game id that cannot exist based upon creation rules
        assertFalse(myDataStorage.locateGameID(antiGameID));

    }

    @Order(21)
    @Test
    public void locateGameIDPass() throws DataAccessException {
        //fill up our games
        GameData gameOne = myDataStorage.createGame(otherGame);
        GameData gameTwo = myDataStorage.createGame(ChessGameName);
        //lets make sure we have something
        assertNotNull(gameOne);
        assertNotNull(gameTwo);
        assertNotNull(gameOne.gameID());
        assertNotNull(gameTwo.gameID());
        //make sure they are different (they should be if createGame is working right...)
        assertNotEquals(gameOne, gameTwo);
        assertNotEquals(gameOne.gameID(), gameTwo.gameID());
        //Now lets see if we can find the games based upon their ID's...
        assertEquals(gameOne, myDataStorage.getGame(gameOne.gameID()));
        assertEquals(gameTwo, myDataStorage.getGame(gameTwo.gameID()));
        assertNotEquals(myDataStorage.getGame(gameOne.gameID()), myDataStorage.getGame(gameTwo.gameID()));
    }

    @Order(22)
    @Test
    public void locateUsernameFail() throws DataAccessException {
        //Lets try to find a username in an empty datastorage
        assertFalse(myDataStorage.locateUsername(Clue.username()));
    }

    @Order(23)
    @Test
    public void colorExistsPass() throws DataAccessException {
        //fill up our games
        GameData test = myDataStorage.createGame(otherGame);
        //make sure we have something
        assertNotNull(test);
        assertNotNull(test.gameID());
        //now see if the null color is allowed in the game (it always should be based upon my logic)
        assertTrue(myDataStorage.colorExists(null, test.gameID()));
    }

    @Order(24)
    @Test
    public void colorExistsFail() throws DataAccessException {
        //Let's try to find a color that does not exist in base Chess after setting up a base Chess game... specifically a lowercase color...
        //fill up our games
        GameData test = myDataStorage.createGame(otherGame);
        //make sure we have something
        assertNotNull(test);
        assertNotNull(test.gameID());
        //now see if the lowercase "white" is allowed... it should not be the way I set up the indicators and methods...
        assertFalse(myDataStorage.colorExists("white", test.gameID()));
    }

    @Order(25)
    @Test
    public void colorNotTakenPass() throws DataAccessException {
        //fill up our games
        GameData greatGame = myDataStorage.createGame(otherGame);
        UserData user = myDataStorage.createUser(Kevin.username(), Kevin.password(), Kevin.email());
        UserData program = myDataStorage.createUser(Clue.username(), Clue.password(), Clue.email());
        //make sure we have something
        assertNotNull(greatGame);
        assertNotNull(greatGame.gameID());
        assertNotNull(user);
        assertNotNull(program);
        //the game should be empty (ready for adding players, check that WHITE exists and is not taken yet... as no one has joined...
        assertTrue(myDataStorage.colorExists("WHITE", greatGame.gameID()));
        assertTrue(myDataStorage.colorNotTaken("WHITE", greatGame.gameID()));
        //add Kevin then see if white is still not taken afterwords
        myDataStorage.updateGame(greatGame.gameID(),"WHITE", Kevin.username());
        assertTrue(myDataStorage.colorExists("WHITE", greatGame.gameID()));
        assertFalse(myDataStorage.colorNotTaken("WHITE", greatGame.gameID()));
    }

    @Order(26)
    @Test
    public void colorNotTakenFail() throws DataAccessException {
        //fill up our games and players
        GameData test = myDataStorage.createGame(otherGame);
        //make sure we have something
        assertNotNull(test);
        assertNotNull(test.gameID());
    }



    @Order(27)
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
    @Order(28)
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

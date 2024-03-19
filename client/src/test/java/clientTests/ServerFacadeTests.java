package clientTests;

import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.Responses;
import org.junit.jupiter.api.*;
import server.Server;
import ui.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private String[] strings = new String[3];

    private GameData[] startingGames;

    private int otherNum = 0;

    private int selection = 0;

    private static Server server;
    static LoopGenerator facade;
    private static CreateGame gameCreator = new CreateGame();
    private static JoinGame gameJoiner = new JoinGame();
    private static Login authValueGenerator = new Login();
    private static Logout authValueRemover = new Logout();
    private static GetExample gameInfo = new GetExample();

    private static ServerConnectorTester myDataStorage;

    private InputStream normalSystemInput = System.in; // backup System.in to restore it later
    //Unfortunately I had to look up the basics of how to turn my non-input into an input here it is...



    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new LoopGenerator("localhost", port, false);
        //Now fill up with all the stuff I actually need to run my tests... based on where I put my code.
        myDataStorage = facade.getMyServerTest();
        //I think I can access this another way...
//        gameCreator = myDataStorage.getGameCreator();
//        gameJoiner = myDataStorage.getGameJoiner();
//        authValueGenerator = myDataStorage.getAuthValueGenerator();
//        gameInfo = myDataStorage.getGameInfo();
    }

    @AfterAll
    public static void stopServer() {
        //If I cannot find a way to delete the server from here, I may always have to run a test from elsewhere after this... for now do that...
        server.stop();
    }

    @AfterEach
    @BeforeEach
    public void fixInput(){
        System.setIn(normalSystemInput);
        //set up for new input...
        strings = new String[3];
        otherNum = 0;
        startingGames = myDataStorage.getGames();
    }

    //these are the five functions I need to test (they are the ones that actually link up with the server... they are found in my datastorage...
//    private CreateGame gameCreator = new CreateGame();
//    private JoinGame gameJoiner = new JoinGame();
//    private Login authValueGenerator = new Login();
//    private Logout authValueRemover = new Logout();
//    private GetExample gameInfo = new GetExample();


    @Order(12)
    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Order(10)
    @Test
    public void sanityCheck() throws DataAccessException, IOException {
        //make sure the games are empty or if this is called in order, make sure they are the same length as they were before.
        if(myDataStorage.getGames() != null) {
            assertEquals(myDataStorage.getGames().length, startingGames.length);
            //we are currently in the wrong part of the menu for this test so log out without changing any key strings in the logout function.
            myDataStorage.completeAction(1, 0, strings);
        } else {
            assertNull(startingGames);
        }
        // Let's use a login method to find someone
//        myDataStorage.completeAction(2);
//        ByteArrayInputStream inputFacade = new ByteArrayInputStream("Hi".getBytes());
//        System.setIn(inputFacade);
//        //next
//        inputFacade = new ByteArrayInputStream("YouShallNotPass".getBytes());
//        System.setIn(inputFacade);
//        //finally
//        inputFacade = new ByteArrayInputStream("@exe.com".getBytes());
//        System.setIn(inputFacade);
        strings[0] = "Name";
        strings[1] = "Pass";
        strings[2] = "Email";
        selection = 2;
        myDataStorage.completeAction(selection, otherNum, strings);
        //Get the authToken for comparison
        AuthData myStuff = myDataStorage.GetCurrentAuthentication();
        assertNotNull(myStuff);
        assertNotNull(myStuff.username());
        assertNotNull(myStuff.authToken());
        assertEquals(myStuff.username(), "Name");
        //log out
        //myDataStorage.completeAction(1);
        selection = 1;
        myDataStorage.completeAction(selection, otherNum, strings);

        // Let's use a login method to find someone we have entered.
        //myDataStorage.completeAction(3);
        selection = 3;
        strings[2] = null;
        myDataStorage.completeAction(selection, otherNum, strings);

//
//        inputFacade = new ByteArrayInputStream("Hi".getBytes());
//        System.setIn(inputFacade);
//        //next
//        inputFacade = new ByteArrayInputStream("YouShallNotPass".getBytes());
//        System.setIn(inputFacade);
        // First lets see if our find method found anything at all. If it did not then we know that we got
        //Get some way of proving we found something...
        AuthData newStuff = myDataStorage.GetCurrentAuthentication();
        assertNotNull(newStuff);
        assertNotNull(newStuff.username());
        assertNotNull(newStuff.authToken());
        assertEquals(newStuff.username(), "Name");

        //now make sure the returns are different
        assertNotEquals(newStuff, myStuff);
        assertNotEquals(newStuff.authToken(), myStuff.authToken());
        //now make a game
        strings[0] = "AGame";
        strings[1] = null;
        strings[2] = null;
        selection = 2;
        myDataStorage.completeAction(selection, otherNum, strings);
        //now get game
        selection = 3;
        myDataStorage.completeAction(selection, otherNum, strings);
        //see if it worked now...
        assertNotNull(myDataStorage.getGames());
        //now retrieve it
        selection = 4;
        otherNum = 0;
        strings[0] = "BLACK";
        myDataStorage.completeAction(selection,otherNum, strings);
        //now see what we get here...
        selection = 3;
        myDataStorage.completeAction(selection, otherNum, strings);
        //see if it worked now...
        assertNotNull(myDataStorage.getGames());

//        selection = 1;
//        myDataStorage.completeAction(selection,otherNum, strings);
//        //make sure we logged out for the next test...
//        assertNull(myDataStorage.GetCurrentAuthentication());
    }

    @Order(2)
    @Test
    public void loginPass() throws DataAccessException, IOException {
        // Let's use a login method to find someone
//        myDataStorage.completeAction(2);
//        ByteArrayInputStream inputFacade = new ByteArrayInputStream("Hi".getBytes());
//        System.setIn(inputFacade);
//        //next
//        inputFacade = new ByteArrayInputStream("YouShallNotPass".getBytes());
//        System.setIn(inputFacade);
//        //finally
//        inputFacade = new ByteArrayInputStream("@exe.com".getBytes());
//        System.setIn(inputFacade);
        strings[0] = "Hi";
        strings[1] = "YouShallNotPass";
        strings[2] = "@exe.com";
        selection = 2;
        myDataStorage.completeAction(selection, otherNum, strings);
        //Get the authToken for comparison
        AuthData myStuff = myDataStorage.GetCurrentAuthentication();
        assertNotNull(myStuff);
        assertNotNull(myStuff.username());
        assertNotNull(myStuff.authToken());
        assertEquals(myStuff.username(), "Hi");
        //log out
        //myDataStorage.completeAction(1);
        selection = 1;
        myDataStorage.completeAction(selection, otherNum, strings);

        // Let's use a login method to find someone we have entered.
        //myDataStorage.completeAction(3);
        selection = 3;
        strings[2] = null;
        myDataStorage.completeAction(selection, otherNum, strings);

//
//        inputFacade = new ByteArrayInputStream("Hi".getBytes());
//        System.setIn(inputFacade);
//        //next
//        inputFacade = new ByteArrayInputStream("YouShallNotPass".getBytes());
//        System.setIn(inputFacade);
        // First lets see if our find method found anything at all. If it did not then we know that we got
        //Get some way of proving we found something...
        AuthData newStuff = myDataStorage.GetCurrentAuthentication();
        assertNotNull(newStuff);
        assertNotNull(newStuff.username());
        assertNotNull(newStuff.authToken());
        assertEquals(newStuff.username(), "Hi");

        //now make sure the returns are different
        assertNotEquals(newStuff, myStuff);
        assertNotEquals(newStuff.authToken(), myStuff.authToken());
    }
    @Order(11)
    @Test
    public void logoutFail() throws DataAccessException, IOException {
        //add a new user and login...
        //Logout, make sure it works then logout again...
        //myDataStorage.completeAction(1);
        selection = 1;
        myDataStorage.completeAction(selection, otherNum, strings);
        assertNull(myDataStorage.GetCurrentAuthentication());
        try {
            myDataStorage.completeAction(selection, otherNum, strings);
        } catch (RuntimeException e) {
            //we exited rather than removing a login, good work
            assertTrue(true);
            return;
        }
        //We should not have gotten here
        fail();
    }
    @Order(8)
    @Test
    public void logoutPass() throws DataAccessException, IOException {
        //Logout, make sure it works
        assertNotNull(myDataStorage.GetCurrentAuthentication());
        //myDataStorage.completeAction(1);
        selection = 1;
        myDataStorage.completeAction(selection, otherNum, strings);
        assertNull(myDataStorage.GetCurrentAuthentication());
    }
    //unfortunatly under the new testable system... this should pass but not indicate as much as I want it to by doing so...
    @Order(1)
    @Test
    public void loginFail() throws DataAccessException, IOException {
        //make sure no one is in here...
        AuthData oldStuff = myDataStorage.GetCurrentAuthentication();
        assertNull(oldStuff);

        //login with only a username and password before we have added anyone to the system
//        myDataStorage.completeAction(3);
//
//        ByteArrayInputStream inputFacade = new ByteArrayInputStream("Hi".getBytes());
//        System.setIn(inputFacade);
//        //next
//        inputFacade = new ByteArrayInputStream("YouShallNotPass".getBytes());
//        System.setIn(inputFacade);
        strings[0] = "Hi";
        strings[1] =  "YouShallNotPass";
        selection = 3;
        myDataStorage.completeAction(selection, otherNum, strings);
        // First lets see if our find method found anything at all. If it did not then we know that we got
        //Get some way of proving we found nothing...
        AuthData newStuff = myDataStorage.GetCurrentAuthentication();
        assertNull(newStuff);
    }

    @Order(3)
    @Test
    public void getFail() throws DataAccessException, IOException {
        //make sure we are in the correct part of the test environment
        //myDataStorage.completeAction(0);
        selection = 0;
        myDataStorage.completeAction(selection, otherNum, strings);
        //now make sure there are no games available right now...
        assertNull(myDataStorage.getGames());
        //ok, now we need to get all the non-existent games
        selection = 3;
        myDataStorage.completeAction(selection, otherNum, strings);
        //make sure there are still no games
        assertNull(myDataStorage.getGames());
    }

    @Order(4)
    @Test
    public void addPass() throws DataAccessException, IOException {
        //make sure we are still in the correct part of the test environment
        //myDataStorage.completeAction(0);
        selection = 0;
        myDataStorage.completeAction(selection, otherNum, strings);
        //now make sure there are no games available right now...
        assertNull(myDataStorage.getGames());
        //ok, now we need to get all the non-existent games
        selection = 3;
        myDataStorage.completeAction(selection, otherNum, strings);
        //myDataStorage.completeAction(3);
        //make sure there are still no games
        assertNull(myDataStorage.getGames());
        //now we need to add a new game...
//        myDataStorage.completeAction(4);
//        ByteArrayInputStream inputFacade = new ByteArrayInputStream("RemeberMe".getBytes());
//        System.setIn(inputFacade);
        strings[0] = "RememberMe";
        strings[1] = null;
        strings[2] = null;
        selection = 2;
        myDataStorage.completeAction(selection, otherNum, strings);
        //now lets see if a game exists
        //myDataStorage.completeAction(3);
        selection = 3;
        myDataStorage.completeAction(selection, otherNum, strings);
        assertNotNull(myDataStorage.getGames());
    }

    @Order(5)
    @Test
    public void getPass() throws DataAccessException, IOException {
        //make sure we are still in the correct part of the test environment
        //myDataStorage.completeAction(0);
        selection = 0;
        myDataStorage.completeAction(selection, otherNum, strings);
        //now make sure there are games available right now...
        assertNotNull(myDataStorage.getGames());
        GameData[] oldGames = myDataStorage.getGames();
        //ok, now we need to get all the games again
        //myDataStorage.completeAction(3);
        selection = 3;
        myDataStorage.completeAction(selection, otherNum, strings);
        //make sure there are still games
        assertNotNull(myDataStorage.getGames());
        GameData[] newGames = myDataStorage.getGames();
        //are they the same?
        assertEquals(oldGames.length, newGames.length);
        assertEquals(oldGames[0].gameID(), newGames[0].gameID());
    }

    @Order(6)
    @Test
    public void joinPass() throws DataAccessException, IOException {
        //make sure we are still in the correct part of the test environment
        //myDataStorage.completeAction(0);
        selection = 0;
        myDataStorage.completeAction(selection, otherNum, strings);
        //now make sure there are games available right now...
        assertNotNull(myDataStorage.getGames());
        GameData[] oldGames = myDataStorage.getGames();
        assertNotNull(oldGames[0]);
//        //ok, now we need to modify game zero
//        myDataStorage.completeAction(4);
//        //add ourselves as white
//        ByteArrayInputStream inputFacade = new ByteArrayInputStream("WHITE".getBytes());
//        System.setIn(inputFacade);
        selection = 4;
        strings[0] = "WHITE";
        strings[1] = null;
        strings[2] = null;
        myDataStorage.completeAction(selection, otherNum, strings);
        //ok, let us see if we were added
        //first get new games selection...
        selection = 3;
        myDataStorage.completeAction(selection, otherNum, strings);
        //now check it...
        assertNotNull(myDataStorage.getGames());
        GameData[] newGames = myDataStorage.getGames();
        assertNotNull(newGames[0]);
        assertNotNull(newGames[0].whiteUsername());
        //just a check that we were not already there...
        assertNull(oldGames[0].whiteUsername());
    }

    @Order(9)
    @Test
    public void addFail() throws DataAccessException, IOException {
        //make sure we are still in the correct part of the test environment
        //myDataStorage.completeAction(0);
        selection = 0;
        myDataStorage.completeAction(selection, otherNum, strings);
        assertNull(myDataStorage.GetCurrentAuthentication());
//        //do impossible action from this selection screen
//        myDataStorage.completeAction(4);
//        //now act as though it worked and see if anything happens... it should not...
//        ByteArrayInputStream inputFacade = new ByteArrayInputStream("The Game That Could Not Be".getBytes());
//        System.setIn(inputFacade);
        selection = 2;
        strings[0] = "The Game That Could Not Be";
        myDataStorage.completeAction(selection, otherNum, strings);
        //next
//        inputFacade = new ByteArrayInputStream("0".getBytes());
//        System.setIn(inputFacade);
        selection = 0;
        myDataStorage.completeAction(selection, otherNum, strings);
        //we should have only got a help output here... nothing else...
        //check that we do not have the game that could not be anywhere


        //Create a new user
//        myDataStorage.completeAction(2);
//        inputFacade = new ByteArrayInputStream("inspector".getBytes());
//        System.setIn(inputFacade);
//        //next
//        inputFacade = new ByteArrayInputStream("SayPlease".getBytes());
//        System.setIn(inputFacade);
//        //finally
//        inputFacade = new ByteArrayInputStream("Classified".getBytes());
//        System.setIn(inputFacade);

        selection = 2;
        strings[0] = "inspector";
        strings[1] = "SayPlease";
        strings[2] = "Classified";
        myDataStorage.completeAction(selection, otherNum, strings);

        assertNotNull(myDataStorage.GetCurrentAuthentication());
        //lets look
//        myDataStorage.completeAction(3);
        selection = 3;
        myDataStorage.completeAction(selection, otherNum, strings);

        assertNotNull(myDataStorage.getGames());
        //we should have a game in our list, but...
        assertNotNull(myDataStorage.getGames()[0]);
        //this next line should prove we only have one game...
        assertEquals(myDataStorage.getGames().length, 1);
        //now check that we do not have the game we just made is slot one
        assertNotNull(myDataStorage.getGames()[0].gameName());
        assertNotEquals(myDataStorage.getGames()[0].gameName(), "The Game That Could Not Be");
    }

    @Order(7)
    @Test
    public void joinFail() throws DataAccessException, IOException {
        //make sure we are still in the correct part of the test environment
        //myDataStorage.completeAction(0);
        selection = 0;
        myDataStorage.completeAction(selection, otherNum, strings);
        //Log Out
        assertNotNull(myDataStorage.GetCurrentAuthentication());
        //myDataStorage.completeAction(1);
        selection = 1;
        myDataStorage.completeAction(selection, otherNum, strings);
        assertNull(myDataStorage.GetCurrentAuthentication());
        //Create a new user
        selection = 2;
        strings[0] = "No!!!";
        strings[1] = "OPENSESAME!";
        strings[2] = "@.@@@.@";
        myDataStorage.completeAction(selection, otherNum, strings);
//        ByteArrayInputStream inputFacade = new ByteArrayInputStream("No!!!".getBytes());
//        System.setIn(inputFacade);
//        //next
//        inputFacade = new ByteArrayInputStream("OPENSESAME!".getBytes());
//        System.setIn(inputFacade);
//        //finally
//        inputFacade = new ByteArrayInputStream("@.@@@.@".getBytes());
//        System.setIn(inputFacade);
        //Get the authToken for comparison
        AuthData myStuff = myDataStorage.GetCurrentAuthentication();
        assertNotNull(myStuff);
        assertNotNull(myStuff.username());
        assertNotNull(myStuff.authToken());
        assertEquals(myStuff.username(), "No!!!");
        //Now prove the game exists with a white user...
        assertNotNull(myDataStorage.getGames());
        GameData[] games = myDataStorage.getGames();
        assertNotNull(games[0]);
        assertNotNull(games[0].whiteUsername());
         String rememberMe = games[0].whiteUsername();
        //now try to join it
        //myDataStorage.completeAction(4);
        selection = 4;
        strings[0] = "WHITE";
        myDataStorage.completeAction(selection, otherNum, strings);
        //inputFacade = new ByteArrayInputStream("WHITE".getBytes());
        //System.setIn(inputFacade);
        assertNotNull(games[0]);
        assertNotNull(games[0].whiteUsername());
        assertNotEquals(games[0].whiteUsername(), myStuff.username());
        assertEquals(rememberMe, games[0].whiteUsername());
        //now join as black to prove it can be done...
        //myDataStorage.completeAction(4);
        //inputFacade = new ByteArrayInputStream("BLACK".getBytes());
        //System.setIn(inputFacade);
        //log out
//        selection = 0;
//        myDataStorage.completeAction(selection, otherNum, strings);
//        //log in
//        selection = 3;
//        strings[0] = "inspector";
//        strings[1] = "SayPlease";
//        myDataStorage.completeAction(selection, otherNum, strings);
//        selection = 4;
        strings[0] = "BLACK";
        myDataStorage.completeAction(selection, otherNum, strings);
        myDataStorage.completeAction(3, otherNum, strings);
        assertNotNull(games[0]);
        assertNotNull(games[0].whiteUsername());
        assertNotEquals(games[0].whiteUsername(), myStuff.username());
        assertEquals(rememberMe, games[0].whiteUsername());
        assertEquals(myStuff.username(), games[0].blackUsername());
        //now join again as null to get a chessboard drawn again
//        myDataStorage.completeAction(5);
        selection = 5;
        myDataStorage.completeAction(selection, otherNum, strings);
    }




}
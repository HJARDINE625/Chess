package clientTests;

import dataAccess.DataAccessException;
import model.AuthData;
import model.Responses;
import org.junit.jupiter.api.*;
import server.Server;
import ui.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private static CreateGame gameCreator = new CreateGame();
    private static JoinGame gameJoiner = new JoinGame();
    private static Login authValueGenerator = new Login();
    private static Logout authValueRemover = new Logout();
    private static GetExample gameInfo = new GetExample();

    private static ServerConnector myDataStorage;

    private InputStream normalSystemInput = System.in; // backup System.in to restore it later
    //Unfortunately I had to look up the basics of how to turn my non-input into an input here it is...



    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("localhost", port);
        //Now fill up with all the stuff I actually need to run my tests... based on where I put my code.
        myDataStorage = facade.getMyServer();
        //I think I can access this another way...
//        gameCreator = myDataStorage.getGameCreator();
//        gameJoiner = myDataStorage.getGameJoiner();
//        authValueGenerator = myDataStorage.getAuthValueGenerator();
//        gameInfo = myDataStorage.getGameInfo();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @AfterEach
    @BeforeEach
    private void fixInput(){
        System.setIn(normalSystemInput);
    }

    //these are the five functions I need to test (they are the ones that actually link up with the server... they are found in my datastorage...
//    private CreateGame gameCreator = new CreateGame();
//    private JoinGame gameJoiner = new JoinGame();
//    private Login authValueGenerator = new Login();
//    private Logout authValueRemover = new Logout();
//    private GetExample gameInfo = new GetExample();


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Order(2)
    @Test
    public void loginPass() throws DataAccessException, IOException {
        // Let's use a login method to find someone
        myDataStorage.completeAction(2);
        ByteArrayInputStream inputFacade = new ByteArrayInputStream("Hi".getBytes());
        System.setIn(inputFacade);
        //next
        inputFacade = new ByteArrayInputStream("YouShallNotPass".getBytes());
        System.setIn(inputFacade);
        //finally
        inputFacade = new ByteArrayInputStream("@exe.com".getBytes());
        System.setIn(inputFacade);
        //Get the authToken for comparison
        AuthData myStuff = myDataStorage.GetCurrentAuthentication();
        assertNotNull(myStuff);
        assertNotNull(myStuff.username());
        assertNotNull(myStuff.authToken());
        assertEquals(myStuff.username(), "Hi");
        //log out
        myDataStorage.completeAction(1);

        // Let's use a login method to find someone we have entered.
        myDataStorage.completeAction(3);

        inputFacade = new ByteArrayInputStream("Hi".getBytes());
        System.setIn(inputFacade);
        //next
        inputFacade = new ByteArrayInputStream("YouShallNotPass".getBytes());
        System.setIn(inputFacade);
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
    @Order(4)
    @Test
    public void logoutFail() throws DataAccessException, IOException {
        //add a new user and login...
        //Logout, make sure it works then logout again...
        myDataStorage.completeAction(1);
        assertNull(myDataStorage.GetCurrentAuthentication());
        try {
            myDataStorage.completeAction(1);
        } catch (RuntimeException e) {
            //we exited rather than removing a login, good work
            assertTrue(true);
            return;
        }
        //We should not have gotten here
        fail();
    }
    @Order(3)
    @Test
    public void logoutPass() throws DataAccessException, IOException {
        //Logout, make sure it works
        assertNotNull(myDataStorage.GetCurrentAuthentication());
        myDataStorage.completeAction(1);
        assertNull(myDataStorage.GetCurrentAuthentication());
    }
    @Order(1)
    @Test
    public void loginFail() throws DataAccessException, IOException {
        //make sure no one is in here...
        AuthData oldStuff = myDataStorage.GetCurrentAuthentication();
        assertNull(oldStuff);

        //login with only a username and password before we have added anyone to the system
        myDataStorage.completeAction(3);

        ByteArrayInputStream inputFacade = new ByteArrayInputStream("Hi".getBytes());
        System.setIn(inputFacade);
        //next
        inputFacade = new ByteArrayInputStream("YouShallNotPass".getBytes());
        System.setIn(inputFacade);
        // First lets see if our find method found anything at all. If it did not then we know that we got
        //Get some way of proving we found nothing...
        AuthData newStuff = myDataStorage.GetCurrentAuthentication();
        assertNull(newStuff);
    }



}
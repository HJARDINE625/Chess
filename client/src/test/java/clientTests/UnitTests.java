package clientTests;

import dataAccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import ui.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

//import static clientTests.ServerFacadeTests.myDataStorage;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class UnitTests {
    private static Server server;
    static LoopGenerator facade;
    private static CreateGame gameCreator = new CreateGame();
    private static JoinGame gameJoiner = new JoinGame();
    private static Login authValueGenerator = new Login();
    private static Logout authValueRemover = new Logout();
    private static GetExample gameInfo = new GetExample();

    private static String[] loginStrings = new String[3];

    private static String authenticator = "";

    //this will always fail, unless it has been redefined.
    private static String gameID;

    private String gamename = "A Friendly Game";

    private NewGame myGame;

    private GameData theGame;

    //private UserData myUser = new UserData("True", "True", "Me and You");

    private static ServerConnectorTester myDataStorage;

    private static String urlString = new String();

    private static String color;

    private String game = "/game";
    private String database = "/db";
    private String user = "/user";
    private String auth = "/session";

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new LoopGenerator("localhost", port, false);
        //Now fill up with all the stuff I actually need to run my tests... based on where I put my code.
        myDataStorage = facade.getMyServerTest();
        urlString = "http://" + "localhost" + ":" + Integer.toString(port);
    }

    @BeforeEach
    public void initalize(){
        loginStrings[0] = "True";
        loginStrings[1] = "True";
        loginStrings[2] = "Me And You";
        //UserData myUser = new UserData(loginStrings[0], loginStrings[1], loginStrings[2]);

    }


    @Order(1)
    @Test
    public void loginFail(){
        //login before registration
        try {
            authValueGenerator.login(urlString + auth, new UserData(loginStrings[0], loginStrings[1], null));
        }catch(Exception e){
            //good
            assertTrue(true);
            return;
        }
        //this is bad
        fail();
    }

    @Order(2)
    @Test
    public void createGameFail(){
        //create game while not an existing user...
        try {
            gameCreator.create(urlString + game, authenticator, new NewGame(authenticator, loginStrings[0], gamename));
        }catch(Exception e){
            //good
            assertTrue(true);
            return;
        }
        //this is bad
        fail();
    }

    @Order(3)
    @Test
    public void logoutFail(){
        //logout when not logged in...
        try {
            authValueRemover.doLogout(urlString + auth, authenticator);
        }catch(Exception e){
            //good
            assertTrue(true);
            return;
        }
        //this is bad
        fail();
    }

    @Order(10)
    @Test
    public void getGameFail(){
        //get game when no longer logged in...
        try {
            gameInfo.doGet(urlString + game, authenticator);
        } catch (Exception e) {
            //we can no longer get stuff with deleted auth...
        assertEquals(0,0);
        return;
        }
        //we are messing up at something...
        fail();
    }

    @Order(7)
    @Test
    public void updateGameFail(){
        //update game with nonexistent player color...
        try {
            gameJoiner.join(urlString + game, authenticator, "Yep", gameID);
        } catch(Exception e) {
            //we wanted to get here
        assertTrue(1 == 1);
        return;
    }
        //we got a success somehow...
        fail();
}

    @Order(4)
    @Test
    public void loginPass() throws ReportingException, IOException {
        //login
            AuthData authentication  = authValueGenerator.login(urlString + user, new UserData(loginStrings[0], loginStrings[1], loginStrings[2]));
            assertNotNull(authentication);
            assertEquals(loginStrings[0], authentication.username());
            assertNotNull(authentication.authToken());
            authenticator = authentication.authToken();

    }

    @Order(5)
    @Test
    public void createGamePass() throws ReportingException, IOException {
        //create a game when logged in...
            theGame = gameCreator.create(urlString + game, authenticator, new NewGame(null, null, gamename));
            assertNotNull(theGame);
            gameID = Integer.toString(theGame.gameID());

    }

    @Order(9)
    @Test
    public void logoutPass(){
        //logout, no real return to check here...
        try {
            authValueRemover.doLogout(urlString + auth, authenticator);
        }catch(Exception e){
            //very bad
            fail();
        }
        //no return from this particular method.
        assertTrue(true);
    }

    @Order(6)
    @Test
    public void getGamePass() throws ReportingException, IOException {
        //lets get the games
        assertNotNull(gameInfo.doGet(urlString + game, authenticator));
        //an exception should be thrown if this does not work.
    }

    @Order(8)
    @Test
    public void updateGamePass() throws ReportingException, IOException {
    //We should be able to get our game
        GameData[] allGames = gameInfo.doGet(urlString + game, authenticator);
        //We should be able to update the game we got earlier...
        gameJoiner.join(urlString + game, authenticator, "BLACK", gameID);
        //check that we now exist in the game
        GameData[] allGamesImproved = gameInfo.doGet(urlString + game, authenticator);
        assertNotNull(allGamesImproved);
        assertNotNull(allGames);
        assertEquals(allGamesImproved.length, allGames.length);
        //check that the username was updated.
        for (GameData game: allGames) {
            if(Integer.toString(game.gameID()).equals(gameID)) {
            assertNull(game.blackUsername());
            }
        }
        for (GameData game: allGamesImproved) {
            if(Integer.toString(game.gameID()).equals(gameID)) {
                assertNotNull(game.blackUsername());
            }
        }
    }
}

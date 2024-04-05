package dataAccessTests;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import dataAccess.DataAccessException;
import dataAccess.DataAccesser;
import dataAccess.DataBaseAccesser;
import model.AuthData;
import model.GameData;
import model.Responses;
import model.UserData;
import org.junit.jupiter.api.*;
import passoffTests.obfuscatedTestClasses.TestServerFacade;
import passoffTests.testClasses.TestModels;
import server.Server;
import service.ContolServices;
import service.GameServices;
import service.RegistrationServices;
import service.WebSocketServices;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class finalFewTests {




        //We will use this to test that our insert method is working and failing in the right ways

        private DataAccesser myDataStorage = new DataBaseAccesser();

        private UserData Kevin;

        private RegistrationServices myRegister = new RegistrationServices();

        private ContolServices ender = new ContolServices();

        private GameServices myGameService = new GameServices();

        private WebSocketServices stuffFromWeb = new WebSocketServices();

        private UserData Clue;

        private AuthData loginToken;

        private ChessGame newChessGame = new ChessGame();

        private int ChessGameID;

        private String ChessGameName = "New";

        @BeforeAll
        public static void init() {
                Server server = new Server();
                var port = server.run(0);
                System.out.println("Started test HTTP server on " + port);

                var serverFacade = new TestServerFacade("localhost", Integer.toString(port));

                var existingUser = new TestModels.TestUser();
                existingUser.username = "ExistingUser";
                existingUser.password = "existingUserPassword";
                existingUser.email = "eu@mail.com";

                var newUser = new TestModels.TestUser();
                newUser.username = "NewUser";
                newUser.password = "newUserPassword";
                newUser.email = "nu@mail.com";

                 var createRequest = new TestModels.TestCreateRequest();
                createRequest.gameName = "testGame";
        }

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
            ender.DeleteALL(myDataStorage);
            //we do not need to do anything, here for now...
        }

        @Order(2)
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
                //now create a game


                AuthData myLogin = authDataHolder.getMyAuthData();

                myGameService.createGame(myLogin.authToken(), ChessGameName, myDataStorage);
                // Let's make sure that there is a game now
                Responses gameList = myGameService.listGames(myLogin.authToken(), myDataStorage);
                GameData myGameTest = gameList.getAllGames()[0];
                assertNotNull(myGameTest);
                //now let us make sure it is the right one
                assertEquals(myGameTest.gameName(), ChessGameName);
                ChessGameID = myGameTest.gameID();

                //now use the last few functions for the websocet
//                stuffFromWeb.

        }
        @Order(1)
        @Test
        public void loginPass() throws DataAccessException {
                // Start by inserting a user into the grid database.
                myRegister.register(Kevin, myDataStorage);
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

                myGameService.createGame(loginToken.authToken(), ChessGameName, myDataStorage);
                // Let's make sure that there is a game now
                Responses gameList = myGameService.listGames(loginToken.authToken(), myDataStorage);
                GameData myGameTest = gameList.getAllGames()[0];
                assertNotNull(myGameTest);
                //now let us make sure it is the right one
                assertEquals(myGameTest.gameName(), ChessGameName);
                ChessGameID = myGameTest.gameID();

                Responses game = myGameService.joinGame(loginToken.authToken(), myGameTest.gameID(), null, myDataStorage);
                assertNotNull(game.getMyGameData());
                assertEquals(200, game.getNumericalCode());
                //now let us make sure it is the right one
                GameData myNewGameTest = game.getMyGameData();
                assertEquals(myGameTest.gameName(), ChessGameName);
                //ok let us make sure the ID is still the same
                assertEquals(ChessGameID, myNewGameTest.gameID());
                //now check the special new functions...
                assertEquals(stuffFromWeb.getName(compareTest.authToken(), myDataStorage), compareTest.username());
                stuffFromWeb.removeName(compareTest.authToken(), myDataStorage, stuffFromWeb.findPosition(compareTest.username(), myDataStorage, myNewGameTest.gameID()), myNewGameTest.gameID());
                //did we remove the name?
                assertNull(stuffFromWeb.findPosition(compareTest.username(), myDataStorage, myNewGameTest.gameID()));
               // myDataStorage.getWatchers(myNewGameTest.gameID());
                //GameData theGameTest = game.getMyGameData();
                //add a line here to see it.
                stuffFromWeb.findPosition(compareTest.username(), myDataStorage, ChessGameID);
                String[] watchers= myDataStorage.getWatchers(myNewGameTest.gameID());
                for (String watcher: watchers) {
                        assertNotEquals(watcher, compareTest.username());
                }
                //assertNotEquals(stuffFromWeb.getName(myGameTest.authToken(), myDataStorage), compareTest.username());
                myGameService.joinGame(loginToken.authToken(),ChessGameID, "BLACK", myDataStorage);
                assertEquals(stuffFromWeb.getName(compareTest.authToken(), myDataStorage), compareTest.username());
                //find black
                assertEquals(stuffFromWeb.findPosition(compareTest.username(), myDataStorage, myNewGameTest.gameID()).toString(), "BLACK");
                //leave
                stuffFromWeb.removeName(compareTest.authToken(), myDataStorage, stuffFromWeb.findPosition(compareTest.username(), myDataStorage, myNewGameTest.gameID()), myNewGameTest.gameID());
                myGameService.joinGame(loginToken.authToken(),ChessGameID, "WHITE", myDataStorage);
                //find white.
                assertEquals(stuffFromWeb.findPosition(compareTest.username(), myDataStorage, myNewGameTest.gameID()).toString(), "WHITE");
                //other tests
                //we now need to start the games by setting up the baseboards... maybe we should do this when we pass new games into the database.
                var cheese = stuffFromWeb.moveMaker(compareTest.authToken(), myDataStorage, new ChessMove(new ChessPosition(2,5), new ChessPosition(3,5), null), myNewGameTest.gameID());
                //make sure that move was made!
                Responses games = myGameService.joinGame(loginToken.authToken(),ChessGameID, null, myDataStorage);
                GameData myNewestGameTest = games.getMyGameData();
                assertNotEquals(myNewestGameTest.implementation().getTeamTurn(), myNewGameTest.implementation().getTeamTurn());
                //now make some illegal moves and make sure they do not count.
                stuffFromWeb.moveMaker(compareTest.authToken(), myDataStorage, new ChessMove(new ChessPosition(7,5), new ChessPosition(6,5), null), myNewGameTest.gameID());
                Responses gameses = myGameService.joinGame(loginToken.authToken(),ChessGameID, "BLACK", myDataStorage);
                GameData other = gameses.getMyGameData();
                assertEquals(myNewestGameTest.implementation().getTeamTurn(), other.implementation().getTeamTurn());
                //ChessGame.TeamColor white = new ChessGame.TeamColor();
                //stuffFromWeb.removeName(compareTest.authToken(), myDataStorage, new ChessGame.TeamColor("WHITE"), myDataStorage, myNewGameTest.gameID()), myNewGameTest.gameID());
                stuffFromWeb.moveMaker(compareTest.authToken(), myDataStorage, new ChessMove(new ChessPosition(7,5), new ChessPosition(3,5), null), myNewGameTest.gameID());
                Responses last = myGameService.joinGame(loginToken.authToken(),ChessGameID, null, myDataStorage);
                GameData others = last.getMyGameData();
                assertEquals(others.implementation().getTeamTurn(), other.implementation().getTeamTurn());
                stuffFromWeb.moveMaker(compareTest.authToken(), myDataStorage, new ChessMove(new ChessPosition(7,5), new ChessPosition(5,5), null), myNewGameTest.gameID());
                Responses good = myGameService.joinGame(loginToken.authToken(),ChessGameID, null, myDataStorage);
                GameData move = good.getMyGameData();
                assertNotEquals(others.implementation().getTeamTurn(), move.implementation().getTeamTurn());
                stuffFromWeb.giveUp(compareTest.authToken(), myDataStorage, move.gameID());
                myGameService.joinGame(loginToken.authToken(),ChessGameID, "WHITE", myDataStorage);
                stuffFromWeb.moveMaker(loginToken.authToken(), myDataStorage, new ChessMove(new ChessPosition(2,2), new ChessPosition(4,2), null), myNewGameTest.gameID());
                Responses goods = myGameService.joinGame(loginToken.authToken(),ChessGameID, null, myDataStorage);
                GameData moves = goods.getMyGameData();
                assertEquals(move.implementation().getTeamTurn(), moves.implementation().getTeamTurn());
                //that means we successfully left.

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
        @Order(5)
        @Test
        public void addGamePass() throws DataAccessException {
                // Start by inserting a user into the grid database.
                myRegister.register(Kevin, myDataStorage);
                // Let's use a find method to get the user that we previously put in back out.
                loginToken = myRegister.login(Kevin.username(), Kevin.password(), myDataStorage).getMyAuthData();
                // Lets try to have a new Game added
                myGameService.createGame(loginToken.authToken(), ChessGameName, myDataStorage);
                // Let's make sure that there is a game now
                Responses gameList = myGameService.listGames(loginToken.authToken(), myDataStorage);
                GameData myGameTest = gameList.getAllGames()[0];
                assertNotNull(myGameTest);
                //now let us make sure it is the right one
                assertEquals(myGameTest.gameName(), ChessGameName);
        }
        @Order(6)
        @Test
        public void listGamePass() throws DataAccessException {
                // Start by inserting a user into the grid database.
                myRegister.register(Kevin, myDataStorage);
                // Let's use a find method to get the user that we previously put in back out.
                loginToken = myRegister.login(Kevin.username(), Kevin.password(), myDataStorage).getMyAuthData();
                // Lets try to have a new Game added
                myGameService.createGame(loginToken.authToken(), ChessGameName, myDataStorage);
                // Lets check if we can list games again and get the same output
                Responses gameList = myGameService.listGames(loginToken.authToken(), myDataStorage);
                GameData myGameTest = gameList.getAllGames()[0];
                assertNotNull(myGameTest);
                //now let us make sure it is the right one
                assertEquals(myGameTest.gameName(), ChessGameName);
                //ok let us make sure the ID is still the same
                Responses list = myGameService.listGames(loginToken.authToken(), myDataStorage);
                GameData test = list.getAllGames()[0];
                ChessGameID = test.gameID();
                //the check...
                assertEquals(ChessGameID, myGameTest.gameID());
                //Now make sure the response did not have an error token and gave 200
                assertNull(gameList.getMyException());
                assertEquals(gameList.getNumericalCode(), 200);
        }

        @Order(8)
        @Test
        public void joinGamePass() throws DataAccessException {
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
                // Lets join the real game now
                Responses game = myGameService.joinGame(loginToken.authToken(),ChessGameID, null, myDataStorage);
                assertNotNull(game.getMyGameData());
                assertEquals(200, game.getNumericalCode());
                //now let us make sure it is the right one
                GameData myGameTest = game.getMyGameData();
                assertEquals(myGameTest.gameName(), ChessGameName);
                //ok let us make sure the ID is still the same
                assertEquals(ChessGameID, myGameTest.gameID());
        }
}

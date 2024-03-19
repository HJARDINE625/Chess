package clientTests;

import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import server.Server;
import ui.*;

import java.io.InputStream;

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

public class InputTest {

        private static Server server;
        static LoopGenerator facade;

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
        @Test
        public void inputs() throws IOException {
            facade.executeCommands();
            Assertions.assertTrue(true);
            myDataStorage.getGames();
            assertTrue(true);
        }

    }

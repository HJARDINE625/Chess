package dataAccess;

import java.sql.Connection;
import java.sql.SQLException;

public class TableManager {
    void configureDatabase(Connection conn) throws DataAccessException {
    try(conn){
        //use the connection passed in based upon database manager to access the database database manager looked up...
        //conn.setCatalog(db);

        //here we create our three tables... they should have correct primary and secondary keys and rules...
        var createUserTable = """
            CREATE TABLE  IF NOT EXISTS user (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )""";

        var createAuthTable = """
            CREATE TABLE  IF NOT EXISTS auth (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (authToken)
            )""";

        var createGameTable = """
            CREATE TABLE  IF NOT EXISTS game (
                gameID INT NOT NULL AUTO_INCREMENT,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                implementation longtext NOT NULL,
                PRIMARY KEY (gameID)
            )""";

        //added to make there be an easy way to lookup watchers
        var createObserverTable = """
            CREATE TABLE  IF NOT EXISTS observer (
                thisID INT NOT NULL AUTO_INCREMENT,
                gameID INT NOT NULL,
                watchers longtext NOT NULL,
                PRIMARY KEY (thisID)
            )""";


        try (var createTableStatement = conn.prepareStatement(createUserTable)) {
            createTableStatement.executeUpdate();
        }
        try (var createTableStatement = conn.prepareStatement(createAuthTable)) {
            createTableStatement.executeUpdate();
        }
        try (var createTableStatement = conn.prepareStatement(createGameTable)) {
            createTableStatement.executeUpdate();
        }
        try (var createTableStatement = conn.prepareStatement(createObserverTable)) {
            createTableStatement.executeUpdate();
        }
    } catch (SQLException e) {
        throw new DataAccessException(e.getMessage());
    }
    }
    void truncateALLTables(Connection conn) throws DataAccessException {
            try(conn){
                //use the connection passed in based upon database manager to access the database database manager looked up...

                //here we create our three tables... they should have correct primary and secondary keys and rules...
                var recreateUserTable = """
            TRUNCATE TABLE user""";

                var recreateAuthTable = """
            TRUNCATE TABLE auth""";

                var recreateGameTable = """
            TRUNCATE TABLE game""";

                var recreateObserverTable = """
            TRUNCATE TABLE game""";


                try (var createTableStatement = conn.prepareStatement(recreateUserTable)) {
                    createTableStatement.executeUpdate();
                }
                try (var createTableStatement = conn.prepareStatement(recreateAuthTable)) {
                    createTableStatement.executeUpdate();
                }
                try (var createTableStatement = conn.prepareStatement(recreateGameTable)) {
                    createTableStatement.executeUpdate();
                }
                try (var createTableStatement = conn.prepareStatement(recreateObserverTable)) {
                    createTableStatement.executeUpdate();
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
    }
}

package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.WatcherList;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Properties;
import java.util.UUID;

public class DataBaseAccesser implements DataAccesser{

    private HashSet<UserData> users = new HashSet<UserData>();

    private HashSet<AuthData> authentications = new HashSet<AuthData>();

    private HashSet<GameData> games = new HashSet<GameData>();

    //private DatabaseManager databaseCreator;

    private RowManager rowUpdater;

    private TableManager tableCreator;

    private Connection conn;

    private final String userTable = "user";
    private final String authTable = "auth";
    private final String gameTable = "game";

    private final String observerTable = "observer";

    private SQLInterface implementer = new SQLInterface();

    private BCryptPasswordEncoder encoder;

    //This will have to be directly implemented everywhere as the return type is unknown...
//    SELECT columns_to_return
//    FROM table_to_query
//    WHERE search_condition;
//    void get;
    //String statementBuilder = "SELECT " + " FROM " + " WHERE ";
    //var statement = statementBuilder;


    public DataBaseAccesser(){
        encoder = new BCryptPasswordEncoder();
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) throw new Exception("Unable to laod db.properties");
            Properties props = new Properties();
            props.load(propStream);
            DatabaseManager.createDatabase();
            conn = DatabaseManager.getConnection();
            tableCreator = new TableManager();
            tableCreator.configureDatabase(conn);
            rowUpdater = new RowManager();
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean clear() {
        //reset the three databases by defining them as new databases with nothing inside.
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to laod db.properties");
                Properties props = new Properties();
                props.load(propStream);
                conn = DatabaseManager.getConnection();
                tableCreator.truncateALLTables(conn);
            } catch (DataAccessException e) {
                throw new RuntimeException(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        //now it worked, so return.
        return true;
    }

    //I am using the booleans later to check for errors, so this will only be called if it can work...
    @Override
    public UserData createUser(String username, String password, String email) throws DataAccessException {
        //Before we called this we checked if the username was valid
        if((implementer.allowedChars(password)) && (implementer.allowedChars(email))) {
            UserData newUser = new UserData(username, password, email);
            //String secretPassword = interpreter(password);
            String secretPassword = password;
            String statement = "INSERT INTO " + userTable + " (username, password, email) VALUES (?, ?, ?)";
            implementer.executeUpdate(statement, DatabaseManager.getConnection(), username, secretPassword, email);
//        try {
//            rowUpdater.insert(username, 1, conn, userTable);
//            rowUpdater.update(password, 2, username, conn, userTable);
//            rowUpdater.update(email, 3, username, conn, userTable);
//        } catch (DataAccessException e) {
//            throw new RuntimeException(e.getMessage());
//        }
            return newUser;
            //now call login separately in RegistrationService...
        } else {
            //look for this return value to determine if the game was allowed by char name...
            return null;
        }
    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        if(!((implementer.allowedChars(username)) && (implementer.allowedChars(password)))){
            return null;
            //after checking this it is fine to go on...
        }
        boolean exists = false;
        String email;
        //String secretPassword = interpreter(password);
        //test for now...
        String secretPassword = password;
        if (implementer.exists("username", username, userTable)) {
            String statementBuilder = "SELECT username, password, email FROM " + userTable + " WHERE " + " password=?";
            //return implementer.executeUpdate(DatabaseManager.getConnection(), statementBuilder, username);
            try (Connection conn = DatabaseManager.getConnection()) {
                try (var ps = conn.prepareStatement(statementBuilder)) {
                    ps.setString(1, secretPassword);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            if (rs.getString("username") != null) {
                                if (username.equals(rs.getString("username"))) {
                                    email = rs.getString("email");
                                    return new UserData(username, password, email);
                                } else {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    } catch (Exception e) {
                        throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
                    }
                } catch (Exception e) {
                    throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
                }
            } catch (Exception e) {
                throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
            }
            //for (UserData user: users) {
            //if(user.username().equals(username)){
            //if(user.password().equals(password)){
            //return user;
            //}
            //make the main method find this and if it is found throw incorrect password error...
            //return null;
            //}
            // }
            //make the main method find this and if it is found throw incorrect password error...
            //SELECT username FROM table_to_query
            //WHERE search_condition;
//        if(username.matches("[a-zA-Z]+/\"")) {
//            String statementBuilder = "SELECT * FROM " + userTable + " WHERE " + " username = ?";
//            var statement = statementBuilder;
//            try (var preparedStatement = conn.prepareStatement(statement)) {
//                preparedStatement.setString(1, username);
//                email = preparedStatement.executeQuery();
//                //should not return an int... above...
//            } catch (SQLException e) {
//                throw new RuntimeException(e.getMessage());
//            }
//        } else {
//            return null;
//        }
//        if(password.matches("[a-zA-Z]+/\"")) {
//            String statementBuilder = "SELECT email FROM " + userTable + " WHERE " + " (password) VALUES(?)";
//            var statement = statementBuilder;
//            try (var preparedStatement = conn.prepareStatement(statement)) {
//                preparedStatement.setString(1, username);
//                if(email == preparedStatement.executeUpdate()){
//                    exists = true;
//                }
//                //should not return an int... above...
//            } catch (SQLException e) {
//                throw new RuntimeException(e.getMessage());
//            }
//        } else {
//            return null;
//        }
//            if (!exists) {
//                return null;
//            } else {
//                return new UserData(username, password, email);
//            }
//        }
        }
        else return null;
    }

    //only call if locate game ID does not return an error

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        if(implementer.allowedChars(gameName)) {
            //some formula for making new games
            boolean newUniqueValueFound = false;
            //capitalized to prevent mix-up with element gameID
            int GameID = 0;
            while (newUniqueValueFound == false) {
                GameID = UUID.randomUUID().hashCode();
                newUniqueValueFound = true;
                //negative and zero hashes are invalid, for some reason...
                if (GameID <= 0) {
                    //this is an odd way to do this, but it seems efficient enough...
                    newUniqueValueFound = false;
                    //we will only continue if we have a valid hashcode...
                } else {
                    //now we need to make sure that this hash does not already exist in the database...
//                if (!games.isEmpty()) {
//                    for (GameData game : games) {
//                        if (game.gameID() == GameID) {
//                            newUniqueValueFound = false;
//                            break;
//                        }
//                    }
//                }
                    //if(password.matches("[a-zA-Z]+/\"")) {
//                    String statementBuilder = "SELECT gameID FROM " + gameTable + " WHERE " + " (gameID) VALUES(GameID)";
//                    var statement = statementBuilder;
//                    try (var preparedStatement = conn.prepareStatement(statement)) {
//                        preparedStatement.setInt(1, GameID);
//                        if(preparedStatement.executeUpdate() != null) {
//                            newUniqueValueFound = false;
//                        }
//                        //should not return an int... above...
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e.getMessage());
//                    }
                    if (implementer.exists("gameName", gameName, gameTable)) {
                        throw new DataAccessException("error: game name already taken");
                    }
                }
            }
            //The game will be very incomplete so far as no-one has joined as white or black... but it will need a new ChessGame
            ChessGame gameOfChess = new ChessGame();
            gameOfChess.getBoard().resetBoard();
            gameOfChess.setTeamTurn(ChessGame.TeamColor.WHITE);
            GameData newGame = new GameData(GameID, null, null, gameName, gameOfChess);
            var chessGame = new Gson().toJson(gameOfChess);
//            try {
//                var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, implementation) VALUES (?, ?, ?)";
//                rowUpdater.insert(GameID, 1, conn, gameTable);
//                rowUpdater.update(null, 2, GameID, conn, gameTable);
//                rowUpdater.update(null, 3, GameID, conn, gameTable);
//                rowUpdater.update(gameName, 4, GameID, conn, gameTable);
//                rowUpdater.update(chessGame, 5, GameID, conn, gameTable);
//            } catch (DataAccessException e) {
//                throw new RuntimeException(e.getMessage());
//            }
            int newGameID = implementer.newGame(newGame);
            //We generate a new game of chessID when we add it to the table, so we add that here...
            return new GameData(newGameID, null, null, newGame.gameName(), newGame.implementation());
        } else {
            //or throw an exception
            throw new DataAccessException("error: illegal name");
        }
    }

    //only call if there is a game
    @Override
    public boolean subtractWatcher(int gameID, String watcherName) throws DataAccessException {
        String[] observers = getWatchers(gameID);
        //check boolean; and String which will only allow functions if modified below.
        String[] newObservers = new String[0];
        boolean alreadyExists = false;
        if(observers != null) {
            if(observers.length > 0) {
               newObservers = new String[(observers.length) - 1];
                int i = 0;
                for (String observer : observers) {
                    if (!observer.equals(watcherName)) {
                        newObservers[i] = observer;
                        i++;
                    } else {
                        alreadyExists = true;
                    }
                    //i++;
                }
            }
        }
        //now check already exists
        //these are easist to see as simple code together, they are basically the same overall check.. just not specfic ones.
        if((alreadyExists) && (newObservers.length >= 0)){
            var updateString = new Gson().toJson(new WatcherList(newObservers));
            //this statement is probabaly invalid, but it at least stands as pesdocode here...
            String statement = "UPDATE " + observerTable + " SET watchers=? WHERE gameID=?";
            //executeUpdate(DatabaseManager.getConnection(), statement, newGame.whiteUsername(), ID);
            //String statement = "INSERT INTO " + observerTable + " (observers) VALUES (?)" + " WHERE " + " gameID=?";
            implementer.executeUpdate(DatabaseManager.getConnection(), statement, updateString, gameID);
            return true;
        } else {
            //if()
            //this return might not matter that much to overall logic however, as it just means that the person was added already...
            return false;
        }
    }


    //only call if there is a game
    @Override
    public boolean addWatcher(int gameID, String watcherName) throws DataAccessException {
        String[] observers = getWatchers(gameID);
        //check boolean;
        boolean alreadyExists = false;
        if(observers != null) {
            if(observers.length > 0) {
                for (String observer : observers) {
                    if (observer.equals(watcherName)) {
                        alreadyExists = true;
                        break;
                    }
                }
            }
        }
        //now check already exists
        if(!alreadyExists){
            String[] newObservers;
            int i = 0;
            if(observers != null) {
                newObservers = new String[(observers.length) + 1];
                for (String observer : observers) {
                    newObservers[i] = observer;
                    i++;
                }
            } else {
                newObservers = new String[1];
            }
            newObservers[i] = watcherName;
            var updateString = new Gson().toJson(new WatcherList(newObservers));
            //this statement is probabaly invalid, but it at least stands as pesdocode here...
            //String statement = "INSERT INTO " + observerTable + " (observers) VALUES (?)" + " WHERE " + " gameID=?";
            String statement =  "INSERT INTO " + observerTable + " (watchers, gameID) VALUES (?, ?)";
            //inefficient, but I think it works.
            implementer.delete(gameID,"gameID", observerTable);
            implementer.executeUpdate(statement, DatabaseManager.getConnection(), updateString, gameID);
            //implementer.delete(gameID,"gameID", observerTable);
            //implementer.executeUpdate(statement, DatabaseManager.getConnection(), updateString, gameID);
            return true;
        } else {
            //this return might not matter that much to overall logic however, as it just means that the person was added already...
            return false;
        }
    }

    //only call if there is a game
    @Override
    public String[] getWatchers(int gameID) throws DataAccessException {
        //GameData myNewChessGame = implementer.getGame(gameID, gameTable);
        String statementBuilder = "SELECT watchers FROM " + observerTable + " WHERE " + " gameID=?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statementBuilder)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getString("watchers") != null) {
                            //This should work but is currently untested...
                            //add a add watchers function somewhere else that first gets the Watchers than compares to make sure the new watcher is new... then add a delete...
                            WatcherList listOfWatchers = new Gson().fromJson(rs.getString("watchers"), WatcherList.class);
                            return listOfWatchers.getWatchers();
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
                }
            } catch (Exception e) {
                throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    //call after proving game exists...
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        //Get a serializer...
//        var serializer = new Gson();
//        //now declare a ChessGame and componets
//        ChessGame returnedChessGame;
//        String black;
//        String white;
//        String name;
//
//        //now get the data we need for a response and then check it before passing it into a service...
//        if(username.matches("[a-zA-Z]+/\"")) {
//            String statementBuilder = "SELECT implementation FROM " + gameTable + " WHERE " + " (gameID) VALUES(?)";
//            var statement = statementBuilder;
//            try (var preparedStatement = conn.prepareStatement(statement)) {
//                preparedStatement.setInt(5, gameID);
//                returnedChessGame = serializer.fromJson(preparedStatement.executeUpdate(), ChessGame.class);
//                //should not return an int... above...
//            } catch (SQLException e) {
//                throw new RuntimeException(e.getMessage());
//            }
//        } else {
//            return null;
//        }
//        if(username.matches("[a-zA-Z]+/\"")) {
//            String statementBuilder = "SELECT blackUsername FROM " + gameTable + " WHERE " + " (gameID) VALUES(?)";
//            var statement = statementBuilder;
//            try (var preparedStatement = conn.prepareStatement(statement)) {
//                preparedStatement.setInt(1, gameID);
//                black = preparedStatement.executeUpdate();
//                //should not return an int... above...
//            } catch (SQLException e) {
//                throw new RuntimeException(e.getMessage());
//            }
//        } else {
//            return null;
//        }
//        if(username.matches("[a-zA-Z]+/\"")) {
//            String statementBuilder = "SELECT whiteUsername FROM " + gameTable + " WHERE " + " (gameID) VALUES(?)";
//            var statement = statementBuilder;
//            try (var preparedStatement = conn.prepareStatement(statement)) {
//                preparedStatement.setInt(1, gameID);
//                white = preparedStatement.executeUpdate();
//                //should not return an int... above...
//            } catch (SQLException e) {
//                throw new RuntimeException(e.getMessage());
//            }
//        } else {
//            return null;
//        }
//        if(username.matches("[a-zA-Z]+/\"")) {
//            String statementBuilder = "SELECT gameName FROM " + gameTable + " WHERE " + " (gameID) VALUES(?)";
//            var statement = statementBuilder;
//            try (var preparedStatement = conn.prepareStatement(statement)) {
//                preparedStatement.setInt(1, gameID);
//                name = preparedStatement.executeUpdate();
//                //should not return an int... above...
//            } catch (SQLException e) {
//                throw new RuntimeException(e.getMessage());
//            }
//        } else {
//            return null;
//        }
        //no need for int ids...
        //if(implementer.allowedChars(gameID)) {
            GameData myNewChessGame = implementer.getGame(gameID, gameTable);
            return myNewChessGame;
        //} else {
            //or throw an exception here...
            //return null;
        //}
        //for (GameData game:games) {
            //if(game.gameID() == gameID){
               // return game;
           // }
      //  }
        //if we did not find it something went very wrong
        //return null;
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        HashSet<Integer> gameIDs = new HashSet<Integer>();
        //String statementBuilder = "SELECT gameID FROM " + gameTable;
        //var statement = statementBuilder;
//        try (var preparedStatement = conn.prepareStatement(statement)) {
//            preparedStatement.setInt(1, gameID);
//            gameIDs = Integer.parseInt(preparedStatement.executeUpdate());
//            //should not return an int... above...
//        } catch (SQLException e) {
//            throw new RuntimeException(e.getMessage());
//        }
        return implementer.getGames(gameTable);
//        if(gameIDs.size() == 0) {
//            return null;
//        } else {
//            GameData[] returnArray = new GameData[gameIDs.size()];
//            int currentIndex = 0;
//            for (Integer i : gameIDs) {
//                returnArray[currentIndex] = getGame(i.intValue());
//            }
//            return returnArray;
//        }

        //look for null response and other response from this function
        //if(games.isEmpty()){
           // return null;
      //  } else {
          //  int sizeOfReturnArray = games.size();
         //   GameData[] returnArray = new GameData[sizeOfReturnArray];
         //   int currentArrayLocation = 0;
          //  for (GameData game: games) {
         //       returnArray[currentArrayLocation] = game;
        //        currentArrayLocation++;
        //    }
            //we added every game
        // return returnArray;
        }
    //}
    @Override
    public String usernameFinder(String auth) throws DataAccessException {
        //We only call this at appropriate times (when the user has been found), so I think it is fine to do this...
        String username = "";
        String statementBuilder = "SELECT username FROM " + authTable + " WHERE " + " authToken=?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statementBuilder)) {
                ps.setString(1, auth);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getString("username") != null) {
                            username = rs.getString("username");
                        }
                    }
                } catch (Exception e) {
                    throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
                }
            } catch (Exception e) {
                throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return username;
    }

//    @Override
//    public String usernameFinder(String auth, int gameID) throws DataAccessException {
//        //We only call this at appropriate times (when the user has been found), so I think it is fine to do this...
//        String username = "";
//        String statementBuilder = "SELECT username FROM " + gameTable + " WHERE " + " authToken=?";
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (var ps = conn.prepareStatement(statementBuilder)) {
//                ps.setString(1, auth);
//                try (var rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        if (rs.getString("username") != null) {
//                            username = rs.getString("username");
//                        }
//                    }
//                } catch (Exception e) {
//                    throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
//                }
//            } catch (Exception e) {
//                throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
//            }
//        } catch (Exception e) {
//            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
//        }
//        return username;
//    }
    //...I added a new updateGame to allow for removals
    //First check this person is authorized then that the game exists then that color is available... Then call this function...
    @Override
    public GameData updateGame(int gameID, String clientColor, String auth) throws DataAccessException {
        //We only call this at appropriate times (when the user has been found), so I think it is fine to do this...
        String username = usernameFinder(auth);
        return updateGame(username, clientColor, gameID);
    }
    //here is the removal one...
    @Override
        public GameData updateGame(String username, String clientColor, int gameID) throws DataAccessException{
//        String statementBuilder = "SELECT username FROM " + authTable + " WHERE " + " authToken=?";
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (var ps = conn.prepareStatement(statementBuilder)) {
//                ps.setString(1, auth);
//                try (var rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        if (rs.getString("username") != null) {
//                            username = rs.getString("username");
//                        }
//                    }
//                } catch (Exception e) {
//                    throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
//                }
//            } catch (Exception e) {
//                throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
//            }
//        } catch (Exception e) {
//            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
//        }

        //for (AuthData user: authentications) {
            //if(user.authToken().equals(auth)) {
               // username = user.username();
               // break;
           // }
     //   }
//        String statementBuilder = "SELECT username FROM " + userTable + " WHERE " + " (auth) VALUES(?)";
//        var statement = statementBuilder;
//        //This should not throw an error as the string has been previously checked in another code that called it...
//        try (var preparedStatement = conn.prepareStatement(statement)) {
//            preparedStatement.setInt(1, gameID);
//            username = preparedStatement.executeUpdate();
//            //should not return an int... above...
//        } catch (SQLException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//
//        //add the new game to return
        GameData newGame = new GameData(0, null, null, null, null);
//        //and the game to watch
        GameData oldGame = new GameData(0, null, null, null, null);

        GameData chessGame = getGame(gameID);
        //We have already checked that the username exists, so add them as the user...
       // for (GameData chessGame : games) {
            //remember if we implemented everything else right, there should only be one of these!
            //if (chessGame.gameID() == gameID) {
                //watch this game
                oldGame = chessGame;
                //add the otherplayer  string definition
                String otherPlayer = null;
                //so far this will work, update to add more players...
                if(clientColor != null) {
                    if (clientColor.equals("WHITE")) {
                        //first get the black username (regardless of it is null)...
                        if (chessGame.blackUsername() != null) {
                            otherPlayer = chessGame.blackUsername();
                        }
                        //add a white player
                        newGame = new GameData(gameID, username, otherPlayer, chessGame.gameName(), chessGame.implementation());
//                        try {
//                            rowUpdater.update(username, 2, gameID, conn, gameTable);
//                        } catch (DataAccessException e) {
//                            throw new RuntimeException(e.getMessage());
//                        }
                    } else if (clientColor.equals("BLACK")) {
                        //first get the white username (regardless of it is null)...
                        if (chessGame.whiteUsername() != null) {
                            otherPlayer = chessGame.whiteUsername();
                        }
                        //add a black player
                        newGame = new GameData(gameID, otherPlayer, username, chessGame.gameName(), chessGame.implementation());
//                        try {
//                            rowUpdater.update(username, 3, gameID, conn, gameTable);
//                        } catch (DataAccessException e) {
//                            throw new RuntimeException(e.getMessage());
//                        }
                    }
                }else {
                    //I am not sure that we need to do anything to add a new observer....
                    //can call the observer add code now...
                    addWatcher(gameID, username);
                    return chessGame;
                //}
           // }
        }
        //we must not have changed nothing (otherwise we would have returned), so let us update the chart with one old game gone and a new one in the place of it.
        //games.remove(oldGame);
        //games.add(newGame);
        //done above...
       implementer.updateGame(gameID, newGame);
        return newGame;
    }

    //NOTE: for the breakdown of steps I have broken up the overall pieces of this project into parts here and parts elsewhere... DO NOT CALL THIS FUNCTION UNTIL YOU CHECKED THAT THE CHANGE MAKES SENSE ELSEWHERE!!!
    @Override
    public GameData modifyGameState(int gameID, ChessGame implementation) throws DataAccessException {
        GameData chessGame = getGame(gameID);
        //see how we swap out the implementations here.
        GameData newGame = new GameData(chessGame.gameID(), chessGame.whiteUsername(), chessGame.blackUsername(), chessGame.gameName(), implementation);
        //This function should already work for this... but check here if an error is being thrown...
        implementer.updateGame(gameID, newGame);
        //now return.
        return newGame;
    }

    //only call this after calling get user and checking for non-null responses...
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        //some formula for making new authentications
        boolean newUniqueValueFound = false;
        String authValue = new String();
        while(newUniqueValueFound == false) {
            authValue = UUID.randomUUID().toString();
            newUniqueValueFound = !implementer.exists("authToken", authValue, authTable);
            //if (!authentications.isEmpty()){
               // for (AuthData authenticator: authentications) {
                   // if (authenticator.authToken().equals(authValue)) {
                      //  newUniqueValueFound = false;
                     //   break;
                   // }
              //  }
           // }
            //String statementBuilder = "SELECT authToken FROM " + authTable + " WHERE " + " (username) VALUES(?)";
            //var statement = statementBuilder;
            //This should not throw an error as the string has been previously checked in another code that called it...
//            try (var preparedStatement = conn.prepareStatement(statement)) {
//                preparedStatement.setInt(1, gameID);
//                if(preparedStatement.executeUpdate() != null) {
//                newUniqueValueFound = false;
//                }
//                //should not return an int... above...
//            } catch (SQLException e) {
//                throw new RuntimeException(e.getMessage());
//            }
        }
        AuthData authenticator = new AuthData(authValue, username);
//        try {
//            rowUpdater.insert(authValue, 1, conn, authTable);
//            rowUpdater.update(username, 2, authValue, conn, gameTable);
//        } catch (DataAccessException e) {
//            throw new RuntimeException(e.getMessage());
//        }
        //uthentications.add(authenticator);
        //We checked in another function if the username was valid and we are making the authToken to be valid, so we do not need to check here...
        String statement =  "INSERT INTO " + authTable + " (username, authToken) VALUES (?, ?)";
        implementer.executeUpdate(statement, DatabaseManager.getConnection(), username, authValue);
        return authenticator;

    }

    //Might not be necessary in this implementation...
    //@Override
    //public AuthData getAuth() {
    // return null;
    // }

    //Can call this function without calling function below...
    @Override
    public boolean deleteAuth(String authenticator) throws DataAccessException {
//        if(authentications.isEmpty()){
//            return false;
//        } else {
//            boolean foundIt = false;
//            AuthData authTokenToDelete = new AuthData(null, null);
//            for (AuthData authenticate: authentications) {
//                if(authenticate.authToken().equals(authenticator)) {
//                    foundIt = true;
//                    authTokenToDelete = authenticate;
//                    break;
//                }
//            }
            if (checkAuthorization(authenticator)) {
                try {
                    implementer.delete(authenticator, "authToken", authTable);
                    //rowUpdater.delete(authenticator, 1, conn, authTable);
                } catch (DataAccessException e) {
                    throw new DataAccessException(e.getMessage());
                }
                //authentications.remove(authTokenToDelete);
                return true;
            } else {
                return false;
            }
        }
    //}

    @Override
    public boolean checkAuthorization(String authenticator) throws DataAccessException {
        //if(authentications.isEmpty()){
           // return false;
      //  } else {
         //   boolean foundIt = false;
       //     for (AuthData authenticate: authentications) {
      //          if(authenticate.authToken().equals(authenticator)) {
            //        foundIt = true;
         //    //       break;
            //    }
           // }
        //if(authenticator.matches("[a-zA-Z]+-")) {
//        //boolean foundIt = false;
//        String statementBuilder = "SELECT authToken FROM " + authTable + " WHERE " + " (authenticator) VALUES(?)";
//        var statement = statementBuilder;
//        //This should not throw an error as the string has been previously checked in another code that called it...
//        try (var preparedStatement = conn.prepareStatement(statement)) {
//            preparedStatement.setInt(1, gameID);
//            if(preparedStatement.executeUpdate() != null) {
//                foundIt = true;
//            }
//            //should not return an int... above...
//        } catch (SQLException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//            return foundIt;
//        } else {
//            //this authtentication cannot exist...
//        return false;
//        }
        //if(implementer.allowedChars(authenticator)) {
            return implementer.exists("authToken", authenticator, authTable);
        //} else {
           // throw new DataAccessException("error: illegal name");
       // }
    }

    //only call the color checkers after calling locate game id, and call first noSuchColor then colorTaken...
    @Override
    public boolean colorExists(String color, int gameID) {
        //For now I have implemented this in a way that is fairly easy to check.
        if(color != null) {
            if ((color.equals("WHITE")) || (color.equals("BLACK"))) {
                return true;
                //however at some time, if I implement more teams than just black or white, I will want another checker that is more intelligent, I can ask about this...
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    //call this check last... so gameID exists...
    @Override
    public boolean colorNotTaken(String color, int gameID) throws DataAccessException {
        if(color == null){
            return true;
        } else {
            //for (GameData chessGame: games) {
                //remember if we implemented everything else right, there should only be one of these!
                //if(chessGame.gameID() == gameID){
                    //so far this will work, update to add more players...

            GameData chessGame = getGame(gameID);
                    if(color.equals("WHITE")){
                        if(chessGame.whiteUsername() == null){
                            return true;
                        } else {
                            return false;
                        }
                    } else if(color.equals("BLACK")){
                        if(chessGame.blackUsername() == null){
                            return true;
                        } else {
                            return false;
                        }
                    }
               // }
           // }
            return false;
        }
    }

    @Override
    public boolean locateUsername(String username) throws DataAccessException {
        //if(users.isEmpty()){
        // return false;
        //} else {
        //This will work just fine, as long as no one ever glitches the system to accept a faulty user with no username, assuming this will not happen should be a fine assumption.
        //  for (UserData user: users) {
        //    if(user.username().equals(username)){
        //  return true;
        //   }
        // }
        // return false;
        // }
        if(implementer.allowedChars(username)) {
            return implementer.exists("username", username, userTable);
        } else {
            throw new DataAccessException("error: illegal name");
        }

//        if (username.matches("[a-zA-Z]+/\"")) {
//            String statementBuilder = "SELECT username FROM " + userTable + " WHERE " + " (username) VALUES(?)";
//            var statement = statementBuilder;
//            try (var preparedStatement = conn.prepareStatement(statement)) {
//                preparedStatement.setString(1, username);
//                return (preparedStatement.executeUpdate() != null);
//                //should not return an int... above...
//            } catch (SQLException e) {
//                throw new RuntimeException(e.getMessage());
//            }
//        } else {
//            return false;
//        }
//    }
    }
    //repitiious to what is above, but the Professor said that repition here was fine...

    @Override
    public boolean locateGameID(int gameID) throws DataAccessException {
        //if(games.isEmpty()){
        //return false;
        //  } else {
        //This will work just fine, as long as no one ever glitches the system to accept a faulty game with no ID, assuming this will not happen should be a fine assumption.
        //   for (GameData game: games) {
        //       if(game.gameID() == gameID){
        //          return true;
        //      }
        //  }
        //  return false;
        // }
        //implementer.allowedChars(gameID) no need to worry with ints...
        return implementer.exists(gameID, "gameID", gameTable);
    }

    //This encodes stuff into non-illegal strings... I may need to simply invalidate them if the encode illegally, but for now I will use this...

    private String interpreter(String normalString){
        boolean workingString = false;
        String finalString = null;
        while(!workingString) {
            String testString = encoder.encode(normalString);
            if (implementer.allowedChars(testString)) {
                    workingString = true;
                    finalString = testString;
            }
        }
        return finalString;
    }

    //This decodes the strings...
    private boolean normalizer(String encoded, String normal){
        String equals = encoder.matches(normal, encoded) ? "==" : "!=";
        if(equals.equals("==")){
            return true;
        } if(equals.equals("!=")){
            return false;
        } else {
            throw new RuntimeException("unecriptable");
        }
    }




}


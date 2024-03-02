package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLInterface {    //to delete stuff
    public void delete(Integer id, String idName, String table) throws DataAccessException {
        var statement = "DELETE FROM " + table + " WHERE "+ idName+"=?";
        executeUpdate(DatabaseManager.getConnection(), statement, id);
    }
    //in case the identifier is a String...
    public void delete(String id, String idName, String table) throws DataAccessException {
        var statement = "DELETE FROM " + table + " WHERE "+ idName+"=?";
        executeUpdate(DatabaseManager.getConnection(), statement, id);
    }

    public boolean allowedChars(String testStatement){
        if(testStatement.matches("[a-zA-Z0-9@/.]+-")){
            return true;
        } else {
            return false;
        }
    }

    public int executeUpdate(Connection conn, String statement, Object... params) throws DataAccessException {
        try (conn) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                        //else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    //call this one on user database that has no generated key...
    public void executeUpdate(String statement, Connection conn, Object... params) throws DataAccessException {
        try (conn) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                        //else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    //This function checks if x exist in y in table z
    public boolean exists(int id, String idName, String table) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String StatementBuilder = "SELECT " + idName + " FROM " + table + " WHERE "+ idName+"=?";
            var statement = StatementBuilder;
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (Exception e) {
                throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    //same but for chars...
    public boolean exists(String id, String idName, String table) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String StatementBuilder = "SELECT " + id + " FROM " + table + " WHERE "+ idName+"=?";
            var statement = StatementBuilder;
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (Exception e) {
                throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    //These two functions together get the game for the get game function...
    public GameData getGame(int id, String table) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String StatementBuilder = "SELECT gameID, blackUsername, whiteUsername, gameName, implementation FROM " + table + " WHERE gameID=?";
            var statement = StatementBuilder;
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }
    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getInt("gameID");
        var white = rs.getString("whiteUsername");
        var black = rs.getString("blackUsername");
        var name = rs.getString("gameName");
        var game = rs.getString("implementation");
        var chess = new Gson().fromJson(game, ChessGame.class);
        return new GameData(id, black, white, name, chess);
    }
}

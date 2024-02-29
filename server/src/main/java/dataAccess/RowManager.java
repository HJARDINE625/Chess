package dataAccess;

import java.sql.Connection;
import java.sql.SQLException;

//unfortunately this class will not be able to adequately return a table it knows nothing about... so find and returns will have
//to be in another one...
public class RowManager {

    //use this function to add the first name/identifier in a table...
    void insert(String name, int updatePoint, Connection conn, String table) throws DataAccessException {

        if (name.matches("[a-zA-Z]+/\"")) {
            String statementBuilder = "INSERT INTO " + table + " (name) VALUES(?)";
            var statement = statementBuilder;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(updatePoint, name);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }

    //use this function to add the first name/identifier in a table... if it is a int...
    void insert(int name, int updatePoint, Connection conn, String table) throws DataAccessException {

        //double check that the (name) stuff actually works...
            String statementBuilder = "INSERT INTO " + table + " (name) VALUES(?)";
            var statement = statementBuilder;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(updatePoint, name);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
    }

    //use this function to modify part of a table or to add the other rows for it...
    void update(String name, int updatePoint, int id, Connection conn, String table) throws DataAccessException {

        if (name.matches("[a-zA-Z]+/\"")) {
            String statementBuilder = "UPDATE " + table + " SET (name) VALUES(?) WHERE (id)=?";
            var statement = statementBuilder;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(updatePoint, name);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }

    //use this function to modify part of a table or to add the other rows for it... if the primary key is a String...
    void update(String name, int updatePoint, String id, Connection conn, String table) throws DataAccessException {

        if ((name.matches("[a-zA-Z]+/\"")) && (id.matches("[a-zA-Z]+/\""))) {
            String statementBuilder = "UPDATE " + table + " SET (name) VALUES(?) WHERE (id)=?";
            var statement = statementBuilder;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(updatePoint, name);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }

    //use this function to delete a single row from a table
    void delete(int id, int index, Connection conn, String table) throws DataAccessException {
        String statementBuilder = "DELETE FROM " + table + " WHERE (id)=?";
        var statement = statementBuilder;
        try (var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setInt(index, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
        throw new DataAccessException(e.getMessage());
    }
    }

    //use this function to delete a single row from a table... if the key is a String...
    void delete(String id, int index, Connection conn, String table) throws DataAccessException {
        if (id.matches("[a-zA-Z]+/\"")) {
            String statementBuilder = "DELETE FROM " + table + " WHERE (id)=?";
            var statement = statementBuilder;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(index, id);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        }
    }
    //the function for dropping whole tables is linked with the one for making new ones in TableManager...
}

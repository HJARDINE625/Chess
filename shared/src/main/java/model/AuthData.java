package model;

import java.util.Objects;

final class AuthData {
    private final String authToken;
    private final String username;

    AuthData(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public String authToken() {
        return authToken;
    }

    public String username() {
        return username;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AuthData) obj;
        return Objects.equals(this.authToken, that.authToken) &&
                Objects.equals(this.username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, username);
    }

    @Override
    public String toString() {
        return "AuthData[" +
                "authToken=" + authToken + ", " +
                "username=" + username + ']';
    }
}

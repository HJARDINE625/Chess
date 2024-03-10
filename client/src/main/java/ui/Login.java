package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//This can actually work for both Login and Register as long as you previously ensure the correct values are passed in.

public class Login {
    public AuthData login(String urlString) throws IOException, ReportingException {
    URL url = new URL(urlString);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setReadTimeout(5000);
    connection.setRequestMethod("GET");

    //gives body issues...
    var outputStream = connection.getOutputStream();
    var json = new Gson().toJson(new UserData(null, null, null));
    outputStream.write(json.getBytes());

    // Set HTTP request headers, if necessary
    // connection.addRequestProperty("Accept", "text/html");
    //connection.addRequestProperty("Authorization", "fjaklc8sdfjklakl");
    //here we are getting the AuthToken, so the above makes no sense...
    connection.connect();

    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
        // Get HTTP response headers, if necessary
        // Map<String, List<String>> headers = connection.getHeaderFields();
        var input = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(input);
        var Gson = new Gson().fromJson(inputStreamReader, AuthData.class);
        return Gson;
        //process these responses differently to allow for a proper string output...
//        if(Gson[0] == null) {
//            return null;
//        } else {
//            return Gson;
//        }
        // OR

        //connection.getHeaderField("Content-Length");

        //InputStream responseBody = connection.getInputStream();
        // Read and process response body from InputStream ...
    } else {
        var input = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(input);
        var Gson = new Gson().fromJson(inputStreamReader, ReportingException.class);
        throw Gson;
        // SERVER RETURNED AN HTTP ERROR
    }
}
}

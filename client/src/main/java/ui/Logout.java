package ui;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Logout {
    public void doLogout(String urlString) throws IOException, ReportingException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("DELETE");

        //gives body issues...
        var outputStream = connection.getOutputStream();
//        var json = new Gson().toJson(new String "authentication");
//        outputStream.write(json.getBytes());

        // Set HTTP request headers, if necessary
        // connection.addRequestProperty("Accept", "text/html");
        connection.addRequestProperty("Authorization", "fjaklc8sdfjklakl");

        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Get HTTP response headers, if necessary
            // Map<String, List<String>> headers = connection.getHeaderFields();
//            var input = connection.getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(input);
//            var Gson = new Gson().fromJson(inputStreamReader, GameData[].class);
            //process these responses differently to allow for a proper string output...

            // OR
            //all we need for this function...
            return;
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

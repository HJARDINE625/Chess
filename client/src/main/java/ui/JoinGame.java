package ui;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinGame {
    public void join(String urlString) throws IOException, ReportingException {
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
        connection.addRequestProperty("Authorization", "fjaklc8sdfjklakl");

        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Get HTTP response headers, if necessary
            // Map<String, List<String>> headers = connection.getHeaderFields();
//            var input = connection.getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(input);
//            var Gson = new Gson().fromJson(inputStreamReader, GameData.class);
//            //process these responses differently to allow for a proper string output...
//            return Gson;
            //all we need here
            return;
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

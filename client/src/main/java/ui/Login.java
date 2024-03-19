package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.AuthDataInt;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//This can actually work for both Login and Register as long as you previously ensure the correct values are passed in.

public class Login {
    public AuthData login(String urlString, UserData gson) throws IOException, ReportingException {
    URL url = new URL(urlString);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setReadTimeout(100000);
    connection.setRequestMethod("POST");

    //gives body issues...
        connection.setDoOutput(true);
//    var outputStream = connection.getOutputStream();
//    var json = new Gson().toJson(gson);
//    outputStream.write(json.getBytes());

        try (var outputStream = connection.getOutputStream()) {
            var json = new Gson().toJson(gson);
            outputStream.write(json.getBytes());
        }

    // Set HTTP request headers, if necessary
    // connection.addRequestProperty("Accept", "text/html");
    //connection.addRequestProperty("Authorization", "fjaklc8sdfjklakl");
    //here we are getting the AuthToken, so the above makes no sense...
    connection.connect();

    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
        // Get HTTP response headers, if necessary
        // Map<String, List<String>> headers = connection.getHeaderFields();
        var input = connection.getInputStream();
        try(input) {
            InputStreamReader inputStreamReader = new InputStreamReader(input);
            try(inputStreamReader) {
                var Gson = new Gson().fromJson(inputStreamReader, AuthData.class);
                connection.disconnect();
                return Gson;
            }
        }
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
        try {
            var input = connection.getInputStream();
            try (input) {
                InputStreamReader inputStreamReader = new InputStreamReader(input);
                try(inputStreamReader) {
                    var Gson = new Gson().fromJson(inputStreamReader, ReportingException.class);
                    connection.disconnect();
                    throw Gson;
                }
            }
            } catch (IOException e) {
                ExceptionTransformer error = new ExceptionTransformer();
                error.transform(e);
                connection.disconnect();
                //in case there is not an available transformation...
                throw e;
            }
        // SERVER RETURNED AN HTTP ERROR
    }
}
}

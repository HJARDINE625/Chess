package ui;

import com.google.gson.Gson;
import model.AuthDataInt;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JoinGame {
    public void join(String urlString, String authentication, String color, String gameID) throws IOException, ReportingException {
        URL url = new URL(urlString);
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print("Color " + color + " ID" + " " + gameID);
        new AuthDataInt(gameID, color);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(100000);
        connection.setRequestMethod("PUT");
        //out.print("HERE!!!");

        //connection.setRequestMethod("PUT");

        //gives body issues...
//        var outputStream = connection.getOutputStream();
//        var json = new Gson().toJson(new AuthDataInt(gameID, color));
//        outputStream.write(json.getBytes());

        // Set HTTP request headers, if necessary
        // connection.addRequestProperty("Accept", "text/html");
        connection.addRequestProperty("Authorization", authentication);

//        var outputStream = connection.getOutputStream();
//        var json = new Gson().toJson(new AuthDataInt(gameID, color));
//        outputStream.write(json.getBytes());
        connection.setDoOutput(true);
        //connection.setRequestMethod("PUT");
        try (var outputStream = connection.getOutputStream()) {
            var json = new Gson().toJson(new AuthDataInt(gameID, color));
            outputStream.write(json.getBytes());
        }

        connection.connect();
        //out.print("Connected... ");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            out.print("OK!\n");
            // Get HTTP response headers, if necessary
            // Map<String, List<String>> headers = connection.getHeaderFields();
//            var input = connection.getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(input);
//            var Gson = new Gson().fromJson(inputStreamReader, GameData.class);
//            //process these responses differently to allow for a proper string output...
//            return Gson;
            //all we need here
            connection.disconnect();
            //return;
            // OR

            //connection.getHeaderField("Content-Length");

            //InputStream responseBody = connection.getInputStream();
            // Read and process response body from InputStream ...
        } else {
            try {
                //out.print("NOOO!\n");

                var input = connection.getInputStream();
                try(input) {
                    InputStreamReader inputStreamReader = new InputStreamReader(input);
                    try(inputStreamReader) {
                        var Gson = new Gson().fromJson(inputStreamReader, ReportingException.class);
                        connection.disconnect();
                        throw Gson;
                    }
                }
            } catch(IOException e){
                ExceptionTransformer error = new ExceptionTransformer();
                error.transform(e);
                connection.disconnect();
                out.print("ERROR!\n");
                //in case there is not an available transformation...
                throw e;
            }
            // SERVER RETURNED AN HTTP ERROR
        }
    }
}

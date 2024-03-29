package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.NewGame;
import model.UserData;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CreateGame {
    public GameData create(String urlString, String authentication, NewGame games) throws IOException, ReportingException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(100000);
        connection.setRequestMethod("POST");

        //gives body issues...
//        var outputStream = connection.getOutputStream();
//        var json = new Gson().toJson(game);
//        outputStream.write(json.getBytes());

        // Set HTTP request headers, if necessary
        // connection.addRequestProperty("Accept", "text/html");
        connection.addRequestProperty("Authorization", authentication);

        connection.setDoOutput(true);
//        var outputStream = connection.getOutputStream();
//        var json = new Gson().toJson(games);
//        outputStream.write(json.getBytes());
        try (var outputStream = connection.getOutputStream()) {
            var json = new Gson().toJson(games);
            outputStream.write(json.getBytes());
//            var json = new Gson().toJson(new String("Arbitrarily Given"));
//            outputStream.write(json.getBytes());
        }

        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Get HTTP response headers, if necessary
            // Map<String, List<String>> headers = connection.getHeaderFields();
            var input = connection.getInputStream();
            try(input){
                InputStreamReader inputStreamReader = new InputStreamReader(input);
                try(inputStreamReader) {
                    var Gson = new Gson().fromJson(inputStreamReader, GameData.class);
                    //process these responses differently to allow for a proper string output...
                    connection.disconnect();
                    return Gson;
                }
            }

            // OR

            //connection.getHeaderField("Content-Length");

            //InputStream responseBody = connection.getInputStream();
            // Read and process response body from InputStream ...
        } else {
            try {
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
                //in case there is not an available transformation...
                throw e;
            }
            // SERVER RETURNED AN HTTP ERROR
        }
    }
}

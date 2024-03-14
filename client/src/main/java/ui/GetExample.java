package ui;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetExample {

    public GameData[] doGet(String urlString, String authentication) throws IOException, ReportingException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(100000);
        connection.setRequestMethod("GET");

        //gives body issues...
        //var outputStream = connection.getOutputStream();
//        var json = new Gson().toJson(new UserData(null, null, null));
//        outputStream.write(json.getBytes());

        // Set HTTP request headers, if necessary
        // connection.addRequestProperty("Accept", "text/html");
        connection.addRequestProperty("Authorization", authentication);

        connection.setDoOutput(true);
        var outputStream = connection.getOutputStream();

        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Get HTTP response headers, if necessary
            // Map<String, List<String>> headers = connection.getHeaderFields();
            var input = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(input);
            var Gson = new Gson().fromJson(inputStreamReader, GameData[].class);
            //process these responses differently to allow for a proper string output...
            if(Gson[0] == null) {
                return null;
            } else {
                return Gson;
            }
            // OR

            //connection.getHeaderField("Content-Length");

            //InputStream responseBody = connection.getInputStream();
            // Read and process response body from InputStream ...
        } else {
            try {
                var input = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(input);
                var Gson = new Gson().fromJson(inputStreamReader, ReportingException.class);
                throw Gson;
            } catch(IOException e){
                ExceptionTransformer error = new ExceptionTransformer();
                error.transform(e);
                //in case there is not an available transformation...
                throw e;
            }
            // SERVER RETURNED AN HTTP ERROR
        }
    }
}
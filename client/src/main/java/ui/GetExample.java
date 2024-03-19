package ui;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import model.GameData;
import model.GameDecomplier;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;


public class GetExample {
    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

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
        connection.getRequestMethod();

        out.print("outputting");
        //methods with setDoOutput(true), will find a request method that works...
//        connection.setDoOutput(true);
//        try (var outputStream = connection.getOutputStream()) {
//            //        var json = new Gson().toJson(new String("Arbitrarily Given"));
////        outputStream.write(json.getBytes());
//        }
//        out.print("outputted");
////        var json = new Gson().toJson(new String("Arbitrarily Given"));
////        outputStream.write(json.getBytes());
//
//
//        out.print("connecting");
        connection.connect();
        out.print("connected");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Get HTTP response headers, if necessary
            // Map<String, List<String>> headers = connection.getHeaderFields();
            var input = connection.getInputStream();
            try(input) {
                InputStreamReader inputStreamReader = new InputStreamReader(input);
                try (inputStreamReader) {
//            //The following gives the protocol...
//            String huh = input.toString();
//            int check = huh.length();
//            StringBuilder otherpossiblity = new StringBuilder();
//            for (int i = 0; i < huh.length(); i++) {
//                String moreHelpful = Character.toString(huh.charAt(i));
//                otherpossiblity.append(moreHelpful);
//            }
//            String now = otherpossiblity.toString();
//            Object Json = input.readAllBytes();
//            String help = Json.toString();
//            StringBuilder finalValue = new StringBuilder();
//            for (int i = 0; i < help.length(); i++) {
//                String moreHelpful = Character.toString(help.charAt(i));
//                finalValue.append(moreHelpful);
//            }
//            String moreHelpful = finalValue.toString();
//            String json = moreHelpful;
//            //var Gson = new GameData[1];
                    try {
                        var Gson = new Gson().fromJson(inputStreamReader, GameDecomplier.class);
                        if (Gson == null) {
                            return null;
                        } else if (Gson.getGames() == null) {
                            connection.disconnect();
                            return null;
                        } else if (Gson.getGames() == null) {
                            connection.disconnect();
                            return null;
                        } else if (Gson.getGames().length == 0) {
                            connection.disconnect();
                            return null;
                        } else if (Gson.getGames()[0] == null) {
                            connection.disconnect();
                            return null;
                        } else {
                            connection.disconnect();
                            return Gson.getGames();
                        }
                    } catch (JsonSyntaxException e) {
                        try {
//                    String help = inputStreamReader
                            //String please = help.read();
                            JsonReader myReader = new JsonReader(inputStreamReader);
                            myReader.setLenient(true);
//                    String jsonString = EntityUtils.toString(connection..getEntity());
                            //var gson = new Gson().fromJson(myReader, GameData[].class);
                            Type collectionType = new TypeToken<Collection<GameData>>() {
                            }.getType();
                            Collection<GameData> enums = new Gson().fromJson(myReader, collectionType);
//                    String test = Arrays.toString(gson);
                            connection.disconnect();
                            int i;
                            return null;
                        } catch (JsonSyntaxException r) {
                            connection.disconnect();
                            return null;
                        }
                    } catch (JsonIOException e) {
                        connection.disconnect();
                        throw new RuntimeException(e);
                    }
//                    try {
////                    String help = inputStreamReader
//                        //String please = help.read();
//                        JsonReader myReader = new JsonReader(inputStreamReader);
//                        myReader.setLenient(true);
////                    String jsonString = EntityUtils.toString(connection..getEntity());
//                        //var gson = new Gson().fromJson(myReader, GameData[].class);
//                        Type collectionType = new TypeToken<Collection<GameData>>() {
//                        }.getType();
//                        Collection<GameData> enums = new Gson().fromJson(myReader, collectionType);
////                    String test = Arrays.toString(gson);
//                        connection.disconnect();
//                        int i;
//                        return null;
//                    } catch (JsonSyntaxException r) {
//                        connection.disconnect();
//                        return null;
//                    }
                }
            }
            //process these responses differently to allow for a proper string output...
//            if(Gson[0] == null) {
//                connection.disconnect();
//                return null;
//            } else {
//                connection.disconnect();
//                return Gson;
//            }
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
                out.print("??????????");
                error.transform(e);
                connection.disconnect();
                //in case there is not an available transformation...
                throw e;
            }
            // SERVER RETURNED AN HTTP ERROR
        }

    }
}
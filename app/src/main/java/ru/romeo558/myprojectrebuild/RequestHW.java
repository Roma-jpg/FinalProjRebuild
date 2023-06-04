package ru.romeo558.myprojectrebuild;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestHW {

    public static void sendRequest(final String email, final String password, final String date, final Callback callback) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                String apiUrl = "http://188.120.238.71/";

                try {
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Create the JSON request body
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("login", email);
                    requestBody.put("password", password);
                    requestBody.put("date", date);

                    // Send the request body
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(requestBody.toString().getBytes());
                    outputStream.flush();
                    outputStream.close();

                    // Read the response
                    StringBuilder response = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    return response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    callback.onSuccess(result);
                } else {
                    callback.onError();
                }
            }
        }.execute();
    }

    public static void checkServerResponse(final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL serverUrl = new URL("http://188.120.238.71/");
                    HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        StringBuilder response = new StringBuilder();

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        reader.close();

                        callback.onSuccess(response.toString());
                    } else {
                        callback.onError();
                    }
                } catch (IOException e) {
                    callback.onError();
                }
            }
        }).start();
    }

    public interface Callback {
        void onSuccess(String response);
        void onError();
    }
}

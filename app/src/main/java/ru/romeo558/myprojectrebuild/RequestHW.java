package ru.romeo558.myprojectrebuild;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestHW {

    // Отправка асинхронного запроса
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

                    // Создание тела JSON-запроса
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("login", email);
                    requestBody.put("password", password);
                    requestBody.put("date", date);

                    // Отправка тела запроса
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(requestBody.toString().getBytes());
                    outputStream.flush();
                    outputStream.close();

                    // Чтение ответа
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
                    runOnUIThread(() -> {
                        try {
                            callback.onSuccess(result);
                        } catch (JSONException e) {
                            callback.onError();
                        }
                    });
                } else {
                    callback.onError();
                }
            }
        }.execute();
    }

    // Запуск выполнения кода на основном потоке пользовательского интерфейса
    private static void runOnUIThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    // Интерфейс обратного вызова для обработки успешного выполнения запроса или ошибки
    public interface Callback {
        void onSuccess(String response) throws JSONException;

        void onError();
    }
}

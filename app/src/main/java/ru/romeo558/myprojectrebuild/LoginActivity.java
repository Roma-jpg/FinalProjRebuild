package ru.romeo558.myprojectrebuild;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Инициализация элементов интерфейса
        EditText emailInput = findViewById(R.id.email_input);
        EditText passwordInput = findViewById(R.id.password_input);
        TextView appName = findViewById(R.id.textView);
        Button authButton = findViewById(R.id.login_button);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Проверка доступности интернета
        if (!isInternetAvailable()) {
            System.out.println("The device is not connected to the Internet.");
            // Показ диалогового окна с полупрозрачным фоном
            NoInternetDialogFragment dialogFragment = new NoInternetDialogFragment();
            dialogFragment.setCancelable(false);
            dialogFragment.show(getSupportFragmentManager(), "no_internet_dialog");
        } else {
            System.out.println("Device has internet connection.");
        }

        // Проверка сохраненных учетных данных
        boolean hasStoredCredentials = sharedPreferences.getBoolean("hasStoredCredentials", false);
        if (hasStoredCredentials) {
            String savedEmail = sharedPreferences.getString("email", "");
            String savedPassword = sharedPreferences.getString("password", "");

            switchToMainActivity(savedEmail, savedPassword);
        }

        // Проверка, был ли выполнен вход ранее
        boolean everEntered = sharedPreferences.getBoolean("everEntered", false);
        if (!everEntered){
            switchToWelcomePage();
        }

        // Анимация элементов интерфейса
        emailInput.setAlpha(0f);
        passwordInput.setAlpha(0f);
        appName.setAlpha(0f);
        authButton.setAlpha(0f);

        appName.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(400)
                .start();

        emailInput.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(500)
                .start();

        passwordInput.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(600) // Задержка 200 мс между анимациями полей ввода
                .start();

        authButton.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(700)
                .start();

        // Обработчик нажатия на кнопку "authButton"
        authButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(view, "Please enter both email and password", Snackbar.LENGTH_SHORT).show();
                return;
            }

            // Сохранение введенной электронной почты и пароля
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email);
            editor.putString("password", password);
            editor.putBoolean("hasStoredCredentials", true);
            editor.apply();

            // Отправка POST-запроса к API
            new UserVerificationTask().execute(email, password);
        });
    }

    // Переход на страницу приветствия
    private void switchToWelcomePage() {
        Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("StaticFieldLeak")
    private class UserVerificationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            String apiUrl = "http://188.120.238.71/";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Создание JSON-тела запроса
                JSONObject requestBody = new JSONObject();
                requestBody.put("login", email);
                requestBody.put("password", password);

                Calendar calendar;
                calendar = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String formattedDate = formatter.format(calendar.getTime());

                requestBody.put("date", formattedDate);

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
                try {
                    // Декодирование Unicode-символов в ответе
                    String unescapedResult = unescapeUnicode(result);
                    System.out.println("\n=================\n" + unescapedResult + "\n==================\n");

                    JSONObject jsonResponse = new JSONObject(unescapedResult);
                    String className = jsonResponse.getString("class_name");
                    String studentName = jsonResponse.getString("student_name");
                    System.out.printf("Class:%s, Student Name:%s", className, studentName);
                    // Вход выполнен успешно
                    Toast.makeText(LoginActivity.this, "Логин прошёл успешно.", Toast.LENGTH_SHORT).show();

                    // Запуск MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    String[] nameParts = studentName.split(" ");
                    String middleName = nameParts[1]; // Получение второго слова из массива
                    intent.putExtra("student_name", middleName.toUpperCase());
                    intent.putExtra("json_response", unescapedResult);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Не получилось войти. Проверьте свой логин и пароль.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Не получилось подключиться к серверу. Повторите позже.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Декодирование Unicode-символов
    private static String unescapeUnicode(String unicodeEscapedString) {
        StringBuilder builder = new StringBuilder();
        int length = unicodeEscapedString.length();
        for (int i = 0; i < length; i++) {
            char currentChar = unicodeEscapedString.charAt(i);
            if (currentChar == '\\' && i + 1 < length && unicodeEscapedString.charAt(i + 1) == 'u') {
                // Найдена последовательность Unicode Escape
                String unicodeHex = unicodeEscapedString.substring(i + 2, i + 6);
                int codePoint = Integer.parseInt(unicodeHex, 16);
                builder.append((char) codePoint);
                i += 5; // Пропуск следующих 5 символов
            } else {
                builder.append(currentChar);
            }
        }
        return builder.toString();
    }

    // Переход на MainActivity
    private void switchToMainActivity(String login, String password) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("login", login);
        intent.putExtra("password", password);
        intent.putExtra("hasCred", true);
        intent.putExtra("fromLogin", true);
        startActivity(intent);
        finish();
    }

    // Проверка доступности интернета
    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}

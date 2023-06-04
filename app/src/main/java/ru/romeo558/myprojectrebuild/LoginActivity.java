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
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import ru.romeo558.myprojectrebuild.MainActivity;
import ru.romeo558.myprojectrebuild.R;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

        EditText emailInput = findViewById(R.id.email_input);
        EditText passwordInput = findViewById(R.id.password_input);
        TextView appName = findViewById(R.id.textView);
        Button authButton = findViewById(R.id.login_button);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (!isInternetAvailable()) {
            System.out.println("The device is not connected to the Internet.");
            // Show a dialog fragment with a semi-transparent background
            NoInternetDialogFragment dialogFragment = new NoInternetDialogFragment();
            dialogFragment.setCancelable(false);
            dialogFragment.show(getSupportFragmentManager(), "no_internet_dialog");
        } else {
            System.out.println("Device has internet connection.");
        }

        boolean hasStoredCredentials = sharedPreferences.getBoolean("hasStoredCredentials", false);
        if (hasStoredCredentials) {
            String savedEmail = sharedPreferences.getString("email", "");
            String savedPassword = sharedPreferences.getString("password", "");

            switchToMainActivity(savedEmail, savedPassword);
        }
        boolean everEntered = sharedPreferences.getBoolean("everEntered", false);
        if (!everEntered){
            switchToWelcomePage();
        }

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
                .setStartDelay(600) // 200ms delay between EditText animations
                .start();

        authButton.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(700)
                .start();

        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Snackbar.make(view, "Please enter both email and password", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // Store the entered email and password
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.putBoolean("hasStoredCredentials", true);
                editor.apply();

                // Send the POST request to the API
                new UserVerificationTask().execute(email, password);
            }
        });
    }

    private void switchToWelcomePage() {
        Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }


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

                // Create the JSON request body
                JSONObject requestBody = new JSONObject();
                requestBody.put("login", email);
                requestBody.put("password", password);

                Calendar calendar;
                calendar = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String formattedDate = formatter.format(calendar.getTime());

                requestBody.put("date", formattedDate);

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
                try {
                    // Unescape Unicode characters in the response
                    String unescapedResult = unescapeUnicode(result);
                    System.out.println("\n=================\n" + unescapedResult + "\n==================\n");

                    JSONObject jsonResponse = new JSONObject(unescapedResult);
                    String className = jsonResponse.getString("class_name");
                    String studentName = jsonResponse.getString("student_name");
                    System.out.printf("Class:%s, Student Name:%s", className, studentName);
                    // Login successful
                    Toast.makeText(LoginActivity.this, "Логин прошёл успешно.", Toast.LENGTH_SHORT).show();

                    // Start the MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    String[] nameParts = studentName.split(" ");
                    String middleName = nameParts[1]; // Получаем второе слово из массива
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

    private static String unescapeUnicode(String unicodeEscapedString) {
        StringBuilder builder = new StringBuilder();
        int length = unicodeEscapedString.length();
        for (int i = 0; i < length; i++) {
            char currentChar = unicodeEscapedString.charAt(i);
            if (currentChar == '\\' && i + 1 < length && unicodeEscapedString.charAt(i + 1) == 'u') {
                // Found a Unicode Escape sequence
                String unicodeHex = unicodeEscapedString.substring(i + 2, i + 6);
                int codePoint = Integer.parseInt(unicodeHex, 16);
                builder.append((char) codePoint);
                i += 5; // Skip the next 5 characters
            } else {
                builder.append(currentChar);
            }
        }
        return builder.toString();
    }

    private void switchToMainActivity(String login, String password) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("login", login);
        intent.putExtra("password", password);
        intent.putExtra("hasCred", true);
        intent.putExtra("fromLogin", true);
        startActivity(intent);
        finish();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}

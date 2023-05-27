package ru.romeo558.myprojectrebuild;

import android.content.Intent;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
        EditText emailInput = findViewById(R.id.email_input);
        EditText passwordInput = findViewById(R.id.password_input);
        TextView appName = findViewById(R.id.textView);
        Button authButton = findViewById(R.id.login_button);

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

                if (email.equals("Romeo558") && password.equals("Zz12345!!!")) {
                    startActivity(new Intent(LoginActivity.this, EasterEggActivity.class));
                    finish();
                } else {
                    // Normal login logic
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    private void saveCredentials(String email, String password) {
        try {
            File file = new File(getFilesDir(), "login_shit.xml");
            FileOutputStream outputStream = new FileOutputStream(file);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            xmlSerializer.setOutput(outputStream, "UTF-8");
            xmlSerializer.startDocument(null, Boolean.valueOf(true));
            xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            xmlSerializer.startTag(null, "credentials");
            xmlSerializer.startTag(null, "email");
            xmlSerializer.text(email);
            xmlSerializer.endTag(null, "email");
            xmlSerializer.startTag(null, "password");
            xmlSerializer.text(password);
            xmlSerializer.endTag(null, "password");
            xmlSerializer.endTag(null, "credentials");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

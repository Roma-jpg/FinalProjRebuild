package ru.romeo558.myprojectrebuild;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EasterEggActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Да-да. Пасхалка)
        // Я всегда в таких проектах оставляю пасхалки и этот - не исключение
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easter_egg);

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            startActivity(new Intent(EasterEggActivity.this, LoginActivity.class));
            finish();
        });
    }
}

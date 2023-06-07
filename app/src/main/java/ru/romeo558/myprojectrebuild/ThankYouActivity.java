package ru.romeo558.myprojectrebuild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ThankYouActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        Button closeButton = findViewById(R.id.close_button);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Слушатель кнопки closeButton
        closeButton.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("everEntered", true); // Теперь приложение знает, что пользователь пользовался приложением.
            editor.apply();
            Intent welcomeIntent = new Intent(ThankYouActivity.this, LoginActivity.class);
            setActivityStartAnimation(welcomeIntent);
            startActivity(welcomeIntent);
            finish();
        });
    }
    // Задаёт анимацию
    private void setActivityStartAnimation(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

}

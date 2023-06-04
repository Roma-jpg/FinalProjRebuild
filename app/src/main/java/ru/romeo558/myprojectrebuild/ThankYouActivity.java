package ru.romeo558.myprojectrebuild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ThankYouActivity extends AppCompatActivity {

    private Button closeButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        closeButton = findViewById(R.id.close_button);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("everEntered", true);
                editor.apply();
                Intent welcomeIntent = new Intent(ThankYouActivity.this, LoginActivity.class);
                setActivityStartAnimation(welcomeIntent);
                startActivity(welcomeIntent);
                finish();
            }
        });
    }
    private void setActivityStartAnimation(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

}

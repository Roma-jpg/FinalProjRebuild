package ru.romeo558.myprojectrebuild;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

public class SurveyActivity extends AppCompatActivity {
    private Button nextButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syrvey);

        // Set click listener for the "Next" button
        nextButton = findViewById(R.id.submit_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the ThankYouActivity
                Intent intent = new Intent(SurveyActivity.this, ThankYouActivity.class);
                setActivityStartAnimation(intent);
                startActivity(intent);
                finish();
            }
        });
    }
    private void setActivityStartAnimation(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}

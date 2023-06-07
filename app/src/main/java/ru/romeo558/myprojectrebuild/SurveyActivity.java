package ru.romeo558.myprojectrebuild;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SurveyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syrvey);

        // Установка слушателя для кнопки "Далее"
        Button nextButton = findViewById(R.id.submit_button);
        nextButton.setOnClickListener(v -> {
            // Переход к ThankYouActivity
            Intent intent = new Intent(SurveyActivity.this, ThankYouActivity.class);
            setActivityStartAnimation(intent);
            startActivity(intent);
            finish();
        });
    }
    // Задаёт анимацию
    private void setActivityStartAnimation(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}

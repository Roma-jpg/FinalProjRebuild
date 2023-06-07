package ru.romeo558.myprojectrebuild;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;

public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView pepTalkTextView = findViewById(R.id.greetings);

        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        int currentMonth = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentMonth = currentDate.getMonthValue();
        }

        String pepTalkMessage; // Сделал интересную штуку - если пользователь скачал приложение в сентябре или октябре, то текст меняется.
        if (currentMonth == 9 || currentMonth == 10) {
            pepTalkMessage = "Вот и начался учебный год. Я уверен, что это приложение поможет вам в вашем пути и начинаниях и всё также легко и быстро будет показывать вам ваше домашнее задание.";
        } else {
            pepTalkMessage = "Приветствуем вас в моём приложении - RomeoDiary. \nЭто приложение легко и быстро покажет вам какое домашнее задание вам задали на сегодня и какие оценки вы получили.";
        }
        pepTalkTextView.setText(pepTalkMessage);

        // Слушатель кнопки startButton
        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> {
            Intent surveyIntent = new Intent(WelcomeActivity.this, SurveyActivity.class);
            setActivityStartAnimation(surveyIntent);
            startActivity(surveyIntent);
            finish();
        });
    }

    // Задаёт анимацию
    private void setActivityStartAnimation(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

}

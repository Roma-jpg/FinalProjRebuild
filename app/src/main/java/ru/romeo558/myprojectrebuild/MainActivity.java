package ru.romeo558.myprojectrebuild;
// Imports
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // Глобальные переменные
    private DrawerLayout drawerLayout;
    private ListView listView;


    @SuppressLint({"SimpleDateFormat", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("onCreate() method is called");

        // Проверка на доступность интернета
        if (!isInternetAvailable()) {
            System.out.println("The device is not connected to the Internet.");
            // Show a dialog fragment with a semi-transparent background
            NoInternetDialogFragment dialogFragment = new NoInternetDialogFragment();
            dialogFragment.setCancelable(false);
            dialogFragment.show(getSupportFragmentManager(), "no_internet_dialog");
        } else {
            System.out.println("Device has internet connection.");
        }

        // Получение разных данных из предыдущей активити (LoginActivity.java)
        boolean fromLogin = getIntent().getBooleanExtra("fromLogin", false);
        String login = getIntent().getStringExtra("login");
        String password = getIntent().getStringExtra("password");
        SimpleDateFormat dateFormatServer = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDateServer = dateFormatServer.format(new Date());
        Calendar calendar = Calendar.getInstance();
        listView = findViewById(R.id.myListView);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences sharedPreferences2 = getSharedPreferences("LoginActivity", Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences2.edit();
        editor.putString("selectedDate", currentDateServer);
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");

        // То место, где делается первый реквест на сервер и парсится ДЗ
        if (fromLogin) {
            if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)) {
                String selectedDate = sharedPreferences2.getString("selectedDate", "");
                RequestHW.sendRequest(savedEmail, savedPassword, selectedDate, new RequestHW.Callback() {
                    @Override
                    public void onSuccess(String response) {
                        String unescapedResult = unescapeUnicode(response);
                        System.out.println(unescapedResult);
                        System.out.println(selectedDate.split("-")[0]);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            populateListViewWithJsonResponse(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError() {
                        Toast.makeText(MainActivity.this, "Ошибка", Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                Toast.makeText(MainActivity.this, "Программа заметила, что у неё нет ваших сохранённых логина и пароля. Пожалуйста, пройдите регистрацию снова, удалив приложение и заново его установив.", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            String selectedDate = sharedPreferences2.getString("selectedDate", "");
            RequestHW.sendRequest(savedEmail, savedPassword, selectedDate, new RequestHW.Callback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        populateListViewWithJsonResponse(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError() {
                    Toast.makeText(MainActivity.this, "Ошибка", Toast.LENGTH_LONG).show();
                }
            });
        }

        // Чисто мера предосторожности. Мало ли какие юзеры бывают
        if (fromLogin) {
            if (TextUtils.isEmpty(login) || TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "Программа заметила, что у неё нет ваших сохранённых логина и пароля. Пожалуйста, пройдите регистрацию снова, удалив приложение и заново его установив.", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            System.out.println("-");
        }

        // Ещё одни переменные (Эти нужны для меню)
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.bringToFront();
        navigationView.requestLayout();

        navigationView.setNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            //Do nothing, so we already on MainActivity
                            break;
                        case R.id.nav_profile:
                            replaceFragmentAnimated(new InfoFragment());
                            break;
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                });

        toggle.setToolbarNavigationClickListener(v -> {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Кнопка для выбора даты и её переменные
        Button dateButton = findViewById(R.id.date_button);
        SimpleDateFormat dateFormatUno = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDateDos = dateFormatUno.format(new Date());
        dateButton.setText(currentDateDos);
        Calendar selectedDate = Calendar.getInstance();
        dateButton.setOnClickListener(v -> {
            System.out.println("dateButton onClick() method is called");
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.DialogTheme,
                    (view, year, month, dayOfMonth) -> {
                        // Сохранение выбранной даты в переменной
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String dateString = dateFormat.format(selectedDate.getTime());


                        SharedPreferences.Editor editor1 = sharedPreferences2.edit();
                        editor1.putString("selectedDate", dateString);
                        editor1.apply();

                        dateButton.setText(dateString);

                        String selectedDate1 = sharedPreferences2.getString("selectedDate", "");
                        RequestHW.sendRequest(savedEmail, savedPassword, dateString, new RequestHW.Callback() {
                            @Override
                            public void onSuccess(String response) {
                                String unescapedResult = unescapeUnicode(response);
                                System.out.println(unescapedResult);
                                System.out.println(selectedDate1.split("-")[0]);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    populateListViewWithJsonResponse(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError() {
                                Toast.makeText(MainActivity.this, "Ошибка", Toast.LENGTH_LONG).show();
                            }
                        });

                    },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            Window dialogWindow = datePickerDialog.getWindow();
            if (dialogWindow != null) {
                // Установка цвета фона окна диалога
                dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FDFCF8")));
                dialogWindow.setNavigationBarColor(Color.parseColor("#FDFCF8"));
            }
            datePickerDialog.show();
        });

        // Кнопка "поделиться"
        Button shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(v -> {
            String selectedDate12 = sharedPreferences2.getString("selectedDate", "");
            RequestHW.sendRequest(savedEmail, savedPassword, selectedDate12, new RequestHW.Callback() {
                @Override
                public void onSuccess(String response) throws JSONException {
                    String unescapedResult = unescapeUnicode(response);
                    String textToShare = shareHomework(unescapedResult, String.valueOf(day));
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                    startActivity(Intent.createChooser(sharingIntent, "Отправить ДЗ через:"));
                }

                @Override
                public void onError() {
                    Toast.makeText(MainActivity.this, "Ошибка", Toast.LENGTH_LONG).show();
                }
            });


        });

        // Обновление страницы (Этого в видеопрезентации не было, кстати)
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            System.out.println(selectedDate);


            String selectedDate13 = sharedPreferences2.getString("selectedDate", "");
            RequestHW.sendRequest(savedEmail, savedPassword, selectedDate13, new RequestHW.Callback() {
                @Override
                public void onSuccess(String response) {
                    String unescapedResult = unescapeUnicode(response);
                    System.out.println(unescapedResult);
                    System.out.println(selectedDate13.split("-")[0]);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        populateListViewWithJsonResponse(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError() {
                    Toast.makeText(MainActivity.this, "Ошибка", Toast.LENGTH_LONG).show();
                }
            });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        // Список с самим домашним заданием.
        ListView listView = findViewById(R.id.myListView);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Обработка щелчка на элементе списка
            showPopupMenu(view);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Обработка удерживания на элементе списка
            showPopupMenu(view);
            return true; // Возвращаем true, чтобы предотвратить вызов обычного щелчка
        });

    }

    // Обработчик нажатий меню
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        System.out.println("OnOptionsItemSelected() method is called");
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Handle clicks on the home/up button in the action bar
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Заменяет фрагмент с анимацией
    public void replaceFragmentAnimated(Fragment fragment) {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame_container_main, fragment);
        fragmentTransaction.commit();
    }


    // Проверяет наличие интернета
    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showPopupMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.show_content) {
                Toast.makeText(MainActivity.this, "Выбрана функция: Показать подробное содержание", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.hide_rating) {
                Toast.makeText(MainActivity.this, "Выбрана функция: Скрыть оценку", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        popupMenu.show();
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

    public void populateListViewWithJsonResponse(JSONObject jsonObject) throws JSONException {
        List<GetHW.Entry> entries = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("LoginActivity", Context.MODE_PRIVATE);
        String targetDay = sharedPreferences.getString("selectedDate", "");
        String studentName = jsonObject.getString("student_name").split(" ")[1];
        TextView textViewName = findViewById(R.id.student_name);
        textViewName.setText(studentName.toUpperCase());


        try {
            JSONArray diaryEntries = jsonObject.getJSONArray("diary_entries");
            for (int i = 0; i < diaryEntries.length(); i++) {
                JSONObject entryObject = diaryEntries.getJSONObject(i);
                String dayLabel = entryObject.getString("day_label");


                if (dayLabel.equalsIgnoreCase("Day " + targetDay.split("-")[0])) {
                    JSONArray entriesArray = entryObject.getJSONArray("entries");

                    for (int j = 0; j < entriesArray.length(); j++) {
                        JSONObject itemObject = entriesArray.getJSONObject(j);
                        String subject = itemObject.getString("subject");
                        String task = itemObject.getString("task");
                        String mark = itemObject.getString("mark");

                        if (!subject.isEmpty() || !task.isEmpty() || !mark.isEmpty()) {
                            GetHW.Entry entry = new GetHW.Entry(mark, subject, task);
                            entries.add(entry);
                        }
                    }

                    break; // Exit the loop after finding the target day
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GetHW.HomeworkAdapter adapter = new GetHW.HomeworkAdapter(this, entries);
        listView.setAdapter(adapter);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        listView.startAnimation(animation);

    }
    public String shareHomework(String json, String day) throws JSONException {
        StringBuilder result = new StringBuilder();

        JSONObject response = new JSONObject(json);
        JSONArray diaryEntries = response.getJSONArray("diary_entries");

        for (int i = 0; i < diaryEntries.length(); i++) {
            JSONObject entry = diaryEntries.getJSONObject(i);
            String dayLabel = entry.getString("day_label");

            if (dayLabel.equals(day)) {
                JSONArray entries = entry.getJSONArray("entries");

                result.append("Домашнее задание на ").append(dayLabel.split(" ")[1]).append(" число:\n");

                for (int j = 0; j < entries.length(); j++) {
                    JSONObject task = entries.getJSONObject(j);
                    String subject = task.getString("subject");
                    String taskDescription = task.getString("task");

                    if (!subject.isEmpty()) {
                        result.append(subject).append(": ").append(taskDescription).append("\n");
                    }
                }

                result.append("\n");
                break;
            }
        }
        Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();
        return result.toString();
    }

}
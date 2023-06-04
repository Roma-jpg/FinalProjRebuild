package ru.romeo558.myprojectrebuild;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import com.google.android.material.navigation.NavigationView;

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
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button shareButton;
    private String textToShare;
    private ListView listView;
    private Button dateButton;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("onCreate() method is called");

        if (!isInternetAvailable()) {
            System.out.println("The device is not connected to the Internet.");
            // Show a dialog fragment with a semi-transparent background
            NoInternetDialogFragment dialogFragment = new NoInternetDialogFragment();
            dialogFragment.setCancelable(false);
            dialogFragment.show(getSupportFragmentManager(), "no_internet_dialog");
        } else {
            System.out.println("Device has internet connection.");
        }
        boolean fromLogin = getIntent().getBooleanExtra("fromLogin", false);
        String login = getIntent().getStringExtra("login");
        String password = getIntent().getStringExtra("password");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Увеличиваем на 1, так как в Calendar.MONTH январь имеет значение 0
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (fromLogin) {
            if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)) {
                RequestHW.sendRequest(login, password, currentDate, new RequestHW.Callback() {
                    @Override
                    public void onSuccess(String response) {
                        String unescapedResult = unescapeUnicode(response);
                        System.out.println(unescapedResult);
                        Toast.makeText(MainActivity.this, "Программа отработала хорошо", Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String studentName = jsonObject.getString("student_name");
                            String[] nameParts = studentName.split(" ");
                            String middleName = nameParts[1];
                            TextView student = findViewById(R.id.student_name);
                            student.setText(middleName.toUpperCase());
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
            Toast.makeText(MainActivity.this, "Что-то произошло. Но не то, чего все ожидали.", Toast.LENGTH_SHORT).show();
        }

        if (fromLogin) {
            if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)) {

            } else {
                Toast.makeText(MainActivity.this, "Программа заметила, что у неё нет ваших сохранённых логина и пароля. Пожалуйста, пройдите регистрацию снова, удалив приложение и заново его установив.", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(MainActivity.this, "Что-то произошло. Но не то, чего все ожидали.", Toast.LENGTH_SHORT).show();
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.bringToFront();
        navigationView.requestLayout();

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                    }
                });

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        Button dateButton = findViewById(R.id.date_button);
        SimpleDateFormat dateFormatUno = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String currentDateDos = dateFormatUno.format(new Date());
        dateButton.setText(currentDateDos);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("dateButton onClick() method is called");
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                System.out.println("DatePicker is active.");
                                String dateString = String.format(Locale.getDefault(), "%02d.%02d.%04d",
                                        dayOfMonth, month + 1, year);
                                dateButton.setText(dateString);
                                System.out.printf("%d.%d.%d", dayOfMonth, month + 1, year);
                            }
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
            }
        });

        textToShare = "Example text to send via messengers and others.";
        shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                sharingIntent.setType("text/plain");
//                sharingIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
//                startActivity(Intent.createChooser(sharingIntent, "Отправить ДЗ через:"));
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                setActivityStartAnimation(loginIntent);
                startActivity(loginIntent);
                finish();
            }
        });
//        String newName = getIntent().getStringExtra("student_name");
//        TextView student_name = findViewById(R.id.student_name);
//        student_name.setText(newName);
//
//        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//
//        String jsonResponse = getIntent().getStringExtra("json_response");
//        String hasStored = getIntent().getStringExtra("hasCred");

//        boolean run = true;
//        if (jsonResponse == null && Objects.equals(hasStored, "true")) {
//            run = false;
//
//            SharedPreferences sharedPreferences = getSharedPreferences("LoginActivity", Context.MODE_PRIVATE);
//            String login = sharedPreferences.getString("login", "");
//            String password = sharedPreferences.getString("password", "");
//
//            // Создаем объект SimpleDateFormat для форматирования даты
//            dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//
//            // Получаем сегодняшнюю дату
//            Calendar calendar = Calendar.getInstance();
//            String today = dateFormat.format(calendar.getTime());
//
//            RequestHW request = new RequestHW();
//            request.execute(login, password, today);
//        }
//        if (run){
//
//            try {
//                JSONObject jsonObject = new JSONObject(jsonResponse);
//                GetHW getHW = new GetHW(jsonObject);
//                List<GetHW.DayEntry> diaryEntries = getHW.diaryEntries;
//
//                List<GetHW.Entry> homeworkEntries = new ArrayList<>();
//                for (GetHW.DayEntry dayEntry : diaryEntries) {
//                    if (dayEntry.dayLabel.equals("11")) { // Update to access dayLabel property
//                        homeworkEntries.addAll(dayEntry.entries);
//                    }
//                }
//
//                // Remove empty rows
//                List<GetHW.Entry> nonEmptyEntries = new ArrayList<>();
//                for (GetHW.Entry entry : homeworkEntries) {
//                    if (!TextUtils.isEmpty(entry.getSubject()) || !TextUtils.isEmpty(entry.getTask())) {
//                        nonEmptyEntries.add(entry);
//                    }
//                }
//
//                GetHW.HomeworkAdapter adapter = new GetHW.HomeworkAdapter(this, nonEmptyEntries);
//                listView.setAdapter(adapter);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        ListView listView = findViewById(R.id.myListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Обработка щелчка на элементе списка
                // Отображение всплывающего меню здесь
                showPopupMenu(view);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Обработка удерживания на элементе списка
                // Отображение всплывающего меню здесь
                showPopupMenu(view);
                return true; // Возвращаем true, чтобы предотвратить вызов обычного щелчка
            }
        });

    }

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

    public void replaceFragment(Fragment fragment) {
        System.out.println("replaceFragment() is called.");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container_main, fragment);
        fragmentTransaction.commit();
    }

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

    private void setActivityStartAnimation(Intent intent) {
        drawerLayout.closeDrawer(GravityCompat.START);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }


    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showPopupMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.show_content) {
                    Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.hide_rating) {
                    // Действие для пункта "Скрыть оценку"
                    return true;
                }
                return false;
            }
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
}
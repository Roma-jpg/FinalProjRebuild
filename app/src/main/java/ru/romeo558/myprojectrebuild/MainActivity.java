package ru.romeo558.myprojectrebuild;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        dateButton.setText(currentDate);

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
        String newName = getIntent().getStringExtra("student_name");
        TextView student_name = findViewById(R.id.student_name);
        student_name.setText(newName);

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        String jsonResponse = getIntent().getStringExtra("json_response");
        ListView listView = findViewById(R.id.myListView);
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            GetHW getHW = new GetHW(jsonObject);
            List<GetHW.DayEntry> diaryEntries = getHW.diaryEntries;

            List<GetHW.Entry> homeworkEntries = new ArrayList<>();
            for (GetHW.DayEntry dayEntry : diaryEntries) {
                if (dayEntry.dayLabel.equals("11")) { // Update to access dayLabel property
                    homeworkEntries.addAll(dayEntry.entries);
                }
            }

            // Remove empty rows
            List<GetHW.Entry> nonEmptyEntries = new ArrayList<>();
            for (GetHW.Entry entry : homeworkEntries) {
                if (!TextUtils.isEmpty(entry.getSubject()) || !TextUtils.isEmpty(entry.getTask())) {
                    nonEmptyEntries.add(entry);
                }
            }

            GetHW.HomeworkAdapter adapter = new GetHW.HomeworkAdapter(this, nonEmptyEntries);
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }





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

}
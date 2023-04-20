package ru.romeo558.myprojectrebuild;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

// Set the item selected listener on the NavigationView
        // Set the item selected listener on the NavigationView
        navigationView.setNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            // Show toast with "Home Fragment selected"
                            Toast.makeText(MainActivity.this, "Home Fragment selected", Toast.LENGTH_SHORT).show();
                            System.out.println("Home Fragment selected");
                            break;
                        case R.id.nav_settings:
                            // Show toast with "Settings Fragment selected"
                            Toast.makeText(MainActivity.this, "Settings Fragment selected", Toast.LENGTH_SHORT).show();
                            System.out.println("Settings Fragment selected");
                            break;
                        case R.id.nav_profile:
                            // Show toast with "Info Fragment selected"
                            Toast.makeText(MainActivity.this, "Info Fragment selected", Toast.LENGTH_SHORT).show();
                            System.out.println("Info Fragment selected");
                            break;
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                });



        // Add a hamburger icon to the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set up click listener for hamburger icon in toolbar
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
                // Открытие диалога выбора даты
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Обновление текста кнопки с выбранной датой
                                String dateString = String.format(Locale.getDefault(), "%02d.%02d.%04d",
                                        dayOfMonth, month + 1, year);
                                dateButton.setText(dateString);
                            }
                        },
                        // Установка текущей даты в качестве значения по умолчанию
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        System.out.println("OnOptionsItemSelected method is called!!!!!!!");
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container_main, fragment);
        fragmentTransaction.commit();
    }

}
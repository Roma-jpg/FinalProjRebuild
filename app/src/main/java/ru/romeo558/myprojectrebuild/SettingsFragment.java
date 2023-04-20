package ru.romeo558.myprojectrebuild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import ru.romeo558.myprojectrebuild.R;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Set up menu item click listeners
        NavigationView navigationView = getActivity().findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        // Handle navigation item clicks here
                        switch (item.getItemId()) {
                            case R.id.nav_home:
                                ((MainActivity) requireActivity()).getSupportFragmentManager().popBackStack();
                                break;
                            case R.id.nav_settings:
                                // Start SettingsActivity
//                                startActivity(new Intent(getActivity(), SettingsActivity.class));
                                break;
                            case R.id.nav_profile:
                                // Start InfoActivity
//                                startActivity(new Intent(getActivity(), InfoActivity.class));
                                break;
                        }

                        // Close the navigation drawer when an item is clicked
                        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });

        return view;
    }
}
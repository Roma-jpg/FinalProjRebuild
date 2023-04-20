package ru.romeo558.myprojectrebuild;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class InfoFragment extends Fragment {

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        // Set up menu item click listeners
        NavigationView navigationView = requireActivity().findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        // Handle navigation item clicks here
                        switch (item.getItemId()) {
                            case R.id.nav_home:
                                ((MainActivity) requireActivity()).getSupportFragmentManager().popBackStack();
                                // Start HomeActivity
//                                startActivity(new Intent(getActivity(), HomeActivity.class));
//                                getActivity().finish();
                                break;
                            case R.id.nav_settings:
                                // Start SettingsActivity
//                                startActivity(new Intent(getActivity(), SettingsActivity.class));
//                                getActivity().finish();
                                break;
                            case R.id.nav_profile:
                                // Do nothing as we are already in InfoFragment
                                break;
                        }

                        // Close the navigation drawer when an item is clicked
                        DrawerLayout drawerLayout = requireActivity().findViewById(R.id.drawer_layout);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });

        return view;
    }
}
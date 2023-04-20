package ru.romeo558.myprojectrebuild;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Set up menu item click listeners
        NavigationView navigationView = getActivity().findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        // Handle navigation item clicks here
                        switch (item.getItemId()) {
                            case R.id.nav_home:
                                // Do nothing as we are already in HomeFragment
                                break;
                            case R.id.nav_settings:
                                // Start SettingsActivity
//                                startActivity(new Intent(getActivity(), SettingsActivity.class));
//                                getActivity().finish();
                                break;
                            case R.id.nav_profile:
                                // Start InfoActivity

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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container_main, fragment);
        fragmentTransaction.commit();
    }
}

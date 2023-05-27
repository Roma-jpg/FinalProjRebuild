package ru.romeo558.myprojectrebuild;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class InfoFragment extends Fragment {

    public InfoFragment() {
        // Required empty public constructor
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        NavigationView navigationView = requireActivity().findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            // Replace the current fragment with the HomeFragment
                            startActivity(new Intent(getActivity(),MainActivity.class));
                            requireActivity().finish();
                            break;
                        case R.id.nav_profile:
                            // Do nothing as we are already in InfoFragment
                            break;
                    }

                    DrawerLayout drawerLayout = requireActivity().findViewById(R.id.drawer_layout);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                });

        return view;
    }
    public void setFragmentStartAnimation(FragmentTransaction transaction) {
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left);
    }

}
package ru.romeo558.myprojectrebuild;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import ru.romeo558.myprojectrebuild.InfoFragment;
import ru.romeo558.myprojectrebuild.MainActivity;
import ru.romeo558.myprojectrebuild.R;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Set up menu item click listeners
        NavigationView navigationView = requireActivity().findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                item -> {
                    // Handle navigation item clicks here
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            requireActivity().finish();
                            break;
                        case R.id.nav_profile:
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame_container_main, new InfoFragment())
                                    .addToBackStack(null)
                                    .commit();
                            break;
                    }

                    // Close the navigation drawer when an item is clicked
                    DrawerLayout drawerLayout = requireActivity().findViewById(R.id.drawer_layout);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                });

        // Initialize views
        Switch errorSwitch = view.findViewById(R.id.error_switch);
        Switch mentalHelpSwitch = view.findViewById(R.id.mental_help_switch);
        RadioGroup themeRadioGroup = view.findViewById(R.id.theme_radio_group);

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Load saved preferences and set UI
        boolean showError = sharedPreferences.getBoolean("show_error", true);
        errorSwitch.setChecked(showError);

        boolean enableMentalHelp = sharedPreferences.getBoolean("enable_mental_help", false);
        mentalHelpSwitch.setChecked(enableMentalHelp);

        int theme = sharedPreferences.getInt("theme", R.id.light_theme_radio_button);
        themeRadioGroup.check(theme);

        // Add listeners to save updated preferences
        errorSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("show_error", isChecked);
            editor.apply();
        });

        mentalHelpSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("enable_mental_help", isChecked);
            editor.apply();
        });

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("theme", checkedId);
            editor.apply();

            // Apply theme change
            if (checkedId == R.id.light_theme_radio_button) {
                requireActivity().setTheme(R.style.AppTheme_Light);
            } else if (checkedId == R.id.dark_theme_radio_button) {
                requireActivity().setTheme(R.style.AppTheme_Discord);
            }

            // Recreate the activity for the theme change to take effect
            requireActivity().recreate();
        });

        return view;
    }

    public void setFragmentStartAnimation(FragmentTransaction transaction) {
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left);
    }
}

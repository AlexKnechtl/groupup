package com.example.alexander.groupup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.alexander.groupup.Fragments.HomeFragment;
import com.example.alexander.groupup.Fragments.NotificationFragment;
import com.example.alexander.groupup.Fragments.ProfileFragment;
import com.example.alexander.groupup.Fragments.SearchFragment;
import com.example.alexander.groupup.Fragments.SettingsFragment;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigationView;
    private FrameLayout frameLayout;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private ProfileFragment profileFragment;
    private SearchFragment searchFragment;
    private SettingsFragment settingsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find IDs
        navigationView = findViewById(R.id.bottom_nav);
        frameLayout = findViewById(R.id.main_frame);

        //Set Fragments
        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        searchFragment = new SearchFragment();
        profileFragment = new ProfileFragment();
        settingsFragment = new SettingsFragment();

        setFragment(homeFragment);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.nav_home :

                        setFragment(homeFragment);
                        return true;

                    case R.id.nav_notification :

                        setFragment(notificationFragment);
                        return true;

                    case R.id.nav_search :

                        setFragment(searchFragment);
                        return true;

                    case R.id.nav_profile :

                        setFragment(profileFragment);
                        return true;

                    case R.id.nav_settings :

                        setFragment(settingsFragment);
                        return true;
                    default:
                        return false;

                }
            }
        });
    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment).commit();
    }

    long lastPress = 0;

    @Override
    public void onBackPressed() {

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastPress > 5000) {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG).show();
            lastPress = currentTime;
        } else {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    }
}

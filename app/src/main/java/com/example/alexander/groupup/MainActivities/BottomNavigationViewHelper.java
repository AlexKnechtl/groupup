package com.example.alexander.groupup.MainActivities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.example.alexander.groupup.MainActivities.HomeActivity;
import com.example.alexander.groupup.MainActivities.NotificationActivity;
import com.example.alexander.groupup.MainActivities.ProfileActivity;
import com.example.alexander.groupup.MainActivities.SearchActivity;
import com.example.alexander.groupup.MainActivities.SettingsActivity;
import com.example.alexander.groupup.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx viewEx) {
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent intent = new Intent(context, HomeActivity.class);
                        context.startActivity(intent);
                        break;
                    case R.id.nav_notification:
                        Intent intent2 = new Intent(context, NotificationActivity.class);
                        context.startActivity(intent2);
                        break;
                    case R.id.nav_search:
                        Intent intent3 = new Intent(context, SearchActivity.class);
                        context.startActivity(intent3);
                        break;
                    case R.id.nav_profile:
                        Intent intent4 = new Intent(context, ProfileActivity.class);
                        context.startActivity(intent4);
                        break;
                    case R.id.nav_settings:
                        Intent intent5 = new Intent(context, SettingsActivity.class);
                        context.startActivity(intent5);
                        break;
                }

                return false;
            }
        });
    }

}

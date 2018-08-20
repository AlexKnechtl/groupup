package com.example.alexander.groupup.main;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.main.ChatActivity;
import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.main.NotificationActivity;
import com.example.alexander.groupup.main.ProfileActivity;
import com.example.alexander.groupup.main.SearchActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, final String user_id, BottomNavigationViewEx viewEx) {
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent intent = new Intent(context, HomeActivity.class);
                        ActivityOptions options =
                                ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("user_id", user_id);
                        context.startActivity(intent, options.toBundle());
                        break;
                    case R.id.nav_notification:
                        Intent intent2 = new Intent(context, NotificationActivity.class);
                        ActivityOptions options1 =
                                ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent2.putExtra("user_id", user_id);
                        context.startActivity(intent2, options1.toBundle());
                        break;
                    case R.id.nav_search:
                        Intent intent3 = new Intent(context, SearchActivity.class);
                        ActivityOptions options2 =
                                ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent3.putExtra("user_id", user_id);
                        context.startActivity(intent3, options2.toBundle());
                        break;
                    case R.id.nav_profile:
                        Intent intent4 = new Intent(context, ProfileActivity.class);
                        ActivityOptions options3 =
                                ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out);
                        intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent4.putExtra("user_id", user_id);
                        context.startActivity(intent4, options3.toBundle());
                        break;
                    case R.id.nav_settings:
                        Intent intent5 = new Intent(context, ChatActivity.class);
                        ActivityOptions options4 =
                                ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out);
                        intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent5.putExtra("user_id", user_id);
                        context.startActivity(intent5, options4.toBundle());
                        break;
                }
                return false;
            }
        });
    }
}
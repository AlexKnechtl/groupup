package com.example.alexander.groupup.MainActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.example.alexander.groupup.Fragments.FriendNotifyFragment;
import com.example.alexander.groupup.Fragments.GroupNotifiyFragment;
import com.example.alexander.groupup.Fragments.SectionsPagerAdapter;
import com.example.alexander.groupup.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class NotificationActivity extends AppCompatActivity {

    private Context mContext = NotificationActivity.this;
    private static final int ACTIVITY_NUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notification);

        setupBottomNavigationView();
        setupViewPager();
    }

    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FriendNotifyFragment());
        adapter.addFragment(new GroupNotifiyFragment());

        ViewPager viewPager = findViewById(R.id.container_viewpager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Friend Requests");
        tabLayout.getTabAt(1).setText("Group Requests");
    }

    public void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
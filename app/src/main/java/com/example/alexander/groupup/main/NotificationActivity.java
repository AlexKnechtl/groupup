package com.example.alexander.groupup.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.alexander.groupup.fragments.FriendNotifyFragment;
import com.example.alexander.groupup.fragments.GroupNotifiyFragment;
import com.example.alexander.groupup.fragments.SectionsPagerAdapter;
import com.example.alexander.groupup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationActivity extends AppCompatActivity {

    private Context mContext = NotificationActivity.this;
    private static final int ACTIVITY_NUM = 1;

    //XML
    private TextView dateTextView;

    //Firebase
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_notification);

        //if(FirebaseAuth.getInstance().getCurrentUser() == null);

        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("user_id");
        if(user_id == null) user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dateTextView = findViewById(R.id.date_notification);

        //Show Date in GroupCalendar
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MMM");
        String currentDate = formatter.format(new Date());
        dateTextView.setText(currentDate);

        setupBottomNavigationView();
        setupViewPager();
    }

    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GroupNotifiyFragment());
        adapter.addFragment(new FriendNotifyFragment());

        ViewPager viewPager = findViewById(R.id.container_viewpager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Groups");
        tabLayout.getTabAt(1).setText("People");
    }

    public void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext, user_id, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NotificationActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
package com.example.alexander.groupup.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.fragments.ChatsFragment;
import com.example.alexander.groupup.fragments.GroupChatsFragment;
import com.example.alexander.groupup.fragments.SectionsPagerAdapter;
import com.example.alexander.groupup.main.BottomNavigationViewHelper;
import com.example.alexander.groupup.main.HomeActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class MessagesActivity extends AppCompatActivity {

    private Context mContext = MessagesActivity.this;
    private static final int ACTIVITY_NUM = 4;

    //XML
    private MaterialSearchView searchView;


    //Variables

    //Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_chat);

        Toolbar myToolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(myToolbar);

        searchView = findViewById(R.id.material_search_view_chat);

        setupViewPager();
        setupBottomNavigationView();
    }

    public void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatsFragment());
        adapter.addFragment(new GroupChatsFragment());

        ViewPager viewPager = findViewById(R.id.container_viewpager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Chats");
        tabLayout.getTabAt(1).setText("Group Chats");
    }

    //Add Search Menu Icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.material_searchbar, menu);

        MenuItem item = menu.findItem(R.id.material_search_icon);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MessagesActivity.this, HomeActivity.class);
        startActivity(intent);
    }

}
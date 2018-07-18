package com.example.alexander.groupup.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.chat.NewMessage;
import com.example.alexander.groupup.fragments.ChatsFragment;
import com.example.alexander.groupup.fragments.GroupChatsFragment;
import com.example.alexander.groupup.fragments.SectionsPagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class ChatActivity extends AppCompatActivity {

    private Context mContext = ChatActivity.this;
    private static final int ACTIVITY_NUM = 4;

    //XML
    private MaterialSearchView searchView;
    private FloatingActionButton newChatFab;

    //Variables
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_chat);

        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("user_id");

        Toolbar myToolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(myToolbar);

        searchView = findViewById(R.id.material_search_view_chat);
        newChatFab = findViewById(R.id.new_chat_fab);

        setupViewPager();
        setupBottomNavigationView();
    }

    public void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext, user_id, bottomNavigationViewEx);

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

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                if (position == 0) {
                    newChatFab.setImageResource(R.drawable.baseline_person_add_white_36);
                } else if (position == 1) {
                    newChatFab.setImageResource(R.drawable.baseline_group_add_white_36);
                }
            }
        });

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Chats");
        tabLayout.getTabAt(1).setText("Group Chats");
    }

    public void newMessageClick(View view) {
        Intent intent = new Intent(ChatActivity.this, NewMessage.class);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
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
        Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
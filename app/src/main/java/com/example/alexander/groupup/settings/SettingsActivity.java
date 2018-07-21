package com.example.alexander.groupup.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.StartActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private Context mContext = SettingsActivity.this;
    private static final int ACTIVITY_NUM = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_settings);

        //Add the Toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    //Add the Logout Menu on the Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_logout_item:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingsActivity.this, StartActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void myAccountClick(View view) {
        Intent intent = new Intent(SettingsActivity.this, MyAccountActivity.class);
        startActivity(intent);
    }

    public void getPremiumClick(View view) {
        Intent intent = new Intent(SettingsActivity.this, GetPremium.class);
        startActivity(intent);
    }
}
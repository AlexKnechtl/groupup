package com.example.alexander.groupup.registration;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.singletons.LanguageStringsManager;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageStringsManager.initialize();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_username);

    }
}

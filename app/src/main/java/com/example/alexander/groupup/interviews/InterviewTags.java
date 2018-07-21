package com.example.alexander.groupup.interviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.main.HomeActivity;

public class InterviewTags extends AppCompatActivity {

    //XML
    private RelativeLayout backLayout;

    //Variables
    String group, activity, memberQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_tags);

        //Get Information by Intent
        Bundle bundle = getIntent().getExtras();
        //group = bundle.getString("group"); // Gruppen Kategorie
        //activity = bundle.getString("activity");

        //Find IDs
        backLayout = findViewById(R.id.back_layout_public);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewTags.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
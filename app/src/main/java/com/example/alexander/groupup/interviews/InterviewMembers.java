package com.example.alexander.groupup.interviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.R;

/**
 * Created by alexander on 20.03.18.
 */

public class InterviewMembers extends AppCompatActivity {

    //XML
    private LinearLayout backLayout;

    //Variables
    String group, activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_members);

        //Get Information by Intent
        Bundle bundle = getIntent().getExtras();
        group = bundle.getString("group"); // Gruppen Kategorie
        activity = bundle.getString("activity");

        //Find IDs
        backLayout = findViewById(R.id.back_layout_members);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewMembers.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }

    public void memberOptionOneClick(View view) {

        Intent intent = new Intent(InterviewMembers.this, InterviewPublic.class);
        intent.putExtra("group", group);
        intent.putExtra("activity", activity);
        intent.putExtra("memberQuantity", 3);
        startActivity(intent);
    }

    public void memberOptionTwoClick(View view) {

        Intent intent = new Intent(InterviewMembers.this, InterviewPublic.class);
        intent.putExtra("group", group);
        intent.putExtra("activity", activity);
        intent.putExtra("memberQuantity", 7);
        startActivity(intent);
    }

    public void memberOptionThreeClick(View view) {

        Intent intent = new Intent(InterviewMembers.this, InterviewPublic.class);
        intent.putExtra("group", group);
        intent.putExtra("activity", activity);
        intent.putExtra("memberQuantity", 10);
        startActivity(intent);
    }

    public void memberOptionFourClick(View view) {

        Intent intent = new Intent(InterviewMembers.this, InterviewPublic.class);
        intent.putExtra("group", group);
        intent.putExtra("activity", activity);
        intent.putExtra("memberQuantity", 15);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {

    }
}

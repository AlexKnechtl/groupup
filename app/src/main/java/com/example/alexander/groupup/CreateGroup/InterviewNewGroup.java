package com.example.alexander.groupup.CreateGroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.alexander.groupup.MainActivities.HomeActivity;
import com.example.alexander.groupup.R;

/**
 * Created by alexander on 18.03.18.
 */

public class InterviewNewGroup extends AppCompatActivity {

    //XML
    private LinearLayout backLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_newgroup);

        backLayout = findViewById(R.id.back_to_home);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewNewGroup.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    //OnClicks
    public void sportClick(View view) {
        Intent intent = new Intent(InterviewNewGroup.this, InterviewActivitySport.class);
        intent.putExtra("group", "sport");
        startActivity(intent);
    }

    public void leisureClick(View view) {
        Intent intent = new Intent(InterviewNewGroup.this, InterviewLeisure.class);
        intent.putExtra("group", "leisure");
        startActivity(intent);
    }

    public void nightlifeClick(View view) {
        Intent intent = new Intent(InterviewNewGroup.this, InterviewBusinessNightlife.class);
        intent.putExtra("group", "nightlife");
        startActivity(intent);
    }

    public void businessClick(View view) {
        Intent intent = new Intent(InterviewNewGroup.this, InterviewBusinessNightlife.class);
        intent.putExtra("group", "business");
        startActivity(intent);
    }
}
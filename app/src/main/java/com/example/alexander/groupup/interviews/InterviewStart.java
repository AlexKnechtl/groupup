package com.example.alexander.groupup.interviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.alexander.groupup.profile.FriendsActivity;
import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.R;

/**
 * Created by alexander on 18.03.18.
 */

public class InterviewStart extends AppCompatActivity {

    private LinearLayout newGroupLayout, sameGroupLayout, showFriendsLayout;
    private RelativeLayout backLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_start);

        backLayout = findViewById(R.id.back_layout_start);
        newGroupLayout = findViewById(R.id.new_group_layout);
        sameGroupLayout = findViewById(R.id.same_group_layout);
        showFriendsLayout = findViewById(R.id.show_friends_layout);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewStart.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        newGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewStart.this, InterviewNewGroup.class);
                startActivity(intent);
            }
        });

        sameGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewStart.this, InterviewNewGroup.class);
                startActivity(intent);
            }
        });

        showFriendsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewStart.this, FriendsActivity.class);
                startActivity(intent);
            }
        });
    }
}
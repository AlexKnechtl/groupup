package com.example.alexander.groupup.CreateGroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.alexander.groupup.MainActivities.HomeActivity;
import com.example.alexander.groupup.R;

/**
 * Created by alexander on 20.03.18.
 */

public class InterviewPublic extends AppCompatActivity {

    //XML
    private LinearLayout backLayout;

    //Variables
    String group, activity, memberQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_public);

        //Get Information by Intent
        Bundle bundle = getIntent().getExtras();
        group = bundle.getString("group"); // Gruppen Kategorie
        activity = bundle.getString("activity");
        memberQuantity = bundle.getString("memberQuantity");

        //Find IDs
        backLayout = findViewById(R.id.back_layout_public);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewPublic.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    public void justFriendsClick(View view) {

        Intent intent = new Intent(InterviewPublic.this, InterviewChoosePlace.class);
        intent.putExtra("group", group);
        intent.putExtra("activity", activity);
        intent.putExtra("memberQuantity", memberQuantity);
        intent.putExtra("publicStatus", "justfriends");
        startActivity(intent);
    }

    public void friendsOfFriendsClick(View view) {

        Intent intent = new Intent(InterviewPublic.this, InterviewChoosePlace.class);
        intent.putExtra("group", group);
        intent.putExtra("activity", activity);
        intent.putExtra("memberQuantity", memberQuantity);
        intent.putExtra("publicStatus", "friendsoffriends");
        startActivity(intent);
    }

    public void everybodyClick(View view) {

        Intent intent = new Intent(InterviewPublic.this, InterviewChoosePlace.class);
        intent.putExtra("group", group);
        intent.putExtra("activity", activity);
        intent.putExtra("memberQuantity", memberQuantity);
        intent.putExtra("publicStatus", "everybody");
        startActivity(intent);
    }

    public void privateGroupClick(View view) {

        Intent intent = new Intent(InterviewPublic.this, InterviewChoosePlace.class);
        intent.putExtra("group", group);
        intent.putExtra("activity", activity);
        intent.putExtra("memberQuantity", memberQuantity);
        intent.putExtra("publicStatus", "private");
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {

    }
}
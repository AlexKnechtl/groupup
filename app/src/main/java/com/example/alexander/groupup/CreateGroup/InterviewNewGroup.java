package com.example.alexander.groupup.CreateGroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.alexander.groupup.MainActivity;
import com.example.alexander.groupup.R;

/**
 * Created by alexander on 18.03.18.
 */

public class InterviewNewGroup extends AppCompatActivity {

    //XML
    private LinearLayout backLayout, sportsLayout, leisureLayout, partyLayout, productivityLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_newgroup);

        backLayout = findViewById(R.id.back_to_home);
        sportsLayout = findViewById(R.id.sports_layout);
        leisureLayout = findViewById(R.id.leisure_layout);
        partyLayout = findViewById(R.id.party_layout);
        productivityLayout = findViewById(R.id.productivity_layout);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewNewGroup.this, MainActivity.class);
                startActivity(intent);
            }
        });

        sportsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewNewGroup.this, InterviewActivitySport.class);
                intent.putExtra("group", getString(R.string.sport));
                startActivity(intent);
            }
        });

        leisureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewNewGroup.this, InterviewActivitySport.class);
                intent.putExtra("group", getString(R.string.leisure));
                startActivity(intent);
            }
        });

        partyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewNewGroup.this, InterviewActivitySport.class);
                intent.putExtra("group", getString(R.string.nightlife));
                startActivity(intent);
            }
        });

        productivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewNewGroup.this, InterviewActivitySport.class);
                intent.putExtra("group", getString(R.string.productivity));
                startActivity(intent);
            }
        });
    }
}

/*
                UserDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        state = dataSnapshot.child("state").getValue().toString();
                        city = dataSnapshot.child("city").getValue().toString();

                        GroupDatabase.child(state).child(current_uid).child("category").setValue("Sport");
                        GroupDatabase.child(state).child(current_uid).child("city").setValue(city);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
 */
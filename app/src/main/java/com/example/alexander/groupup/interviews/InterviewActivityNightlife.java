package com.example.alexander.groupup.interviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.main.HomeActivity;

public class InterviewActivityNightlife extends AppCompatActivity {

    //XML
    private RelativeLayout backLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_nightlife);

        backLayout = findViewById(R.id.back_layout_nightlife);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewActivityNightlife.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    //ToDO Add GroupImagesSystem for Nightlife
    public void nightclubClick(View view) {
        Intent intent = new Intent(InterviewActivityNightlife.this, InterviewPublic.class);
        intent.putExtra("group", "nightlife");
        intent.putExtra("activity", "nightclub");
        //intent.putExtra("group_image", new Integer(position).toString());
        startActivity(intent);
    }

    public void barPubClick(View view) {
        Intent intent = new Intent(InterviewActivityNightlife.this, InterviewPublic.class);
        intent.putExtra("group", "nightlife");
        intent.putExtra("activity", "barPub");
        //intent.putExtra("group_image", new Integer(position).toString());
        startActivity(intent);
    }

    public void hookahClick(View view) {
        Intent intent = new Intent(InterviewActivityNightlife.this, InterviewPublic.class);
        intent.putExtra("group", "nightlife");
        intent.putExtra("activity", "hookah");
        //intent.putExtra("group_image", new Integer(position).toString());
        startActivity(intent);
    }

    public void housepartyClick(View view) {
        Intent intent = new Intent(InterviewActivityNightlife.this, InterviewPublic.class);
        intent.putExtra("group", "nightlife");
        intent.putExtra("activity", "houseparty");
        //intent.putExtra("group_image", new Integer(position).toString());
        startActivity(intent);
    }
}
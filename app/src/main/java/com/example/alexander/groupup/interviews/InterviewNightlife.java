package com.example.alexander.groupup.interviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.main.HomeActivity;

public class InterviewNightlife extends AppCompatActivity {

    //XML
    private RelativeLayout backLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_nightlife);

        /*backLayout = findViewById(R.id.back_layout_new_group);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewNightlife.this, HomeActivity.class);
                startActivity(intent);
            }
        });*/
    }
}

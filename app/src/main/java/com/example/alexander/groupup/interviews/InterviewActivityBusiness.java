package com.example.alexander.groupup.interviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.main.HomeActivity;

public class InterviewActivityBusiness extends AppCompatActivity {

    //XML
    private RelativeLayout backLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_business);

        backLayout = findViewById(R.id.back_layout_business);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewActivityBusiness.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    //ToDO Add GroupImagesSystem for Business
    public void connectionGroupClick(View view) {
        Intent intent = new Intent(InterviewActivityBusiness.this, InterviewPublic.class);
        intent.putExtra("group", "business");
        intent.putExtra("activity", "connectionGroup");
        //intent.putExtra("group_image", new Integer(position).toString());
        startActivity(intent);
    }

    public void seminarClick(View view) {
        Intent intent = new Intent(InterviewActivityBusiness.this, InterviewPublic.class);
        intent.putExtra("group", "business");
        intent.putExtra("activity", "seminar");
        //intent.putExtra("group_image", new Integer(position).toString());
        startActivity(intent);
    }

    public void motivationClick(View view) {
        Intent intent = new Intent(InterviewActivityBusiness.this, InterviewPublic.class);
        intent.putExtra("group", "business");
        intent.putExtra("activity", "motivation");
        //intent.putExtra("group_image", new Integer(position).toString());
        startActivity(intent);
    }

    public void eventClick(View view) {
        Intent intent = new Intent(InterviewActivityBusiness.this, InterviewPublic.class);
        intent.putExtra("group", "business");
        intent.putExtra("activity", "event");
        //intent.putExtra("group_image", new Integer(position).toString());
        startActivity(intent);
    }
}
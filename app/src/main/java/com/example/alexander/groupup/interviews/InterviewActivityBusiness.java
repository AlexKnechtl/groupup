package com.example.alexander.groupup.interviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.models.GroupModel;

public class InterviewActivityBusiness extends BaseActivity {

    //XML
    private RelativeLayout backLayout;
    private GroupModel group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_business);
        group = (GroupModel) getIntent().getSerializableExtra("group");
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
        group.activity = "connection_group";
        intent.putExtra("group", group);
        //intent.putExtra("group_image", new Integer(position).toString());
        startActivity(intent);
    }

    public void seminarClick(View view) {
        Intent intent = new Intent(InterviewActivityBusiness.this, InterviewPublic.class);
        group.activity = "seminar";
        intent.putExtra("group", group);
        //intent.putExtra("group_image", new Integer(position).toString());
        startActivity(intent);
    }

    public void motivationClick(View view) {
        Intent intent = new Intent(InterviewActivityBusiness.this, InterviewPublic.class);
        group.activity = "motivation";
        intent.putExtra("group", group);
        //intent.putExtra("group_image", new Integer(position).toString());
        startActivity(intent);
    }

    public void eventClick(View view) {
        Intent intent = new Intent(InterviewActivityBusiness.this, InterviewPublic.class);
        group.activity = "event";
        intent.putExtra("group", group);
        //intent.putExtra("group_image", new Integer(position).toString());
        startActivity(intent);
    }
}
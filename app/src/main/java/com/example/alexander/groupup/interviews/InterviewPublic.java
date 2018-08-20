package com.example.alexander.groupup.interviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.models.GroupModel;
import com.example.alexander.groupup.models.PublicStatus;

import java.security.acl.Group;

/**
 * Created by alexander on 20.03.18.
 */

public class InterviewPublic extends BaseActivity {

    //Variables
    GroupModel group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_public);

        //Get Information by Intent
        Bundle bundle = getIntent().getExtras();
        group = (GroupModel) bundle.getSerializable("group"); // Gruppen Kategorie

        RelativeLayout backLayout;
        backLayout = findViewById(R.id.back_layout_public);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewPublic.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    public void requestsClick(View view) {
        Intent intent = new Intent(InterviewPublic.this, InterviewTags.class);
        group.public_status = PublicStatus.request;
        intent.putExtra("group", group);
        startActivity(intent);
    }

    public void everybodyClick(View view) {
        Intent intent = new Intent(InterviewPublic.this, InterviewTags.class);
        group.public_status = PublicStatus.open;
        intent.putExtra("group", group);
        startActivity(intent);
    }
}
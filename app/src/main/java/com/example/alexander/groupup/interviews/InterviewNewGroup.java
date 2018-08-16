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
import com.example.alexander.groupup.models.GroupType;

/**
 * Created by alexander on 18.03.18.
 */

public class InterviewNewGroup extends BaseActivity {

    //XML
    private RelativeLayout backLayout;
    private GroupModel group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = new GroupModel();
        setContentView(R.layout.interview_newgroup);

        backLayout = findViewById(R.id.back_layout_new_group);

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
        group.category = GroupType.sport;
        intent.putExtra("group", group);
        startActivity(intent);
    }

    public void leisureClick(View view) {
        Intent intent = new Intent(InterviewNewGroup.this, InterviewActivityLeisure.class);
        group.category = GroupType.leisure;
        intent.putExtra("group", group);
        startActivity(intent);
    }

    public void nightlifeClick(View view) {
        Intent intent = new Intent(InterviewNewGroup.this, InterviewActivityNightlife.class);
        group.category = GroupType.nightlife;
        intent.putExtra("group", group);
        startActivity(intent);
    }

    public void businessClick(View view) {
        Intent intent = new Intent(InterviewNewGroup.this, InterviewActivityBusiness.class);
        group.category = GroupType.business;
        intent.putExtra("group", group);
        startActivity(intent);
    }
}
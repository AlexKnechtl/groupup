package com.example.alexander.groupup.interviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.models.GroupModel;
import com.example.alexander.groupup.registration.RegisterAgeCity;
import com.example.alexander.groupup.registration.RegisterUsername;

import java.security.acl.Group;

public class InterviewTags extends BaseActivity {

    //XML
    private RelativeLayout backLayout;
    private EditText tag1EditText, tag2EditText, tag3EditText;

    //Variables
    private GroupModel group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_tags);

        getGroupInformation();

        //Find IDs
        backLayout = findViewById(R.id.back_layout_tags);
        tag1EditText = findViewById(R.id.group_tag_1_edit_tv);
        tag2EditText = findViewById(R.id.group_tag_2_edit_tv);
        tag3EditText = findViewById(R.id.group_tag_3_edit_tv);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewTags.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getGroupInformation() {
        Bundle bundle = getIntent().getExtras();
        group = (GroupModel) bundle.getSerializable("group");
    }

    public void addTagsClick(View view) {
        String tag1 = tag1EditText.getText().toString();
        String tag2 = tag2EditText.getText().toString();
        String tag3 = tag3EditText.getText().toString();

        if (TextUtils.isEmpty(tag1)) {
            tag1 = "empty";
        }

        if (TextUtils.isEmpty(tag2)) {
            tag2 = "empty";
        }

        if (TextUtils.isEmpty(tag3)) {
            tag3 = "empty";
        }

        Intent intent = new Intent(InterviewTags.this, InterviewChoosePlace.class);
        group.tag1 = tag1;
        group.tag2 = tag2;
        group.tag3 = tag3;
        intent.putExtra("group", group);
        startActivity(intent);
    }
}
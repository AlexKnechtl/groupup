package com.example.alexander.groupup.CreateGroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.alexander.groupup.Helpers.OnGetResultListener;
import com.example.alexander.groupup.MainActivity;
import com.example.alexander.groupup.Models.GroupImagesModel;
import com.example.alexander.groupup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by alexander on 20.03.18.
 */

public class InterviewDescription extends AppCompatActivity {

    //XML
    private LinearLayout backLayoutDescription;
    private RelativeLayout addDescription, noDescription;
    private EditText description;

    //Variables
    String group, activity, memberQuantity, publicStatus, location, state,groupDescription, current_uid;

    //Firebase
    private DatabaseReference mGroupDatabase;
    private FirebaseUser mCurrent_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_description);

        //Get Information by Intent
        Bundle bundle = getIntent().getExtras();
        group = bundle.getString("group"); // Gruppen Kategorie
        activity = bundle.getString("activity");
        //TODO Remove
        System.out.println("---------------"+activity);
        memberQuantity = bundle.getString("memberQuantity");
        publicStatus = bundle.getString("publicStatus");
        location = bundle.getString("location");
        state = bundle.getString("state");

        //Initialize Firebase
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrent_user.getUid();
        mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(state).child(current_uid);

        //Find IDs
        backLayoutDescription = findViewById(R.id.back_description);
        addDescription = findViewById(R.id.add_description);
        noDescription = findViewById(R.id.no_description);
        description = findViewById(R.id.edit_text_description);

        backLayoutDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewDescription.this, MainActivity.class);
                startActivity(intent);
            }
        });

        addDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                groupDescription = description.getText().toString();

                setDatabaseValues();

                Intent intent = new Intent(InterviewDescription.this, MainActivity.class);
                startActivity(intent);
            }
        });

        noDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                groupDescription = "Hier sollte eine Beschreibung stehen.";

                setDatabaseValues();

                Intent intent = new Intent(InterviewDescription.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setDatabaseValues() {
        if (group.equals("Sport"))
        {
            GroupImagesModel.getRandomImageURL(activity, new OnGetResultListener<String>() {
                @Override
                public void OnSuccess(String groupImage) {
                    mGroupDatabase.child("category").setValue(group);
                    mGroupDatabase.child("activity").setValue(activity);
                    mGroupDatabase.child("member_quantity").setValue(memberQuantity);
                    mGroupDatabase.child("public_status").setValue(publicStatus);
                    mGroupDatabase.child("location").setValue(location);
                    mGroupDatabase.child("description").setValue(groupDescription);
                    mGroupDatabase.child("tag").setValue(activity);
                    mGroupDatabase.child("members").child(current_uid).child("rank").setValue("creator");
                    mGroupDatabase.child("group_image").setValue(groupImage);
                }

                @Override
                public void OnFail() {
                    Toast.makeText(getBaseContext(), "Error saving Group", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
    }
}
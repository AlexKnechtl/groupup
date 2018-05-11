package com.example.alexander.groupup.CreateGroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
    String group, activity, memberQuantity, publicStatus, location, state;

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
        memberQuantity = bundle.getString("memberQuantity");
        publicStatus = bundle.getString("publicStatus");
        location = bundle.getString("location");
        state = bundle.getString("state");

        //Initialize Firebase
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        final String current_uid = mCurrent_user.getUid();
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

                String groupDescription = description.getText().toString();

                mGroupDatabase.child("category").setValue(group);
                mGroupDatabase.child("activity").setValue(activity);
                mGroupDatabase.child("member_quantity").setValue(memberQuantity);
                mGroupDatabase.child("public_status").setValue(publicStatus);
                mGroupDatabase.child("location").setValue(location);
                mGroupDatabase.child("description").setValue(groupDescription);
                mGroupDatabase.child("tag").setValue(activity);
                mGroupDatabase.child("members").child(current_uid).child("rank").setValue("creator");
                mGroupDatabase.child("group_image").setValue(getRandomImage(activity, group));

                Intent intent = new Intent(InterviewDescription.this, MainActivity.class);
                startActivity(intent);
            }
        });

        noDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mGroupDatabase.child("category").setValue(group);
                mGroupDatabase.child("activity").setValue(activity);
                mGroupDatabase.child("member_quantity").setValue(memberQuantity);
                mGroupDatabase.child("public_status").setValue(publicStatus);
                mGroupDatabase.child("location").setValue(location);
                mGroupDatabase.child("description").setValue("Hier sollte eine Beschreibung stehen.");
                mGroupDatabase.child("tag").setValue(activity);
                mGroupDatabase.child("members").child(current_uid).child("rank").setValue("creator");
                mGroupDatabase.child("group_image").setValue(getRandomImage(activity, group));

                Intent intent = new Intent(InterviewDescription.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private String getRandomImage(String activity, String category) {
        if (category.equals("Sport"))
        {
            return GroupImagesModel.getRandomImageURL(activity);
        }
        return null;
    }

    @Override
    public void onBackPressed() {
    }
}
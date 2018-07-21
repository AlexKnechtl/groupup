package com.example.alexander.groupup.interviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.helpers.OnGetResultListener;
import com.example.alexander.groupup.models.GroupImagesModel;
import com.example.alexander.groupup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by alexander on 20.03.18.
 */

public class InterviewDescription extends AppCompatActivity {

    //XML
    private RelativeLayout backLayoutDescription;
    private FloatingActionButton addDescription, noDescription;
    private EditText description;

    //Variables
    String group, activity, publicStatus, location,
            state, groupDescription, current_uid;

    //Firebase
    private DatabaseReference mGroupDatabase;
    private DatabaseReference UserDatabase;
    private FirebaseUser mCurrent_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_description);

        getGroupInformation();

        initialzieFirebase();

        findIDs();

        backLayoutDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewDescription.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        addDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                groupDescription = description.getText().toString();

                setDatabaseValues();
            }
        });

        noDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                groupDescription = "Hier sollte eine Beschreibung stehen.";

                //Intent intent = new Intent(InterviewDescription.this, HomeActivity.class);
                setDatabaseValues();
                //startActivity(intent);
            }
        });
    }

    private void setDatabaseValues() {
        if (group.equals("sport")) { //todo implement for all categories
            GroupImagesModel.getRandomImageURL(activity, new OnGetResultListener<String>() {
                @Override
                public void OnSuccess(String groupImage) {
                    mGroupDatabase.child("category").setValue(group);
                    mGroupDatabase.child("activity").setValue(activity);
                    mGroupDatabase.child("public_status").setValue(publicStatus);
                    mGroupDatabase.child("location").setValue(location);
                    mGroupDatabase.child("description").setValue(groupDescription);
                    mGroupDatabase.child("tag").setValue(activity);
                    mGroupDatabase.child("members").child(current_uid).child("rank").setValue("creator");
                    mGroupDatabase.child("group_image").setValue(groupImage);

                    UserDatabase.child("my_group").setValue(current_uid).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(InterviewDescription.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void OnFail() {
                    Toast.makeText(getBaseContext(), "Error saving Group", Toast.LENGTH_SHORT).show();
                }
            });
        }else
        { // Todo Freizeit: category = null, activity = null, memberquantity = null? ERROR!!!
            mGroupDatabase.child("category").setValue(group);
            mGroupDatabase.child("activity").setValue(activity);
            mGroupDatabase.child("public_status").setValue(publicStatus);
            mGroupDatabase.child("location").setValue(location);
            mGroupDatabase.child("description").setValue(groupDescription);
            mGroupDatabase.child("tag").setValue(activity);
            mGroupDatabase.child("members").child(current_uid).child("rank").setValue("creator");
            mGroupDatabase.child("group_image").setValue("https://firebasestorage.googleapis.com/v0/b/groupup-4c43f.appspot.com/o/Backgrounds%2FSport%2FLaufen-0.png?alt=media&token=2ebf9b5d-0931-4871-aa39-ef36ae5cac42");

            UserDatabase.child("my_group").setValue(current_uid).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent = new Intent(InterviewDescription.this, HomeActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void getGroupInformation() {
        Bundle bundle = getIntent().getExtras();
        group = bundle.getString("group"); // Gruppen Kategorie
        activity = bundle.getString("activity");
        publicStatus = bundle.getString("publicStatus");
        location = bundle.getString("location");
        state = bundle.getString("state"); // todo not used anymore
    }

    private void initialzieFirebase() {
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrent_user.getUid();

        mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(state).child(current_uid);
        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
    }

    private void findIDs() {
        backLayoutDescription = findViewById(R.id.back_layout_description);
        addDescription = findViewById(R.id.add_description);
        noDescription = findViewById(R.id.no_description);
        description = findViewById(R.id.edit_text_description);
    }

    @Override
    public void onBackPressed() { }
}
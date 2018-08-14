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
import com.example.alexander.groupup.models.GroupMember;
import com.example.alexander.groupup.models.GroupModel;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by alexander on 20.03.18.
 */

public class InterviewDescription extends AppCompatActivity {

    //XML
    private RelativeLayout backLayoutDescription;
    private FloatingActionButton addDescription, noDescription;
    private EditText description;

    //Variables
    private String category, activity, publicStatus, location,
            groupDescription, current_uid, tag1, tag2, tag3, latLng;

    private Double geofirelat, geofirelong;

    //Firebase
    private DatabaseReference mGroupDatabase;
    private DatabaseReference UserDatabase;
    private FirebaseUser mCurrent_user;

    GeoFire geoFire;

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

        if (category.equals("sport")) { //todo implement for all categories
            GroupImagesModel.getRandomSportImageURL(activity, new OnGetResultListener<String>() {
                @Override
                public void OnSuccess(String groupImage) {
                    mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(current_uid);


                    geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("GeoFire"));
                    geoFire.setLocation(current_uid, new GeoLocation(geofirelat, geofirelong), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {

                        }
                    });

                    HashMap<String, GroupMember> members =  new HashMap<String, GroupMember>();
                    members.put(current_uid, new GroupMember("creator"));
                    GroupModel m = new GroupModel(activity, location, tag1,tag2,tag3, groupImage, category, publicStatus, groupDescription, latLng, members);
                    mGroupDatabase.setValue(m);
//                    mGroupDatabase.child("category").setValue(category); //
//                    mGroupDatabase.child("activity").setValue(activity); //
//                    mGroupDatabase.child("public_status").setValue(publicStatus); //
//                    mGroupDatabase.child("location").setValue(location); //
//                    mGroupDatabase.child("description").setValue(groupDescription); //
//                    mGroupDatabase.child("tag1").setValue(tag1); //
//                    mGroupDatabase.child("tag2").setValue(tag2); //
//                    mGroupDatabase.child("tag3").setValue(tag3); //
//                    mGroupDatabase.child("latlng").setValue(latLng); //
//                    mGroupDatabase.child("member_count").setValue(1);
//                    mGroupDatabase.child("members").child(current_uid).child("rank").setValue("creator");
//                    mGroupDatabase.child("group_image").setValue(groupImage);

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
        }else { // Todo Freizeit: category = null, activity = null ERROR!!!

            GroupImagesModel.getRandomImageURL(activity, category, new OnGetResultListener<String>() {
                @Override
                public void OnSuccess(String value) {
                    mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(current_uid);

                    geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("GeoFire"));
                    geoFire.setLocation(current_uid, new GeoLocation(geofirelat, geofirelong), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {

                        }
                    });

                    HashMap<String, GroupMember> members =  new HashMap<String, GroupMember>();
                    members.put(current_uid, new GroupMember("creator"));
                    GroupModel m = new GroupModel(activity, location, tag1,tag2,tag3, value, category, publicStatus, groupDescription, latLng, members);
                    mGroupDatabase.setValue(m);

//                    mGroupDatabase.child("category").setValue(category);
//                    mGroupDatabase.child("activity").setValue(activity);
//                    mGroupDatabase.child("public_status").setValue(publicStatus);
//                    mGroupDatabase.child("location").setValue(location);
//                    mGroupDatabase.child("description").setValue(groupDescription);
//                    mGroupDatabase.child("tag1").setValue(tag1);
//                    mGroupDatabase.child("tag2").setValue(tag2);
//                    mGroupDatabase.child("tag3").setValue(tag3);
//                    mGroupDatabase.child("latlng").setValue(latLng);
//                    mGroupDatabase.child("members").child(current_uid).child("rank").setValue("creator");
//                    mGroupDatabase.child("group_image").setValue(value);
//                    mGroupDatabase.child("member_count").setValue(1);

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

                }
            });
        }
    }

    private void getGroupInformation() {
        Bundle bundle = getIntent().getExtras();
        category = bundle.getString("group");
        activity = bundle.getString("activity");
        publicStatus = bundle.getString("publicStatus");
        location = bundle.getString("location");
        tag1 = bundle.getString("tag1");
        tag2 = bundle.getString("tag2");
        tag3 = bundle.getString("tag3");
        latLng = bundle.getString("latlng");

        geofirelat = bundle.getDouble("geofirelat");
        geofirelong = bundle.getDouble("geofirelong");
    }

    private void initialzieFirebase() {
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrent_user.getUid();

        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
    }

    private void findIDs() {
        backLayoutDescription = findViewById(R.id.back_layout_description);
        addDescription = findViewById(R.id.add_description);
        noDescription = findViewById(R.id.no_description);
        description = findViewById(R.id.edit_text_description);
    }
}

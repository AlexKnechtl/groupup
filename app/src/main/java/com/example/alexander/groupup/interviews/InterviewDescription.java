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

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.helpers.OnGetResultListener;
import com.example.alexander.groupup.models.GroupImagesModel;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.models.GroupMember;
import com.example.alexander.groupup.models.GroupModel;
import com.example.alexander.groupup.models.GroupType;
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

public class InterviewDescription extends BaseActivity {

    //XML
    private RelativeLayout backLayoutDescription;
    private FloatingActionButton addDescription, noDescription;
    private EditText description;

    //Variables
    private GroupModel group;
    private String current_uid;
    private Double geofirelat, geofirelong;

    //Firebase
    private DatabaseReference mGroupDatabase;
    private DatabaseReference UserDatabase;
    private GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_description);

        //Find IDs
        backLayoutDescription = findViewById(R.id.back_layout_description);
        addDescription = findViewById(R.id.add_description);
        noDescription = findViewById(R.id.no_description);
        description = findViewById(R.id.edit_text_description);

        getGroupInformation();
        initialzieFirebase();

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
                group.description = description.getText().toString();
                setDatabaseValues();
            }
        });

        noDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.description = "Hier sollte eine Beschreibung stehen.";
                setDatabaseValues();
            }
        });
    }

    private void setDatabaseValues() {

        if (group.category == GroupType.sport) { //todo implement for all categories
            GroupImagesModel.getRandomSportImageURL(group.activity, new OnGetResultListener<String>() {
                @Override
                public void OnSuccess(String groupImage) {
                    mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(current_uid);

                    geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("GeoFire"));
                    geoFire.setLocation(current_uid, new GeoLocation(geofirelat, geofirelong), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {

                        }
                    });

                    HashMap<String, GroupMember> members = new HashMap<String, GroupMember>();
                    members.put(current_uid, new GroupMember("creator"));
                    group.group_image = groupImage;
                    group.members = members;
                    mGroupDatabase.setValue(group);

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
        } else { // Todo Freizeit: category = null, activity = null ERROR!!!

            GroupImagesModel.getRandomImageURL(group.activity, group.category, new OnGetResultListener<String>() {
                @Override
                public void OnSuccess(String value) {
                    mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(current_uid);

                    geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("GeoFire"));
                    geoFire.setLocation(current_uid, new GeoLocation(geofirelat, geofirelong), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {

                        }
                    });

                    HashMap<String, GroupMember> members = new HashMap<String, GroupMember>();
                    members.put(current_uid, new GroupMember("creator"));
                    group.members = members;
                    group.group_image = value;
                    mGroupDatabase.setValue(group);

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
        group = (GroupModel) bundle.getSerializable("group");

        geofirelat = bundle.getDouble("geofirelat");
        geofirelong = bundle.getDouble("geofirelong");
    }

    private void initialzieFirebase() {
        FirebaseUser mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrent_user.getUid();

        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
    }
}
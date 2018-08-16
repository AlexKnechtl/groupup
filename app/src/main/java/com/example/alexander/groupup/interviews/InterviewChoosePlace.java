package com.example.alexander.groupup.interviews;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.models.GroupModel;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.acl.Group;
import java.util.List;
import java.util.Locale;

public class InterviewChoosePlace extends BaseActivity {

    //Constants
    private final int REQUEST_CODE_PLACE_PICKER = 1;

    //Variables
    private static GroupModel group;
    private Double geofirelat, geofirelong;

    //XML
    private RelativeLayout backLayout;

    //Firebase Database
    private DatabaseReference UserDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_placepicker);

        backLayout = findViewById(R.id.back_layout_choose_place);

        //Initialize Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        //Get Information by Intent
        Bundle bundle = getIntent().getExtras();
        group = (GroupModel) bundle.getSerializable("group"); // Group Category

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewChoosePlace.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    public void choosePlaceClick (View view) {
        startPlacePickerActivity();
    }

    public void noPlaceClick (View view) {

        UserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String city = dataSnapshot.child("city").getValue().toString();

                Intent intent = new Intent(InterviewChoosePlace.this, InterviewTags.class);
                group.location = city;
                intent.putExtra("group", group);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startPlacePickerActivity() {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

        try {
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, REQUEST_CODE_PLACE_PICKER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_PLACE_PICKER && resultCode == RESULT_OK) {
            displaySelectedPlace(data);
        }
    }

    private void displaySelectedPlace(Intent data) {
        Place placeSelected = PlacePicker.getPlace(this, data);

        Double latitude = geofirelat = placeSelected.getLatLng().latitude;
        Double longitude = geofirelong = placeSelected.getLatLng().longitude;



        group.latlng = "geo:<" + latitude  + ">,<" + longitude + ">?q=<" + latitude  + ">,<" + longitude + ">("
                + getResources().getString(R.string.group_is_here) + ")";

        Geocoder geocoder = new Geocoder(this);

        final String placeName = placeSelected.getName().toString();

        UserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //TODO WTF soll DAS? Datasnapshot never used
                Intent intent = new Intent(InterviewChoosePlace.this, InterviewDescription.class);
                group.location = placeName;
                intent.putExtra("group", group);
                intent.putExtra("geofirelat", geofirelat);
                intent.putExtra("geofirelong", geofirelong);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
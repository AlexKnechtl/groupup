package com.example.alexander.groupup.interviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.R;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InterviewChoosePlace extends AppCompatActivity {

    //Constants
    private final int REQUEST_CODE_PLACE_PICKER = 1;

    //Variables
    private String group, activity, memberQuantity, publicStatus;

    //XML
    private LinearLayout backLayout;

    //Firebase Database
    private DatabaseReference UserDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_placepicker);

        backLayout = findViewById(R.id.back_place_picker);

        //Initialize Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        //Get Information by Intent
        Bundle bundle = getIntent().getExtras();
        group = bundle.getString("group"); // Group Category
        activity = bundle.getString("activity");
        memberQuantity = bundle.getString("memberQuantity");
        publicStatus = bundle.getString("publicStatus");

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
                //String state = dataSnapshot.child("state").getValue().toString(); //TODO correct

                Intent intent = new Intent(InterviewChoosePlace.this, InterviewDescription.class);
                intent.putExtra("group", group);
                intent.putExtra("activity", activity);
                intent.putExtra("memberQuantity", memberQuantity);
                intent.putExtra("publicStatus", "justfriends");
                intent.putExtra("location", city);
                intent.putExtra("state", "Steiermark");//state); //TODO Something is definitely wrong here, not used anymore
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

        final String placeName = placeSelected.getName().toString();

        UserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // String state = dataSnapshot.child("state").getValue().toString(); //Todo Something is wrong here

                Intent intent = new Intent(InterviewChoosePlace.this, InterviewDescription.class);
                intent.putExtra("group", group);
                intent.putExtra("activity", activity);
                intent.putExtra("memberQuantity", memberQuantity);
                intent.putExtra("publicStatus", publicStatus);
                intent.putExtra("location", placeName);
                intent.putExtra("state", "Steiermark"); //state); //todo correct this, not used anymore
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {

    }
}
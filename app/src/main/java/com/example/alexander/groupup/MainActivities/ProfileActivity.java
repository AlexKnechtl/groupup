package com.example.alexander.groupup.MainActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alexander.groupup.FriendsActivity;
import com.example.alexander.groupup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    //XML
    private ImageView mProfileImageView;
    private TextView mProfileName, mProfileLocation, mProfileStatus, friendsCounter;
    private RelativeLayout relativeLayout, languages;

    //Constants
    private static final int GALLERY_PICK = 1;
    private static final int ACTIVITY_NUM = 3;


    //Popup
    private String status;
    private PopupWindow mPopupWindow;

    //Firebase
    private DatabaseReference UserDatabase;
    private FirebaseUser mCurrentUser;

    private Context mContext = ProfileActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        setupBottomNavigationView();

        //Initialize Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String current_uid = mCurrentUser.getUid();

        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        UserDatabase.keepSynced(true);

        // Get the application context
        mContext = getApplicationContext();

        //Find IDs
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileImageView = findViewById(R.id.user_image);
        mProfileName = findViewById(R.id.profile_name_textview);

        mProfileLocation = findViewById(R.id.profile_location);
        relativeLayout = findViewById(R.id.coordinator_layout_profile);
        languages = findViewById(R.id.aboutme_languages);
        friendsCounter = findViewById(R.id.friends_counter_profile);

        UserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String location = dataSnapshot.child("city").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String age_day = dataSnapshot.child("age_day").getValue().toString();
                String age_year = dataSnapshot.child("age_year").getValue().toString();
                String friends_count = dataSnapshot.child("friends_count").getValue().toString();
                if (dataSnapshot.child("status").exists()) {
                    status = dataSnapshot.child("status").getValue().toString();
                    mProfileStatus.setText(status);
                }

                int age_day_value = Integer.parseInt(age_day);
                int age_year_value = Integer.parseInt(age_year);

                Calendar today = Calendar.getInstance();

                int age = today.get(Calendar.YEAR) - age_year_value;

                if (today.get(Calendar.DAY_OF_YEAR) < age_day_value) {
                    age--;
                }

                String ageUser = Integer.toString(age);
                UserDatabase.child("age").setValue(ageUser);

                mProfileName.setText(name + ", " + age);
                mProfileLocation.setText(location);
                friendsCounter.setText(friends_count);

                if (!image.equals("default")) {

                    Picasso.with(ProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_profile).into(mProfileImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.ic_profile).into(mProfileImageView);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        friendsCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FriendsActivity.class);
                intent.putExtra("user_id", current_uid);
                startActivity(intent);
            }
        });

        mProfileStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize a new instance of LayoutInflater service
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

                View customView = inflater.inflate(R.layout.popup_edittext, null);

                final EditText editStatus = customView.findViewById(R.id.edit_text_popup);
                final TextView countCharactersText = customView.findViewById(R.id.count_characters);

                if (status != null) {
                    editStatus.setText(status);
                    countCharactersText.setText(status.length() + " / 250");
                }

                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

                ImageButton closeButton = customView.findViewById(R.id.close_popup);

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        status = editStatus.getText().toString();
                        mProfileStatus.setText(status);
                        UserDatabase.child("status").setValue(status);
                        mPopupWindow.dismiss();
                    }
                });

                TextWatcher mTextEditorWatcher = new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        countCharactersText.setText(String.valueOf(s.length()) + " / 250");
                    }

                    public void afterTextChanged(Editable s) {
                    }
                };

                mPopupWindow.setFocusable(true);
                editStatus.addTextChangedListener(mTextEditorWatcher);

                mPopupWindow.showAtLocation(relativeLayout, Gravity.START, 0, 0);
            }
        });
    }

    public void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
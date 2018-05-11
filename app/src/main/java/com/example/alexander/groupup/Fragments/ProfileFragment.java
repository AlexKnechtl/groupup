package com.example.alexander.groupup.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    //XML
    private ImageView mProfileImageView;
    private TextView mProfileName, mProfileLocation, mProfileStatus, friendsCounter;
    private RelativeLayout relativeLayout, languages;

    //Constants
    private static final int GALLERY_PICK = 1;

    //Popup
    private String status;
    private Context mContext;
    private PopupWindow mPopupWindow;

    //Firebase
    private DatabaseReference UserDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference ProfileImageStorage;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        //Initialize Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String current_uid = mCurrentUser.getUid();

        ProfileImageStorage = FirebaseStorage.getInstance().getReference();
        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        UserDatabase.keepSynced(true);

        // Get the application context
        mContext = getActivity().getApplicationContext();

        //Find IDs
        mProfileStatus = view.findViewById(R.id.profile_status);
        mProfileImageView = view.findViewById(R.id.user_image);
        mProfileName = view.findViewById(R.id.profile_name_textview);

        mProfileLocation = view.findViewById(R.id.profile_location);
        relativeLayout = view.findViewById(R.id.coordinator_layout_profile);
        languages = view.findViewById(R.id.aboutme_languages);
        friendsCounter = view.findViewById(R.id.friends_counter_profile);

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

                    Picasso.with(getActivity()).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_profile).into(mProfileImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getActivity()).load(image).placeholder(R.drawable.ic_profile).into(mProfileImageView);
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
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
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

                if (Build.VERSION.SDK_INT >= 21) {
                    mPopupWindow.setElevation(5.0f);
                }

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
                        //This sets a textview to the current length
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

        return view;
    }
}

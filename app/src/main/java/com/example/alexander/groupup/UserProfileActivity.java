package com.example.alexander.groupup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by alexk on 01.03.2018.
 */

public class UserProfileActivity extends AppCompatActivity {

    //XML
    private TextView mDisplayNameAge, mDisplayLocation, mFabFriendText, mStatus, backExplorerText, friendsCounter;
    private CircleImageView mUserProfile;
    private FloatingActionButton mFriendFab, backExplorerFab;

    //Variables
    private String receiver_user_id;
    private String mCurrentState;

    private String userName, userThumbImage;
    private String myProfileName, myProfileThumbImage;
    private long friendsCountMyAccount, friendsCountUser;

    //Animations
    Animation FabOpen, FabClose;
    boolean fabIsOpen = false;

    //Firebase
    private DatabaseReference UserProfileDatabase;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference MyAccountDatabase;
    private DatabaseReference mNotificationDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        //Get Information by Intent
        receiver_user_id = getIntent().getStringExtra("user_id");

        //Firebase
        UserProfileDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(receiver_user_id);
        UserProfileDatabase.keepSynced(true);

        MyAccountDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Find IDs
        mDisplayNameAge = findViewById(R.id.user_name_age);
        mUserProfile = findViewById(R.id.user_image);
        mStatus = findViewById(R.id.user_status);
        mDisplayLocation = findViewById(R.id.user_location);
        friendsCounter = findViewById(R.id.friends_counter_userprofile);

        backExplorerText = findViewById(R.id.back_explorer_textview);
        backExplorerFab = findViewById(R.id.back_explorer_userprofile);
        mFabFriendText = findViewById(R.id.friend_fab_bubble);
        mFriendFab = findViewById(R.id.friend_button);

        //Animations
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        mCurrentState = "not_friends";

        UserProfileDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue().toString();
                String age_day = dataSnapshot.child("age_day").getValue().toString();
                String age_year = dataSnapshot.child("age_year").getValue().toString();
                String displayLocation = dataSnapshot.child("city").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                userThumbImage = dataSnapshot.child("thumb_image").getValue().toString();

                if (dataSnapshot.child("status").exists()) {
                    String status = dataSnapshot.child("status").getValue().toString();
                    mStatus.setText(status);
                }

                String friendCount = dataSnapshot.child("friends_count").getValue().toString();
                friendsCountUser = Long.parseLong(friendCount);

                int age_day_value = Integer.parseInt(age_day);
                int age_year_value = Integer.parseInt(age_year);

                Calendar today = Calendar.getInstance();

                int age = today.get(Calendar.YEAR) - age_year_value;

                if (today.get(Calendar.DAY_OF_YEAR) < age_day_value) {
                    age--;
                }

                String ageUser = Integer.toString(age);
                UserProfileDatabase.child("age").setValue(ageUser);

                mDisplayNameAge.setText(userName + ", " + age);
                mDisplayLocation.setText(displayLocation);
                friendsCounter.setText(friendCount);

                Picasso.with(UserProfileActivity.this).load(image).placeholder(R.drawable.default_user_black).into(mUserProfile);

                //Friends List / Request Feature
                mFriendRequestDatabase.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(receiver_user_id)) {
                            String req_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {
                                mFabFriendText.setText(getString(R.string.accept_friend));
                                mCurrentState = "req_received";

                            } else if (req_type.equals("sent")) {
                                mCurrentState = "req_sent";
                                mFabFriendText.setText(getString(R.string.cancel_friend_request));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                MyAccountDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String counter = dataSnapshot.child("friends_count").getValue().toString();
                        friendsCountMyAccount = Long.parseLong(counter);

                        if (dataSnapshot.child("friends").hasChild(receiver_user_id)) {
                            mCurrentState = "friends";
                            mFabFriendText.setText(getString(R.string.unfriend));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (mCurrentState.equals("not_friends")) {
            mFabFriendText.setText(getString(R.string.add_friend));
        }

        MyAccountDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myProfileName = dataSnapshot.child("name").getValue().toString();
                myProfileThumbImage = dataSnapshot.child("thumb_image").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //Friends Button is pressed
    public void addFriendClick(View view) {

        if (mCurrentState.equals("not_friends")) {

            mFriendRequestDatabase.child(mCurrentUser.getUid()).child(receiver_user_id).child("request_type").setValue("sent")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mFriendRequestDatabase.child(receiver_user_id).child(mCurrentUser.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String, String> notificationData = new HashMap<>();
                                        notificationData.put("from", mCurrentUser.getUid());
                                        notificationData.put("type", "request");

                                        mNotificationDatabase.child(receiver_user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mFabFriendText.setText(getString(R.string.cancel_friend_request));
                                                mCurrentState = "req_sent";
                                                Toast.makeText(UserProfileActivity.this, getString(R.string.sent_successfully), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(UserProfileActivity.this, getString(R.string.failed_sending_request), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        if (mCurrentState.equals("req_sent")) {

            mFriendRequestDatabase.child(mCurrentUser.getUid()).child(receiver_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mFriendRequestDatabase.child(receiver_user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFabFriendText.setText(getString(R.string.add_friend));
                            mCurrentState = "not_friends";
                            Toast.makeText(UserProfileActivity.this, getString(R.string.friend_request_canceled), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        if (mCurrentState.equals("req_received")) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            final String friendDate = day + "." + month + "." + year;

            String friendsMyAccount = Objects.toString(friendsCountMyAccount + 1 );
            final String friendsUser = Objects.toString(friendsCountUser + 1);

            MyAccountDatabase.child("friends_count").setValue(friendsMyAccount);
            MyAccountDatabase.child("friends").child(receiver_user_id).child("date").setValue(friendDate);
            MyAccountDatabase.child("friends").child(receiver_user_id).child("name").setValue(userName);
            MyAccountDatabase.child("friends").child(receiver_user_id).child("thumb_image").setValue(userThumbImage).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    UserProfileDatabase.child("friends_count").setValue(friendsUser);
                    UserProfileDatabase.child("friends").child(mCurrentUser.getUid()).child("date").setValue(friendDate);
                    UserProfileDatabase.child("friends").child(mCurrentUser.getUid()).child("name").setValue(myProfileName);
                    UserProfileDatabase.child("friends").child(mCurrentUser.getUid()).child("thumb_image").setValue(myProfileThumbImage)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(receiver_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendRequestDatabase.child(receiver_user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mCurrentState = "friends";
                                                    mFabFriendText.setText(getString(R.string.unfriend));
                                                    Toast.makeText(UserProfileActivity.this, getString(R.string.you_are_now_friends), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                }
            });
        }

        if (mCurrentState.equals("friends")) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(UserProfileActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
            builder.setTitle(getString(R.string.note))
                    .setMessage(getString(R.string.end_friendship))
                    .setPositiveButton(getString(R.string.yes_continue), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String friendsMyAccount = Objects.toString(friendsCountMyAccount - 1 );
                            final String friendsUser = Objects.toString(friendsCountUser - 1);

                            MyAccountDatabase.child("friends_count").setValue(friendsMyAccount);
                            UserProfileDatabase.child("friends_count").setValue(friendsUser);

                            MyAccountDatabase.child("friends").child(receiver_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    UserProfileDatabase.child("friends").child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mCurrentState = "not_friends";
                                            mFabFriendText.setText(getString(R.string.add_friend));
                                            Toast.makeText(UserProfileActivity.this, getString(R.string.no_friends_anymore), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .show();
        }
    }

    public void backHomeClick(View view) {
        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void assistantUserProfileClick(View view) {
        if (fabIsOpen) {
            mFriendFab.startAnimation(FabClose);
            mFabFriendText.startAnimation(FabClose);
            backExplorerFab.startAnimation(FabClose);
            backExplorerText.startAnimation(FabClose);
            mFriendFab.setClickable(false);
            backExplorerFab.setClickable(false);
            fabIsOpen = false;

        } else {
            mFriendFab.startAnimation(FabOpen);
            mFabFriendText.startAnimation(FabOpen);
            backExplorerFab.startAnimation(FabOpen);
            backExplorerText.startAnimation(FabOpen);
            mFriendFab.setClickable(true);
            backExplorerFab.setClickable(true);
            fabIsOpen = true;
        }
    }

    public void friendsClickUserProfile(View view) {
        Intent intent = new Intent(UserProfileActivity.this, FriendsActivity.class);
        intent.putExtra("user_id", receiver_user_id);
        startActivity(intent);
    }
}
package com.example.alexander.groupup.profile;

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

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.chat.SingleChat;
import com.example.alexander.groupup.main.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by alexk on 01.03.2018.
 */

public class UserProfileActivity extends BaseActivity {

    //XML
    private TextView mDisplayNameAge, mDisplayLocation, mFabFriendText, fabMessageBubble, mStatus, backExplorerText, friendsCounter, languages;
    private CircleImageView mUserProfile;
    private FloatingActionButton mFriendFab, backExplorerFab, messageFab;

    //Variables
    private String receiver_user_id;
    private String mCurrentState;

    private String userName, userThumbImage, userCity;
    private String myProfileName, myProfileThumbImage, myProfileAge, myProfileCity;
    private long friendsCountMyAccount, friendsCountUser;

    //Animations
    Animation FabOpen, FabClose;
    boolean fabIsOpen = false;

    //Firebase
    private DatabaseReference MyAccountDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference UserDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        //Get Information by Intent
        receiver_user_id = getIntent().getStringExtra("user_id");

        initializeFirebase();
        findIDs();

        //Animations
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        mCurrentState = "not_friends";

        UserDatabase.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue().toString();
                String age_day = dataSnapshot.child("age_day").getValue().toString();
                String age_year = dataSnapshot.child("age_year").getValue().toString();
                userCity = dataSnapshot.child("city").getValue().toString();
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
                UserDatabase.child(receiver_user_id).child("age").setValue(ageUser);

                mDisplayNameAge.setText(userName + ", " + age);
                mDisplayLocation.setText(userCity);
                friendsCounter.setText(friendCount);

                Picasso.with(UserProfileActivity.this).load(image).placeholder(R.drawable.profile_white_border).into(mUserProfile);

                checkFriendState();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(receiver_user_id).child("Languages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    languages.setText(dataSnapshot.getValue().toString());
                    if (languages.getText().equals(""))
                        languages.setText("Keine Sprachen");
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (mCurrentState.equals("not_friends")) {
            mFabFriendText.setText(getString(R.string.add_friend));
        }
    }

    //Friends Button is pressed
    public void addFriendClick(View view) {
        if (mCurrentState.equals("not_friends")) {
            MyAccountDatabase.child("friend_requests").child("sent").child(receiver_user_id).setValue("sent")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                HashMap<String, String> userData = new HashMap<>();
                                userData.put("name", myProfileName + ", " + myProfileAge);
                                userData.put("thumb_image", myProfileThumbImage);
                                userData.put("city", myProfileCity);

                                UserDatabase.child(receiver_user_id).child("friend_requests").child("received").child(mCurrentUser.getUid()).setValue(userData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Map notificationData = new HashMap();
                                                notificationData.put("from", mCurrentUser.getUid());
                                                notificationData.put("type", "friend_request");
                                                notificationData.put("time", ServerValue.TIMESTAMP);

                                                mNotificationDatabase.child(receiver_user_id).child(mCurrentUser.getUid()).setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
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

        if (mCurrentState.equals("request_sent")) {
            MyAccountDatabase.child("friend_requests").child("sent").child(receiver_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    UserDatabase.child(receiver_user_id).child("friend_requests").child("received").child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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

        if (mCurrentState.equals("request_received")) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            final String friendDate = day + "." + month + "." + year;

            String friendsMyAccount = Objects.toString(friendsCountMyAccount + 1);
            final String friendsUser = Objects.toString(friendsCountUser + 1);

            MyAccountDatabase.child("friends_count").setValue(friendsMyAccount);
            MyAccountDatabase.child("friends").child(receiver_user_id).child("date").setValue(friendDate);
            MyAccountDatabase.child("friends").child(receiver_user_id).child("name").setValue(userName);
            MyAccountDatabase.child("friends").child(receiver_user_id).child("city").setValue(userCity);
            MyAccountDatabase.child("friends").child(receiver_user_id).child("thumb_image").setValue(userThumbImage).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    UserDatabase.child(receiver_user_id).child("friends_count").setValue(friendsUser);
                    UserDatabase.child(receiver_user_id).child("friends").child(mCurrentUser.getUid()).child("city").setValue(myProfileCity);
                    UserDatabase.child(receiver_user_id).child("friends").child(mCurrentUser.getUid()).child("date").setValue(friendDate);
                    UserDatabase.child(receiver_user_id).child("friends").child(mCurrentUser.getUid()).child("name").setValue(myProfileName);
                    UserDatabase.child(receiver_user_id).child("friends").child(mCurrentUser.getUid()).child("thumb_image").setValue(myProfileThumbImage)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    MyAccountDatabase.child("friend_requests").child("received").child(receiver_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            UserDatabase.child(receiver_user_id).child("friend_requests").child("sent").child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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
            builder = new AlertDialog.Builder(UserProfileActivity.this, R.style.Theme_AppCompat_Dialog_Alert);
            builder.setTitle(getString(R.string.note))
                    .setMessage(getString(R.string.end_friendship))
                    .setPositiveButton(getString(R.string.yes_continue), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String friendsMyAccount = Objects.toString(friendsCountMyAccount - 1);
                            final String friendsUser = Objects.toString(friendsCountUser - 1);

                            MyAccountDatabase.child("friends_count").setValue(friendsMyAccount);
                            UserDatabase.child(receiver_user_id).child("friends_count").setValue(friendsUser);

                            MyAccountDatabase.child("friends").child(receiver_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    UserDatabase.child(receiver_user_id).child("friends").child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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
        Intent intent = new Intent(UserProfileActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void assistantUserProfileClick(View view) {
        if (fabIsOpen) {
            mFriendFab.startAnimation(FabClose);
            mFabFriendText.startAnimation(FabClose);
            backExplorerFab.startAnimation(FabClose);
            backExplorerText.startAnimation(FabClose);
            messageFab.startAnimation(FabClose);
            fabMessageBubble.startAnimation(FabClose);
            mFriendFab.setClickable(false);
            backExplorerFab.setClickable(false);
            messageFab.setClickable(false);
            fabIsOpen = false;

        } else {
            mFriendFab.startAnimation(FabOpen);
            mFabFriendText.startAnimation(FabOpen);
            backExplorerFab.startAnimation(FabOpen);
            backExplorerText.startAnimation(FabOpen);
            messageFab.startAnimation(FabOpen);
            fabMessageBubble.startAnimation(FabOpen);
            mFriendFab.setClickable(true);
            messageFab.setClickable(true);
            backExplorerFab.setClickable(true);
            fabIsOpen = true;
        }
    }

    public void friendsClickUserProfile(View view) {
        Intent intent = new Intent(UserProfileActivity.this, FriendsActivity.class);
        intent.putExtra("user_id", receiver_user_id);
        startActivity(intent);
    }

    public void sendMessageUserprofile(View view) {
        Intent intent = new Intent(UserProfileActivity.this, SingleChat.class);
        intent.putExtra("user_id", mCurrentUser.getUid());
        intent.putExtra("receiver_user_id", receiver_user_id);

        Map myUserMap = new HashMap<>();
        myUserMap.put("name", myProfileName);
        myUserMap.put("city", myProfileCity);
        myUserMap.put("thumb_image", myProfileThumbImage);

        UserDatabase.child(receiver_user_id).child("chats").child(mCurrentUser.getUid()).updateChildren(myUserMap);

        Map userMap = new HashMap<>();
        userMap.put("thumb_image", userThumbImage);
        userMap.put("name", userName);
        userMap.put("city", userCity);

        UserDatabase.child(mCurrentUser.getUid()).child("chats").child(receiver_user_id).updateChildren(userMap);
        startActivity(intent);
    }

    private void checkFriendState() {
        MyAccountDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myProfileName = dataSnapshot.child("name").getValue().toString();
                myProfileThumbImage = dataSnapshot.child("thumb_image").getValue().toString();
                myProfileAge = dataSnapshot.child("age").getValue().toString();
                myProfileCity = dataSnapshot.child("city").getValue().toString();

                String counter = dataSnapshot.child("friends_count").getValue().toString();
                friendsCountMyAccount = Long.parseLong(counter);

                if (dataSnapshot.child("friends").hasChild(receiver_user_id)) {
                    mCurrentState = "friends";
                    mFabFriendText.setText(getString(R.string.unfriend));
                    return;
                }

                if (dataSnapshot.child("friend_requests").child("sent").hasChild(receiver_user_id)) {
                    mCurrentState = "request_sent";
                    mFabFriendText.setText(getString(R.string.cancel_friend_request));
                    return;
                }

                if (dataSnapshot.child("friend_requests").child("received").hasChild(receiver_user_id)) {
                    mCurrentState = "request_received";
                    mFabFriendText.setText(getString(R.string.accept_friend));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initializeFirebase() {
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        MyAccountDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
    }

    private void findIDs() {
        mDisplayNameAge = findViewById(R.id.user_name_age);
        mUserProfile = findViewById(R.id.user_image);
        mStatus = findViewById(R.id.user_status);
        mDisplayLocation = findViewById(R.id.user_location);
        friendsCounter = findViewById(R.id.friends_counter_userprofile);

        backExplorerText = findViewById(R.id.back_explorer_textview);
        backExplorerFab = findViewById(R.id.back_explorer_userprofile);
        mFabFriendText = findViewById(R.id.friend_fab_bubble);
        messageFab = findViewById(R.id.message_fab);
        fabMessageBubble = findViewById(R.id.message_fab_bubble);
        mFriendFab = findViewById(R.id.friend_button);

        languages = findViewById(R.id.languages_interest_text_view);
    }
}
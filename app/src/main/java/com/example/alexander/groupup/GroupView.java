package com.example.alexander.groupup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.alexander.groupup.Models.UserModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupView extends AppCompatActivity {

    //XML
    private TextView headline, description, backHomeFabText, joinGroupFabText;
    private RecyclerView membersList;
    private FloatingActionButton backHomeFab, joinGroupFab;

    //Firebase
    private DatabaseReference GroupDatabase;
    private DatabaseReference GroupMemberDatabase;
    private DatabaseReference UserDatabase;
    private FirebaseUser mCurrentUser;

    //Variables
    private String state, groupId;

    //Animations
    Animation FabOpen, FabClose;
    boolean fabIsOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        //Get Information by Intent
        state = "Steiermark";
        groupId = getIntent().getStringExtra("group_id");

        //Initialize Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        GroupMemberDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(state).child(groupId).child("members");

        GroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(state).child(groupId);
        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        //Animations
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        findIDs();

        GroupDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String activity = dataSnapshot.child("activity").getValue().toString();
                String location = dataSnapshot.child("location").getValue().toString();

                headline.setText(activity + " @" + location);

                if (dataSnapshot.child("description").exists()) {

                    String descriptionText = dataSnapshot.child("description").getValue().toString();
                    description.setText(descriptionText);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<UserModel, MembersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserModel, MembersViewHolder>(
                        UserModel.class,
                        R.layout.single_layout_groupuser,
                        MembersViewHolder.class,
                        GroupMemberDatabase
                ) {

            @Override
            protected void populateViewHolder(MembersViewHolder viewHolder, UserModel user, int position) {

                final String list_user_id = getRef(position).getKey();

                MembersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GroupView.this, UserProfileActivity.class);
                        intent.putExtra("user_id", list_user_id);
                        startActivity(intent);
                    }
                });

                UserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("name").getValue().toString();
                        String userAge = dataSnapshot.child("age").getValue().toString();
                        String thumbImage = dataSnapshot.child("thumb_image").getValue().toString();
                        String city = dataSnapshot.child("city").getValue().toString();

                        MembersViewHolder.setNameAge(userName, userAge);
                        MembersViewHolder.setThumbImage(thumbImage, getApplicationContext());
                        MembersViewHolder.setCity(city);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        membersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder {

        static View mView;

        public MembersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public static void setNameAge(String name, String age) {
            TextView userNameView = mView.findViewById(R.id.username_groupview);
            userNameView.setText(name + ", " + age);
        }

        public static void setCity(String city) {
            TextView friendsDate = mView.findViewById(R.id.user_location_groupview);
            friendsDate.setText(city);
        }

        public static void setThumbImage(String thumb_image, Context context) {
            CircleImageView userImageView = mView.findViewById(R.id.user_picture);
            Picasso.with(context).load(thumb_image).placeholder(R.drawable.profile_white).into(userImageView);
        }
    }

    public void assistantGroupViewClick(View view) {
        if (fabIsOpen) {
            backHomeFab.startAnimation(FabClose);
            backHomeFabText.startAnimation(FabClose);
            joinGroupFab.startAnimation(FabClose);
            joinGroupFabText.startAnimation(FabClose);
            backHomeFab.setClickable(false);
            joinGroupFab.setClickable(false);
            fabIsOpen = false;

        } else {
            backHomeFab.startAnimation(FabOpen);
            backHomeFabText.startAnimation(FabOpen);
            joinGroupFab.startAnimation(FabOpen);
            joinGroupFabText.startAnimation(FabOpen);
            backHomeFab.setClickable(true);
            joinGroupFab.setClickable(true);
            fabIsOpen = true;
        }
    }

    public void sendGroupRequest(View view) {

    }

    private void findIDs() {
        //Fabs
        backHomeFab = findViewById(R.id.back_explorer_groupview);
        joinGroupFab = findViewById(R.id.join_group_fab);
        backHomeFabText = findViewById(R.id.back_explorer_text_groupview);
        joinGroupFabText = findViewById(R.id.join_group_fab_text);

        headline = findViewById(R.id.group_view_headline);
        description = findViewById(R.id.group_description);

        membersList = findViewById(R.id.member_list_group_view);
        membersList.setHasFixedSize(true);
        membersList.setLayoutManager(new LinearLayoutManager(this));
    }
}
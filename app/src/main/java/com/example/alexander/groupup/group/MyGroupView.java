package com.example.alexander.groupup.group;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.main.ProfileActivity;
import com.example.alexander.groupup.models.UserModel;
import com.example.alexander.groupup.profile.UserProfileActivity;
import com.example.alexander.groupup.singletons.LanguageStringsManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyGroupView extends AppCompatActivity {

    //XML
    private TextView headline, description, backHomeFabText, addMemberFabText, leaveGroupText, memberCount;
    private FloatingActionButton backHomeFab, addMemberFab, leaveGroupFab;
    private RecyclerView membersList;

    //FireBase
    private DatabaseReference GroupMemberDatabase;
    private DatabaseReference GroupDatabase;
    private DatabaseReference UserDatabase;

    //Variables
    private String groupId, latLng, user_id;
    boolean fabIsOpen = false;
    long groupMembers;

    //Animations
    private Animation FabOpen, FabClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        //Get Information by Intent
        groupId = getIntent().getStringExtra("group_id");
        user_id = getIntent().getStringExtra("user_id");

        initializeFireBase();
        findIDs();

        //Animations
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<UserModel, membersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserModel, membersViewHolder>(
                UserModel.class,
                R.layout.single_layout_groupuser,
                membersViewHolder.class,
                GroupMemberDatabase
        ) {

            @Override
            protected void populateViewHolder(final membersViewHolder viewHolder, UserModel user, int position) {

                final String list_user_id = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (list_user_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            startActivity(new Intent(MyGroupView.this, ProfileActivity.class));
                        } else {
                            Intent intent = new Intent(MyGroupView.this, UserProfileActivity.class);
                            intent.putExtra("user_id", list_user_id);
                            startActivity(intent);
                        }
                    }
                });

                UserDatabase.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("name").getValue().toString();
                        String userAge = dataSnapshot.child("age").getValue().toString();
                        String thumbImage = dataSnapshot.child("thumb_image").getValue().toString();
                        String city = dataSnapshot.child("city").getValue().toString();

                        viewHolder.setNameAge(userName, userAge);
                        viewHolder.setThumbImage(thumbImage, getApplicationContext());
                        viewHolder.setCity(city);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        membersList.setAdapter(firebaseRecyclerAdapter);
    }

    private static class membersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public membersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setNameAge(String name, String age) {
            TextView userNameView = mView.findViewById(R.id.username_groupview);
            userNameView.setText(name + ", " + age);
        }

        public void setCity(String city) {
            TextView friendsDate = mView.findViewById(R.id.user_location_groupview);
            friendsDate.setText(city);
        }

        public void setThumbImage(String thumb_image, Context context) {
            CircleImageView userImageView = mView.findViewById(R.id.user_picture);
            Picasso.with(context).load(thumb_image).placeholder(R.drawable.profile_white).into(userImageView);
        }
    }

    public void assistantGroupViewClick(View view) {
        if (fabIsOpen) {
            backHomeFab.startAnimation(FabClose);
            backHomeFabText.startAnimation(FabClose);
            addMemberFab.startAnimation(FabClose);
            addMemberFabText.startAnimation(FabClose);
            leaveGroupFab.startAnimation(FabClose);
            leaveGroupText.startAnimation(FabClose);
            leaveGroupFab.setClickable(false);
            backHomeFab.setClickable(false);
            addMemberFab.setClickable(false);
            fabIsOpen = false;

        } else {
            backHomeFab.startAnimation(FabOpen);
            backHomeFabText.startAnimation(FabOpen);
            addMemberFab.startAnimation(FabOpen);
            addMemberFabText.startAnimation(FabOpen);
            leaveGroupFab.startAnimation(FabOpen);
            leaveGroupText.startAnimation(FabOpen);
            leaveGroupFab.setClickable(true);
            backHomeFab.setClickable(true);
            addMemberFab.setClickable(true);
            fabIsOpen = true;
        }
    }

    private void findIDs() {
        backHomeFab = findViewById(R.id.back_home_my_group_view);
        addMemberFab = findViewById(R.id.add_member_fab);
        backHomeFabText = findViewById(R.id.back_home_text_my_group_view);
        addMemberFabText = findViewById(R.id.add_member_fab_text);
        memberCount = findViewById(R.id.members_my_group_view);
        leaveGroupFab = findViewById(R.id.leave_group_fab);
        leaveGroupText = findViewById(R.id.leave_group_fab_text);
        headline = findViewById(R.id.group_view_headline);
        description = findViewById(R.id.group_description);
        membersList = findViewById(R.id.member_list_group_view);
        membersList.setHasFixedSize(true);
        membersList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initializeFireBase() {
        GroupMemberDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("members");
        GroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId);
        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        GroupDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String activity = dataSnapshot.child("activity").getValue().toString();
                String location = dataSnapshot.child("location").getValue().toString();
                latLng = dataSnapshot.child("latlng").getValue().toString();

                headline.setText(LanguageStringsManager.getInstance().getLanguageStringByStringId(activity).getLocalLanguageString()
                        + " @" + location);

                String descriptionText = dataSnapshot.child("description").getValue().toString();
                description.setText(descriptionText);

                memberCount.setText(dataSnapshot.child("member_count").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //OnClicks
    public void locationClick(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setData(Uri.parse(latLng));
        startActivity(intent);
    }

    //FloatingActionButtons
    public void backGroupViewClick(View view) {
        Intent intent = new Intent(MyGroupView.this, HomeActivity.class);
        startActivity(intent);
    }

    public void addMemberClick(View view) {
        Intent intent = new Intent(MyGroupView.this, AddGroupMembersActivity.class);
        startActivity(intent);
    }

    public void leaveGroupClick(View view) {
        UserDatabase.child(user_id).child("my_group").removeValue();
        groupMembers--;
        GroupMemberDatabase.child(user_id).removeValue();

        GroupDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String members = dataSnapshot.child("member_count").getValue().toString();
                groupMembers = Long.parseLong(members);
                groupMembers--;

                if (groupMembers == 0) {
                    DatabaseReference GroupChatDatabase = FirebaseDatabase.getInstance().getReference().child("GroupChat").child(groupId);
                    GroupDatabase.removeValue();
                    UserDatabase.child(user_id).child("my_group").removeValue();
                    GroupChatDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(MyGroupView.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    });

                } else if (groupMembers > 0) {
                    UserDatabase.child(user_id).child("my_group").removeValue();
                    GroupMemberDatabase.child(user_id).removeValue();
                    GroupDatabase.child("member_count").setValue(groupMembers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
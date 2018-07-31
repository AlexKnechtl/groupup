package com.example.alexander.groupup.group;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.main.ProfileActivity;
import com.example.alexander.groupup.models.UserModel;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.profile.UserProfileActivity;
import com.example.alexander.groupup.singletons.LanguageStringsManager;
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
    private TextView headline, description, backHomeFabText, joinGroupFabText, memberCount;
    private RecyclerView membersList;
    private FloatingActionButton backHomeFab, joinGroupFab;

    //FireBase
    private DatabaseReference GroupDatabase;
    private DatabaseReference GroupMemberDatabase;
    private DatabaseReference UserDatabase;

    //Variables
    private String groupId, latLng;

    //Animations
    Animation FabOpen, FabClose;
    boolean fabIsOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        //Get Information by Intent
        groupId = getIntent().getStringExtra("group_id");

        initializeFirebase();
        findIDs();

        //Animations
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        backHomeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupView.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        joinGroupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupView.this, AddGroupMembersActivity.class);
                startActivity(intent);
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
            protected void populateViewHolder(final MembersViewHolder viewHolder, UserModel user, int position) {

                final String list_user_id = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(list_user_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        {
                            startActivity(new Intent(GroupView.this, ProfileActivity.class));
                        }
                        else {
                            Intent intent = new Intent(GroupView.this, UserProfileActivity.class);
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

    public static class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        View mView;

        public MembersViewHolder(View itemView) {
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

        //ToDo Add Menu Option when User is pressed: Chat, Show Profile, Rating, Report
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select The Action");
            menu.add(0, v.getId(), 0, "Send a message");//groupId, itemId, order, title
            menu.add(0, v.getId(), 0, "SMS");
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

    private void findIDs() {
        backHomeFab = findViewById(R.id.back_explorer_groupview);
        joinGroupFab = findViewById(R.id.join_group_fab);
        backHomeFabText = findViewById(R.id.back_explorer_text_groupview);
        joinGroupFabText = findViewById(R.id.join_group_fab_text);
        memberCount = findViewById(R.id.rating_profile);

        headline = findViewById(R.id.group_view_headline);
        description = findViewById(R.id.group_description);

        membersList = findViewById(R.id.member_list_group_view);
        membersList.setHasFixedSize(true);
        membersList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initializeFirebase() {

        GroupMemberDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("members");
        GroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId);
        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        GroupDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String activity = dataSnapshot.child("activity").getValue().toString();
                String location = dataSnapshot.child("location").getValue().toString();
                latLng = dataSnapshot.child("latlng").getValue().toString();

                headline.setText(LanguageStringsManager.getInstance().getLanguageStringByStringId(activity).getLocalLanguageString()
                        + " @" + location);

                if (dataSnapshot.child("description").exists()) {

                    String descriptionText = dataSnapshot.child("description").getValue().toString();
                    description.setText(descriptionText);
                }

                if(dataSnapshot.child("member_count").exists())
                    memberCount.setText(dataSnapshot.child("member_count").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void locationClick(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setData(Uri.parse(latLng));
        startActivity(intent);
    }
}
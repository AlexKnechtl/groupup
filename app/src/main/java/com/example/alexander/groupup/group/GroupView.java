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
import android.view.ContextMenu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.main.ProfileActivity;
import com.example.alexander.groupup.models.UserModel;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.profile.UserProfileActivity;
import com.example.alexander.groupup.singletons.LanguageStringsManager;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupView extends AppCompatActivity {

    //XML
    private TextView headline, description, backHomeFabText, joinGroupFabText, statusText, memberCount;
    private FloatingActionButton backHomeFab, joinGroupFab;
    private RecyclerView membersList;
    private ImageView statusIcon;

    //FireBase
    private DatabaseReference GroupMemberDatabase;
    private DatabaseReference GroupDatabase;
    private DatabaseReference UserDatabase;

    //Variables
    private String groupId, latLng, user_id;
    boolean fabIsOpen = false;
    boolean publicStatus = false;

    //Animations
    private Animation FabOpen, FabClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        //Get Information by Intent
        groupId = getIntent().getStringExtra("group_id");
        user_id = getIntent().getStringExtra("user_id");

        initializeFireBase();
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
                        if (list_user_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            startActivity(new Intent(GroupView.this, ProfileActivity.class));
                        } else {
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

    public static class MembersViewHolder extends RecyclerView.ViewHolder {

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
            Picasso.with(context).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_white).into(userImageView);
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
        backHomeFab = findViewById(R.id.back_home_group_view);
        joinGroupFab = findViewById(R.id.join_group_fab);
        backHomeFabText = findViewById(R.id.back_explorer_text_groupview);
        joinGroupFabText = findViewById(R.id.join_group_fab_text);
        memberCount = findViewById(R.id.members_group_view);
        statusIcon = findViewById(R.id.status_icon_group_view);
        statusText = findViewById(R.id.status_text_group_view);

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
                if (dataSnapshot.child("public_status").getValue().toString().equals("everybody")) {
                    publicStatus = true;

                    statusIcon.setImageResource(R.drawable.material_lock_open_white_36);
                    statusText.setText(R.string.open);
                    joinGroupFab.setImageResource(R.drawable.material_group_add_black_36);
                    joinGroupFabText.setText(R.string.join_group);
                }

                headline.setText(LanguageStringsManager.getInstance().getLanguageStringByStringId(activity).getLocalLanguageString()
                        + " @" + location);

                if (dataSnapshot.child("members").child(user_id).child("rank").exists()) {
                    dataSnapshot.child("members").child(user_id).child("rank").getValue().toString();
                }

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

    public void joinGroupClick(View view) {

        UserDatabase.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("my_group").exists()) {
                    Toast.makeText(GroupView.this, "Du bist bereits in einer Gruppe.", Toast.LENGTH_SHORT).show();

                } else {

                    if (!publicStatus) {
                        //ToDo: Anfragen müssen geschickt werden

                    } else {
                        UserDatabase.child(user_id).child("my_group").setValue(groupId);
                        GroupDatabase.child("members").child(user_id).child("rank").setValue("member");
                        GroupDatabase.child("member_count").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                GroupDatabase.child("member_count").setValue(Integer.parseInt(dataSnapshot.getValue().toString()) + 1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
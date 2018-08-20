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
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.chat.GroupChat;
import com.example.alexander.groupup.chat.SingleChat;
import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.main.ProfileActivity;
import com.example.alexander.groupup.models.GroupModel;
import com.example.alexander.groupup.models.PublicStatus;
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
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyGroupView extends BaseActivity {

    //XML
    private TextView headline, description, backHomeFabText, addMemberFabText, groupChatFabText, statusText, leaveGroupText, memberCount;
    private FloatingActionButton backHomeFab, addMemberFab, leaveGroupFab, groupChatFab;
    private RecyclerView membersList;
    private ImageView statusIcon;

    //FireBase
    private DatabaseReference GroupMemberDatabase;
    private DatabaseReference GroupDatabase;
    private DatabaseReference UserDatabase;

    //Variables
    private String groupId, latLng, user_id;
    private String userName, userCity, userThumbImage, rank;
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
            protected void populateViewHolder(final membersViewHolder viewHolder, final UserModel user, int position) {

                final String list_user_id = getRef(position).getKey();

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

                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(MyGroupView.this, viewHolder.mView);

                        if (rank.equals("admin") || rank.equals("creator")) {
                            popupMenu.inflate(R.menu.menu_user_options_admin);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.message_option:
                                            UserDatabase.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    Map myUserMap = new HashMap<>();
                                                    myUserMap.put("name", userName);
                                                    myUserMap.put("city", userCity);
                                                    myUserMap.put("thumb_image", userThumbImage);

                                                    Map userMap = new HashMap<>();
                                                    userMap.put("thumb_image", dataSnapshot.child("thumb_image").getValue().toString());
                                                    userMap.put("name", dataSnapshot.child("name").getValue().toString());
                                                    userMap.put("city", dataSnapshot.child("city").getValue().toString());

                                                    UserDatabase.child(user_id).child("chats").child(list_user_id).updateChildren(userMap);
                                                    UserDatabase.child(list_user_id).child("chats").child(user_id).updateChildren(myUserMap);

                                                    Intent intent = new Intent(MyGroupView.this, SingleChat.class);
                                                    intent.putExtra("user_id", user_id);
                                                    intent.putExtra("receiver_user_id", list_user_id);
                                                    startActivity(intent);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            break;
                                        case R.id.make_admin_option:
                                            GroupDatabase.child("members").child(list_user_id).child("rank").setValue("admin");
                                            break;
                                        case R.id.remove_user_option:
                                            UserDatabase.child(list_user_id).child("my_group").removeValue();

                                            GroupDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    GroupModel groupModel = dataSnapshot.getValue(GroupModel.class);
                                                    groupMembers = groupModel.getMemberCount();


                                                    if (groupMembers == 0) {
                                                        DatabaseReference GroupChatDatabase = FirebaseDatabase.getInstance().getReference().child("GroupChat").child(groupId);
                                                        GroupDatabase.removeValue();
                                                        GroupChatDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Intent intent = new Intent(MyGroupView.this, HomeActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        });

                                                    } else if (groupMembers > 0) {
                                                        GroupMemberDatabase.child(list_user_id).removeValue();
                                                        //GroupDatabase.child("member_count").setValue(groupMembers);
                                                        memberCount.setText(Long.toString(groupMembers));
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            break;
                                    }
                                    return false;
                                }
                            });

                        } else if (rank.equals("member")) {

                        }

                        popupMenu.show();
                        return true;
                    }
                });
            }
        };
        membersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class membersViewHolder extends RecyclerView.ViewHolder {

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
            groupChatFab.startAnimation(FabClose);
            groupChatFabText.startAnimation(FabClose);
            groupChatFab.setClickable(false);
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
            groupChatFab.startAnimation(FabOpen);
            groupChatFabText.startAnimation(FabOpen);
            groupChatFab.setClickable(true);
            leaveGroupFab.setClickable(true);
            backHomeFab.setClickable(true);
            addMemberFab.setClickable(true);
            fabIsOpen = true;
        }
    }

    private void findIDs() {
        statusText = findViewById(R.id.status_text_my_group_view);
        statusIcon = findViewById(R.id.status_icon_group_view);
        backHomeFab = findViewById(R.id.back_home_my_group_view);
        addMemberFab = findViewById(R.id.add_member_fab);
        backHomeFabText = findViewById(R.id.back_home_text_my_group_view);
        addMemberFabText = findViewById(R.id.add_member_fab_text);
        memberCount = findViewById(R.id.members_my_group_view);
        leaveGroupFab = findViewById(R.id.leave_group_fab);
        leaveGroupText = findViewById(R.id.leave_group_fab_text);
        groupChatFab = findViewById(R.id.group_chat_fab);
        groupChatFabText = findViewById(R.id.group_chat_fab_text);
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
                GroupModel g = dataSnapshot.getValue(GroupModel.class);
                headline.setText(LanguageStringsManager.getInstance().getLanguageStringByStringId(g.activity).getLocalLanguageString()
                        + " @" + g.location);
                description.setText(g.description);
                memberCount.setText(g.getMemberCount().toString());
                latLng = g.latlng;
                rank = dataSnapshot.child("members").child(user_id).child("rank").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        UserDatabase.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue().toString();
                userCity = dataSnapshot.child("city").getValue().toString();
                userThumbImage = dataSnapshot.child("thumb_image").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    public void groupChatFabClick(View view) {
        Intent intent = new Intent(MyGroupView.this, GroupChat.class);
        intent.putExtra("group_id", groupId);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
    }

    public void addMemberClick(View view) {
        Intent intent = new Intent(MyGroupView.this, AddGroupMembersActivity.class);
        startActivity(intent);
    }

    public void leaveGroupClick(View view) {
        UserDatabase.child(user_id).child("my_group").removeValue();

        GroupDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupModel g = dataSnapshot.getValue(GroupModel.class);
                //String members = dataSnapshot.child("member_count").getValue().toString();
                //groupMembers = Long.parseLong(members);
                //groupMembers--;

                groupMembers = g.getMemberCount();

                if (g.public_status == PublicStatus.open) {
                    statusIcon.setImageResource(R.drawable.material_lock_open_white_36);
                    statusText.setText(R.string.open);
                }
                if (groupMembers < 2) {
                    DatabaseReference GroupChatDatabase = FirebaseDatabase.getInstance().getReference().child("GroupChat").child(groupId);
                    GroupDatabase.removeValue();
                    FirebaseDatabase.getInstance().getReference().child("GeoFire").child(user_id).removeValue();
                    GroupChatDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(MyGroupView.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    });

                } else if (groupMembers > 0) {
                    GroupMemberDatabase.child(user_id).removeValue();
                    DatabaseReference GroupChatDatabase = FirebaseDatabase.getInstance().getReference()
                            .child("GroupChat").child(groupId);

                    DatabaseReference user_message_push = GroupChatDatabase.push();
                    String pushId = user_message_push.getKey();

                    Map messageMap = new HashMap();
                    messageMap.put("message", userName + " left the Group.");
                    messageMap.put("from", user_id);
                    messageMap.put("type", "information");

                    GroupChatDatabase.child(pushId).updateChildren(messageMap);

                    Intent intent = new Intent(MyGroupView.this, HomeActivity.class);
                    Toast.makeText(MyGroupView.this, "You left the Group.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
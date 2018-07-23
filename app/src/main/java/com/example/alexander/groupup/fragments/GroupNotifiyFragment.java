package com.example.alexander.groupup.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.models.GroupModel;
import com.example.alexander.groupup.models.GroupsRequestModel;
import com.example.alexander.groupup.models.UserModel;
import com.example.alexander.groupup.profile.UserProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupNotifiyFragment extends Fragment {


    public GroupNotifiyFragment() {
        // Required empty public constructor
    }
    //Firebase
    private DatabaseReference GroupRequestDatabase;
    private DatabaseReference UserProfileDatabase;
    private DatabaseReference MyAccountDatabase;

    //XML
    private RecyclerView requestedGroupsList;
    private TextView noRequests;

    //Variables
    private long friendsCountMyAccount, friendsCountUser;
    private String myProfileName, myProfileCity, myProfileThumbImage;
    private String userName, userCity, userThumbImage;
    private String user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group_notifiy, container, false);;

        noRequests = view.findViewById(R.id.no_requests_groups);
        requestedGroupsList = view.findViewById(R.id.groups_requests_list);

        requestedGroupsList.setHasFixedSize(true);
        requestedGroupsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        user_id = getActivity().getIntent().getExtras().getString("user_id");

        MyAccountDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        GroupRequestDatabase = FirebaseDatabase.getInstance().getReference().child("notifications").child(user_id).child("groupInvitations");

        GroupRequestDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    noRequests.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<GroupsRequestModel, GroupRequestViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<GroupsRequestModel, GroupRequestViewHolder>(
                GroupsRequestModel.class,
                R.layout.single_layout_group_notify,
                GroupRequestViewHolder.class,
                GroupRequestDatabase
        ) {

            @Override
            protected void populateViewHolder(final GroupRequestViewHolder viewHolder, final GroupsRequestModel group, final int position) {
                FirebaseDatabase.getInstance().getReference().child("Groups").child(group.getFrom()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GroupModel model = dataSnapshot.getValue(GroupModel.class);
                        viewHolder.setGroup(model.getActivity()+"@"+model.getLocation());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                FirebaseDatabase.getInstance().getReference().child("Users").child(group.getFrom()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel model = dataSnapshot.getValue(UserModel.class);
                        viewHolder.setCreator(model.getName());
                        viewHolder.setThumbImage(model.getThumb_image(), getActivity().getApplicationContext());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                        intent.putExtra("group_id", group.getFrom());
                        startActivity(intent);
                    }
                });

                viewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptRequest(group.getFrom(), getRef(position).getKey());
                    }
                });
            }
        };
        requestedGroupsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class GroupRequestViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Button acceptBtn;

        public GroupRequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            this.acceptBtn = itemView.findViewById(R.id.accept_group_btn);
        }

        public void setGroup(String name) {
            TextView userNameView = mView.findViewById(R.id.request_group_name);
            userNameView.setText(name);
        }

        public void setCreator(String creator) {
            TextView friendsDate = mView.findViewById(R.id.request_creator);
            friendsDate.setText(creator);
        }

        public void setThumbImage(String thumb_image, Context context) {
            CircleImageView userImageView = mView.findViewById(R.id.request_user_picture);
            Picasso.with(context).load(thumb_image).placeholder(R.drawable.profile_white).into(userImageView);
        }
    }

    private void acceptRequest(final String gid, final String notificationID) {
        UserProfileDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final DatabaseReference GroupsDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(gid);

        UserProfileDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupsDatabase.child("members").child(dataSnapshot.getKey()).child("rank").setValue("member");
                GroupsDatabase.child("member_count").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GroupsDatabase.child("member_count").setValue(Integer.parseInt(dataSnapshot.getValue().toString())+1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        GroupRequestDatabase.child(notificationID).removeValue();
    }
}


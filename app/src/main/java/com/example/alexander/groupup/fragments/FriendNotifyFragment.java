package com.example.alexander.groupup.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.groupup.models.UserModel;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.profile.UserProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendNotifyFragment extends Fragment {

    //Firebase
    private DatabaseReference FriendRequestDatabase;
    private DatabaseReference UserProfileDatabase;
    private DatabaseReference MyAccountDatabase;

    //XML
    private RecyclerView requestedFriendsList;
    private TextView noRequests;

    //Variables
    private long friendsCountMyAccount, friendsCountUser;
    private String myProfileName, myProfileThumbImage, userName, userThumbImage;
    private String user_id;

    public FriendNotifyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friend_notify, container, false);

        noRequests = view.findViewById(R.id.no_requests_friends);
        requestedFriendsList = view.findViewById(R.id.friend_requests_list);

        requestedFriendsList.setHasFixedSize(true);
        requestedFriendsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        user_id = getActivity().getIntent().getExtras().getString("user_id");

        MyAccountDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        FriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id)
                .child("friend_requests").child("received");

        FriendRequestDatabase.addValueEventListener(new ValueEventListener() {
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

        FirebaseRecyclerAdapter<UserModel, FriendRequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserModel, FriendRequestViewHolder>(
                UserModel.class,
                R.layout.single_layout_friend_notify,
                FriendRequestViewHolder.class,
                FriendRequestDatabase
        ) {

            @Override
            protected void populateViewHolder(FriendRequestViewHolder viewHolder, UserModel user, int position) {
                viewHolder.setName(user.getName());
                viewHolder.setCity(user.getCity());
                viewHolder.setThumbImage(user.getThumb_image(), getActivity().getApplicationContext());

                final String list_user_id = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                        intent.putExtra("user_id", list_user_id);
                        startActivity(intent);
                    }
                });

                viewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptRequest(list_user_id);
                    }
                });
            }
        };
        requestedFriendsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Button acceptBtn;

        public FriendRequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            this.acceptBtn = itemView.findViewById(R.id.accept_friend_btn);
        }

        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.request_user_name);
            userNameView.setText(name);
        }

        public void setCity(String city) {
            TextView friendsDate = mView.findViewById(R.id.request_user_city);
            friendsDate.setText(city);
        }

        public void setThumbImage(String thumb_image, Context context) {
            CircleImageView userImageView = mView.findViewById(R.id.request_user_picture);
            Picasso.with(context).load(thumb_image).placeholder(R.drawable.profile_white).into(userImageView);
        }
    }

    private void acceptRequest(final String receiver_user_id) {
        UserProfileDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(receiver_user_id);

        UserProfileDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue().toString();
                userThumbImage = dataSnapshot.child("thumb_image").getValue().toString();

                String friendCount = dataSnapshot.child("friends_count").getValue().toString();
                friendsCountUser = Long.parseLong(friendCount);

                MyAccountDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myProfileName = dataSnapshot.child("name").getValue().toString();
                        myProfileThumbImage = dataSnapshot.child("thumb_image").getValue().toString();

                        String counter = dataSnapshot.child("friends_count").getValue().toString();
                        friendsCountMyAccount = Long.parseLong(counter);

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
                        MyAccountDatabase.child("friends").child(receiver_user_id).child("thumb_image").setValue(userThumbImage).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                UserProfileDatabase.child("friends_count").setValue(friendsUser);
                                UserProfileDatabase.child("friends").child(user_id).child("date").setValue(friendDate);
                                UserProfileDatabase.child("friends").child(user_id).child("name").setValue(myProfileName);
                                UserProfileDatabase.child("friends").child(user_id).child("thumb_image").setValue(myProfileThumbImage)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                MyAccountDatabase.child("friend_requests").child("received").child(receiver_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        UserProfileDatabase.child("friend_requests").child("sent").child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(getActivity(), getString(R.string.you_are_now_friends), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                            }
                        });
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
    }
}
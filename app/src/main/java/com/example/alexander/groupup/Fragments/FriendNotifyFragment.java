package com.example.alexander.groupup.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexander.groupup.Models.UserModel;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.UserProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendNotifyFragment extends Fragment {

    //Firebase
    private FirebaseUser mCurrentUser;
    private DatabaseReference FriendRequestDatabase;

    //XML
    private RecyclerView requestedFriendsList;

    public FriendNotifyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friend_notify, container, false);

        requestedFriendsList = view.findViewById(R.id.friend_requests_list);
        requestedFriendsList.setHasFixedSize(true);
        requestedFriendsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Initialize Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        FriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid)
                .child("friend_requests").child("received");

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
            }
        };
        requestedFriendsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendRequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
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
            CircleImageView userImageView = mView.findViewById(R.id.request_user_picture    );
            Picasso.with(context).load(thumb_image).placeholder(R.drawable.default_user_black).into(userImageView);
        }
    }
}
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
import android.widget.TextView;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.chat.MessageActivity;
import com.example.alexander.groupup.models.UserModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {

    //XML
    private RecyclerView chatsList;
    private TextView noChats;

    //Variables
    private String user_id;

    //Firebase
    private DatabaseReference ChatUsersDatabase;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_chat, container, false);

        noChats = view.findViewById(R.id.no_chats);
        chatsList = view.findViewById(R.id.chat_list);

        chatsList.setHasFixedSize(true);
        chatsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        user_id = getActivity().getIntent().getExtras().getString("user_id");

        ChatUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("chats");

        ChatUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    noChats.setVisibility(View.GONE);
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

        FirebaseRecyclerAdapter<UserModel, ChatsViewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserModel, ChatsViewholder>(
                UserModel.class,
                R.layout.single_layout_user,
                ChatsViewholder.class,
                ChatUsersDatabase
        ) {

            @Override
            protected void populateViewHolder(ChatsViewholder viewHolder, UserModel user, int position) {
                viewHolder.setName(user.getName());
                viewHolder.setCity(user.getCity());
                viewHolder.setThumbImage(user.getThumb_image(), getActivity().getApplicationContext());

                final String list_user_id = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MessageActivity.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("receiver_user_id", list_user_id);
                        startActivity(intent);
                    }
                });
            }
        };
        chatsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ChatsViewholder extends RecyclerView.ViewHolder {

        View mView;

        public ChatsViewholder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setCity(String city) {
            TextView friendsDate = mView.findViewById(R.id.user_single_information);
            friendsDate.setText(city);
        }

        public void setThumbImage(String thumb_image, Context context) {
            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            Picasso.with(context).load(thumb_image).placeholder(R.drawable.profile_white).into(userImageView);
        }
    }
}
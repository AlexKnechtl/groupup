package com.example.alexander.groupup.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.main.ChatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        user_id = getActivity().getIntent().getExtras().getString("user_id");

        ChatUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("chats");

        return view;
    }
}
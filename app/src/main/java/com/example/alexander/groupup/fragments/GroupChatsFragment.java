package com.example.alexander.groupup.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexander.groupup.R;
import com.google.firebase.database.FirebaseDatabase;

public class GroupChatsFragment extends Fragment {


    //Variables
    private String user_id;

    public GroupChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people_chat, container, false);

        user_id = getActivity().getIntent().getExtras().getString("user_id");

        return view;
    }
}
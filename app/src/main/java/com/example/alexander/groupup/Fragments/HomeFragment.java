package com.example.alexander.groupup.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.alexander.groupup.CreateGroup.InterviewStart;
import com.example.alexander.groupup.GroupView;
import com.example.alexander.groupup.Models.GroupsModel;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.RegisterActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    //XML
    private CircleImageView theAssistant;
    private RecyclerView recyclerView;
    private TextView location, dateTextView;
    private Button createGroupButton;

    //Popup
    private Context mContext;

    //Constants
    public static final String ANONYMOUS = "anonymous";
    private static final String TAG = "MainActivity";
    private static final int LOCATION_REQUEST_CODE = 2;

    //Firebase
    private DatabaseReference GroupDatabase;
    private DatabaseReference UserDatabase;
    private FirebaseUser mCurrentUser;


    //Variables
    private String city;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        //Get Current User ID
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        //Find Ids
        dateTextView = view.findViewById(R.id.date_main);
        location = view.findViewById(R.id.location_main);
        createGroupButton = view.findViewById(R.id.create_group_button);

        //Get Data from User
        UserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                city = dataSnapshot.child("city").getValue().toString();
                location.setText(city);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mContext = getActivity().getApplicationContext();

        //Show Date in GroupCalendar
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MMM");
        String currentDate = formatter.format(new Date());
        dateTextView.setText(currentDate);

        //Recycler View
        recyclerView = view.findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InterviewStart.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        GroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child("Steiermark");

        FirebaseRecyclerAdapter<GroupsModel, GroupsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GroupsModel, GroupsViewHolder>(
                GroupsModel.class,
                R.layout.single_layout_group,
                GroupsViewHolder.class,
                GroupDatabase
        ) {
            @Override
            protected void populateViewHolder(GroupsViewHolder groupsViewHolder, GroupsModel groups, int position) {

                groupsViewHolder.setGroupImage(groups.getGroup_image());
                groupsViewHolder.setCategoryCity(groups.getCategory(), groups.getLocation());
                groupsViewHolder.setTag(groups.getTag());

                final String group_id = getRef(position).getKey();

                groupsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), GroupView.class);
                        intent.putExtra("group_id", group_id);
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class GroupsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public GroupsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setGroupImage(String groupImage) {
            final ImageView groupBackground = mView.findViewById(R.id.background_group);
            Glide.with(mView).load(groupImage).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        groupBackground.setBackground(resource);
                    }
                }
            });
        }

        public void setCategoryCity(String category, String city) {
            TextView groupHeadline = mView.findViewById(R.id.group_headline);
            groupHeadline.setText(category + " @" + city);
        }

        public void setTag(String tag) {
            TextView groupTag = mView.findViewById(R.id.group_tag);
            groupTag.setText(",," + tag + ",,");
        }
    }
}
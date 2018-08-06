package com.example.alexander.groupup.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.alexander.groupup.StartActivity;
import com.example.alexander.groupup.chat.GroupChat;
import com.example.alexander.groupup.group.MyGroupView;
import com.example.alexander.groupup.interviews.InterviewStart;
import com.example.alexander.groupup.group.GroupView;
import com.example.alexander.groupup.interviews.InterviewTags;
import com.example.alexander.groupup.singletons.LanguageStringsManager;
import com.example.alexander.groupup.models.GroupModel;
import com.example.alexander.groupup.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    //XML
    private RecyclerView recyclerView;
    private TextView location;
    private TextView searchLocation;
    private Button groupButton, groupChatButton;

    //Constants
    public static final String ANONYMOUS = "anonymous";

    //FireBase
    private DatabaseReference GroupDatabase;
    private DatabaseReference UserDatabase;

    private GeoFire geoFire;

    private GeoQuery geoQuery;

    //Variables
    private String city;
    private Context mContext = HomeActivity.this;
    private static final int ACTIVITY_NUM = 0;
    private boolean creator;
    private String user_id, group_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home);

        FirebaseMessaging.getInstance().subscribeToTopic("TestTopic");

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationTokens")
                        .child(instanceIdResult.getToken()).setValue("True");
            }
        });

        mContext = getApplicationContext();

        //Get Current User ID
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        user_id = current_user.getUid();

        setupBottomNavigationView();

        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        //Find Ids
        TextView dateTextView;
        dateTextView = findViewById(R.id.date_main);
        location = findViewById(R.id.location_main);
        groupButton = findViewById(R.id.group_button);
        recyclerView = findViewById(R.id.main_recycler_view);
        groupChatButton = findViewById(R.id.group_chat_button);
        searchLocation = findViewById(R.id.loc_radius);
        searchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                geoQuery.setRadius(Double.parseDouble(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Get Data from User
        UserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                city = dataSnapshot.child("city").getValue().toString();

                if (dataSnapshot.hasChild("my_group")) {
                    group_id = dataSnapshot.child("my_group").getValue().toString();
                    groupButton.setText(R.string.my_group);
                    groupChatButton.setVisibility(View.VISIBLE);
                    creator = true;
                } else
                    creator = false;

                location.setText(city);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        GroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups");


        geoFire = new GeoFire(GroupDatabase);

        geoQuery = geoFire.queryAtLocation(new GeoLocation(47.16713310000001,15.529306000000002), 10);


        //Show Date in GroupCalendar
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MMM");
        String currentDate = formatter.format(new Date());
        dateTextView.setText(currentDate);

        //Recycler View
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!creator) {
                    Intent intent = new Intent(HomeActivity.this, InterviewStart.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeActivity.this, MyGroupView.class);
                    intent.putExtra("group_id", group_id);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<GroupModel, GroupsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GroupModel, GroupsViewHolder>(
                GroupModel.class,
                R.layout.single_layout_group,
                GroupsViewHolder.class,
                geoQuery.
        ) {
            @Override
            protected void populateViewHolder(GroupsViewHolder groupsViewHolder, GroupModel groups, int position) {

                groupsViewHolder.setGroupImage(groups.getGroup_image());

                groupsViewHolder.setActivityCity(LanguageStringsManager.getInstance().getLanguageStringByStringId(groups.getActivity()).getLocalLanguageString(), groups.getLocation());
                groupsViewHolder.setTag1(groups.getTag1());
                groupsViewHolder.setTag2(groups.getTag2());
                groupsViewHolder.setTag3(groups.getTag3());
                groupsViewHolder.setMemberQuantity(groups.getMember_count().toString());

                final String group_id = getRef(position).getKey();

                groupsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, GroupView.class);
                        intent.putExtra("group_id", group_id);
                        intent.putExtra("user_id", user_id);
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
                    groupBackground.setBackground(resource);
                }
            });
        }

        public void setActivityCity(String activity, String location) {
            TextView groupHeadline = mView.findViewById(R.id.group_headline);
            groupHeadline.setText(activity
                    + " @" + location);
        }

        public void setTag1(String tag1) {
            TextView tag1TextView = mView.findViewById(R.id.group_layout_tag_1);
            tag1TextView.setText(tag1);
        }

        public void setTag2(String tag2) {
            TextView tag2TextView = mView.findViewById(R.id.group_layout_tag_2);
            tag2TextView.setText(tag2);
        }

        public void setTag3(String tag3) {
            TextView tag3TextView = mView.findViewById(R.id.group_layout_tag_3);
            tag3TextView.setText(tag3);
        }

        public void setMemberQuantity(String quantity)
        {
            TextView v = mView.findViewById(R.id.member_quantity_group);
            v.setText(quantity);
        }
    }

    public void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext, user_id, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    long lastPress;

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastPress > 5000) {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG).show();
            lastPress = currentTime;
        } else {
            Intent intent = new Intent(HomeActivity.this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    }

    //OnClicks
    public void groupChatClick(View view) {
        Intent intent = new Intent (HomeActivity.this, GroupChat.class);
        intent.putExtra("group_id", group_id);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
    }
}
package com.example.alexander.groupup.MainActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.example.alexander.groupup.RegisterActivity;
import com.example.alexander.groupup.Singletons.LanguageStringsManager;
import com.example.alexander.groupup.Models.GroupsModel;
import com.example.alexander.groupup.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    //XML
    private RecyclerView recyclerView;
    private TextView location, dateTextView;
    private Button createGroupButton;

    //Popup
    private ConstraintLayout coordinatorLayout;
    private PopupWindow mPopupWindow;

    //Constants
    public static final String ANONYMOUS = "anonymous";
    private static final String TAG = "MainActivity";
    private static final int LOCATION_REQUEST_CODE = 2;
    private Context context;

    //Firebase
    private DatabaseReference GroupDatabase;
    private DatabaseReference UserDatabase;
    private FirebaseUser mCurrentUser;

    //Variables
    private String city;
    private Context mContext = HomeActivity.this;
    private static final int ACTIVITY_NUM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home);

        //Transparent Status Bar
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setupBottomNavigationView();

        //Get Current User ID
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        //Find Ids
        dateTextView = findViewById(R.id.date_main);
        location = findViewById(R.id.location_main);
        createGroupButton = findViewById(R.id.create_group_button);

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

        mContext = getApplicationContext();

        //Show Date in GroupCalendar
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MMM");
        String currentDate = formatter.format(new Date());
        dateTextView.setText(currentDate);

        //Recycler View
        recyclerView = findViewById(R.id.main_recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, InterviewStart.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        GroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child("Steiermark");

        FirebaseRecyclerAdapter<GroupsModel, GroupsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GroupsModel, GroupsViewHolder>(
                GroupsModel.class,
                R.layout.single_layout_group,
                HomeActivity.GroupsViewHolder.class,
                GroupDatabase
        ) {
            @Override
            protected void populateViewHolder(HomeActivity.GroupsViewHolder groupsViewHolder, GroupsModel groups, int position) {

                groupsViewHolder.setGroupImage(groups.getGroup_image());
                groupsViewHolder.setCategoryCity(groups.getCategory(), groups.getLocation());
                groupsViewHolder.setTag(groups.getTag());

                final String group_id = getRef(position).getKey();

                groupsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, GroupView.class);
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
                        groupBackground.setBackground(resource);
                }
            });
        }

        public void setCategoryCity(String category, String city) {
            TextView groupHeadline = mView.findViewById(R.id.group_headline);
            groupHeadline.setText(category + " @" + city);
        }

        public void setTag(String tag) {
            TextView groupTag = mView.findViewById(R.id.group_tag);
            groupTag.setText(",," + LanguageStringsManager.getInstance().getLanguageStringByStringId(tag).getLocalLanguageString() + ",,");
        }
    }

    public void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

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
            Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    }
}
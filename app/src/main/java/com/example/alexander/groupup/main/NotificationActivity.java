package com.example.alexander.groupup.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.alexander.groupup.adapters.RequestAdapter;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.models.RequestModel;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private Context mContext = NotificationActivity.this;
    private static final int ACTIVITY_NUM = 1;

    //XML
    private TextView dateTextView;

    //Firebase
    private String user_id;
    private DatabaseReference RequestDatabase;
    private DatabaseReference UserDatabase;

    //XML
    private RecyclerView requestRecyclerView;
    private TextView noRequests;

    //Variables
    private final List<RequestModel> requestList = new ArrayList<>();
    private RequestAdapter requestAdapter;
    private Boolean myGroup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_notification);

        //Get Information by Intent
        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("user_id");
        if(user_id == null) user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setupBottomNavigationView();

        //Find IDs
        dateTextView = findViewById(R.id.date_notification);
        requestRecyclerView = findViewById(R.id.request_recycler_view);
        noRequests = findViewById(R.id.no_requests_tv);

        //Show Date in GroupCalendar
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MMM");
        String currentDate = formatter.format(new Date());
        dateTextView.setText(currentDate);

        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        UserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("my_group").exists()) {
                    myGroup = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        RequestDatabase = FirebaseDatabase.getInstance().getReference().child("notifications").child(user_id);

        //Set Adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        requestRecyclerView.setHasFixedSize(true);
        requestRecyclerView.setLayoutManager(linearLayoutManager);

        requestAdapter = new RequestAdapter(requestList, mContext, myGroup);

        requestRecyclerView.setAdapter(requestAdapter);

        RequestDatabase.addValueEventListener(new ValueEventListener() {
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

        loadRequests();
    }

    public void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext, user_id, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NotificationActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void loadRequests() {
        RequestDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                RequestModel request = dataSnapshot.getValue(RequestModel.class);

                requestList.add(request);
                requestAdapter.notifyDataSetChanged();
                requestRecyclerView.scrollToPosition(requestList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();

                for (RequestModel testModel : requestList) {
                    if (key.equals(testModel.getFrom())) {
                        requestList.remove(testModel);
                        requestAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
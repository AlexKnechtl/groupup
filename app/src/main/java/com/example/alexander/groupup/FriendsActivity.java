package com.example.alexander.groupup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.alexander.groupup.models.UserModel;
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
 * Created by alexander on 24.03.18.
 */

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView friendsList;

    private DatabaseReference FriendsDatabase;
    private FirebaseAuth mAuth;
    private TextView noFriends;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("user_id");

        FriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("friends");
        FriendsDatabase.keepSynced(true);

        noFriends = findViewById(R.id.no_friends_text);

        friendsList = findViewById(R.id.friends_list);
        friendsList.setHasFixedSize(true);
        friendsList.setLayoutManager(new LinearLayoutManager(this));

        FriendsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    noFriends.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<UserModel, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserModel, FriendsViewHolder>(
                UserModel.class,
                R.layout.single_layout_user,
                FriendsViewHolder.class,
                FriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(FriendsViewHolder viewHolder, UserModel user, int position) {
                viewHolder.setDate(user.getDate());
                viewHolder.setName(user.getName());
                viewHolder.setThumbImage(user.getThumb_image(), getApplicationContext());

                final String list_user_id = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FriendsActivity.this, UserProfileActivity.class);
                        intent.putExtra("user_id", list_user_id);
                        startActivity(intent);
                    }
                });
            }
        };
        friendsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDate(String date) {
            TextView friendsDate = mView.findViewById(R.id.user_single_city);
            friendsDate.setText("Friends since " + date);
        }

        public void setName(String name) {
            TextView nameView = mView.findViewById(R.id.user_single_name);
            nameView.setText(name);
        }

        public void setThumbImage(String thumb_image, Context context) {
            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            Picasso.with(context).load(thumb_image).placeholder(R.drawable.default_user_black).into(userImageView);
        }
    }
}
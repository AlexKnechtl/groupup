package com.example.alexander.groupup.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.UserProfileActivity;
import com.example.alexander.groupup.models.UserModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewMessage extends AppCompatActivity {

    //XML
    private RecyclerView friendsList;
    private MaterialSearchView searchView;
    private TextView noFriends;

    //Firebase
    private DatabaseReference FriendsDatabase;
    private DatabaseReference ChatUsersDatabase;

    //Variables
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("user_id");

        initializeXML();

        FriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("friends");
        FriendsDatabase.keepSynced(true);

        ChatUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("chats");

        searchView = findViewById(R.id.material_search_view_friends);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userSearch(newText);
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

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
    }

    private void userSearch(String searchText) {

        Query firebaseSearchQuery = FriendsDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<UserModel, FriendsMessageViewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserModel, FriendsMessageViewholder>(
                UserModel.class,
                R.layout.single_layout_user,
                FriendsMessageViewholder.class,
                firebaseSearchQuery
        ) {
            @Override
            protected void populateViewHolder(FriendsMessageViewholder viewHolder, final UserModel user, int position) {
                viewHolder.setName(user.getName());
                viewHolder.setThumbImage(user.getThumb_image(), getApplicationContext());

                final String list_user_id = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NewMessage.this, UserProfileActivity.class);
                        intent.putExtra("user_id", list_user_id);

                        Map userMap = new HashMap<>();
                        userMap.put("thumb_image", user.getThumb_image());
                        userMap.put("name", user.getName());
                        userMap.put("message", "Schreib eine Nachricht! :)");

                        ChatUsersDatabase.updateChildren(userMap);

                        startActivity(intent);
                    }
                });
            }
        };
        friendsList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<UserModel, FriendsMessageViewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserModel, FriendsMessageViewholder>(
                UserModel.class,
                R.layout.single_layout_user,
                FriendsMessageViewholder.class,
                FriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(FriendsMessageViewholder viewHolder, final UserModel user, int position) {
                viewHolder.setName(user.getName());
                viewHolder.setThumbImage(user.getThumb_image(), getApplicationContext());

                final String list_user_id = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NewMessage.this, UserProfileActivity.class);
                        intent.putExtra("user_id", list_user_id);

                        Map userMap = new HashMap<>();
                        userMap.put("thumb_image", user.getThumb_image());
                        userMap.put("name", user.getName());
                        userMap.put("message", "Schreib eine Nachricht! :)");

                        ChatUsersDatabase.updateChildren(userMap);

                        startActivity(intent);
                    }
                });
            }
        };
        friendsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsMessageViewholder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsMessageViewholder(View itemView) {
            super(itemView);
            mView = itemView;
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

    //Add Search Menu Icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.material_searchbar, menu);

        MenuItem item = menu.findItem(R.id.material_search_icon);
        searchView.setMenuItem(item);

        return true;
    }

    private void initializeXML() {
        noFriends = findViewById(R.id.no_friends_text);
        friendsList = findViewById(R.id.friends_list);
        friendsList.setHasFixedSize(true);
        friendsList.setLayoutManager(new LinearLayoutManager(this));
        Toolbar myToolbar = findViewById(R.id.toolbar_friends);
        setSupportActionBar(myToolbar);
    }
}
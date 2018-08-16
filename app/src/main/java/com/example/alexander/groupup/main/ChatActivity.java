package com.example.alexander.groupup.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.chat.SingleChat;
import com.example.alexander.groupup.chat.NewSingleChat;
import com.example.alexander.groupup.models.UserModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends BaseActivity {

    private Context mContext = ChatActivity.this;
    private static final int ACTIVITY_NUM = 4;

    //XML
    private MaterialSearchView searchView;
    private RecyclerView chatsList;
    private TextView noChats;

    //Variables
    private String user_id;

    //Firebase
    private DatabaseReference ChatUsersDatabase;
    private Query chatTimeQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_chat);

        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("user_id");
        if(user_id == null) user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Toolbar myToolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(myToolbar);

        searchView = findViewById(R.id.material_search_view_chat);
        noChats = findViewById(R.id.no_chats);
        chatsList = findViewById(R.id.chat_list);

        chatsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        chatsList.setLayoutManager(linearLayoutManager);

        ChatUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("chats");
        ChatUsersDatabase.keepSynced(true);

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

        setupBottomNavigationView();
    }


    public void newMessageClick(View view) {
        Intent intent = new Intent(ChatActivity.this, NewSingleChat.class);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        chatTimeQuery = ChatUsersDatabase.orderByChild("time").limitToLast(20);

        FirebaseRecyclerAdapter<UserModel, ChatsViewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserModel, ChatsViewholder>(
                UserModel.class,
                R.layout.single_layout_chat,
                ChatsViewholder.class,
                chatTimeQuery
        ) {

            @Override
            protected void populateViewHolder(ChatsViewholder viewHolder, UserModel user, int position) {
                viewHolder.setDate(user.getDate());
                viewHolder.setName(user.getName());
                viewHolder.setMessage(user.getMessage());
                viewHolder.setThumbImage(user.getThumb_image(), ChatActivity.this);

                final String list_user_id = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatActivity.this, SingleChat.class);
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

        public void setMessage(String message) {
            TextView messagetv = mView.findViewById(R.id.user_single_information);
            messagetv.setText(message);
        }

        public void setThumbImage(String thumb_image, Context context) {
            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            Picasso.with(context).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_white).into(userImageView);
        }

        public void setDate(String date) {
            TextView messageDate = mView.findViewById(R.id.message_date);
            messageDate.setText(date);
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

    //Add Search Menu Icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.material_searchbar, menu);

        MenuItem item = menu.findItem(R.id.material_search_icon);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
package com.example.alexander.groupup.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alexander.groupup.models.UserModel;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.UserProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {

    //XML
    private EditText mSearchField;
    private RecyclerView mResultList;
    private TextView searchHeadline;

    //Firebase
    private DatabaseReference mUserDatabase;

    //Variables
    private Context mContext = SearchActivity.this;
    private static final int ACTIVITY_NUM = 2;
    private Query firebaseSearchQuery;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_search);

        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("user_id");

        setupBottomNavigationView();

        //Initialize Firebase
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        //Find IDs
        mSearchField = findViewById(R.id.search_users);
        mResultList = findViewById(R.id.result_list);
        searchHeadline = findViewById(R.id.search_headline);

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHeadline.setVisibility(View.GONE);
                String search = mSearchField.getText().toString();
                userSearch(search);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void userSearch(String searchText) {

        firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<UserModel, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserModel, UsersViewHolder>(
                UserModel.class,
                R.layout.single_layout_searchuser,
                UsersViewHolder.class,
                firebaseSearchQuery
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, UserModel users, int position) {
                usersViewHolder.setNameAge(users.getName(), users.getAge());
                usersViewHolder.setCity(users.getCity());
                usersViewHolder.setUserImage(users.getThumb_image(), getApplicationContext());

                final String user_id = getRef(position).getKey();

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SearchActivity.this, UserProfileActivity.class);
                        intent.putExtra("user_id", user_id);
                        startActivity(intent);
                    }
                });
            }
        };
        mResultList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setNameAge(String name, String age) {
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name + ", " + age);
        }

        public void setCity(String city) {
            TextView userNameView = mView.findViewById(R.id.user_single_city);
            userNameView.setText(city);
        }

        public void setUserImage(String thumb_image, Context context) {
            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            Picasso.with(context).load(thumb_image).placeholder(R.drawable.default_user_black).into(userImageView);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
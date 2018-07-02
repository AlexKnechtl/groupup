package com.example.alexander.groupup.CreateGroup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.alexander.groupup.FriendsActivity;
import com.example.alexander.groupup.MainActivities.HomeActivity;
import com.example.alexander.groupup.Models.UserModel;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.UserProfileActivity;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class InterviewLeisure extends AppCompatActivity {

    //XML
    private LinearLayout backLayout;
    private RecyclerView tagRecyclerView;

    //Variables
    private String group;

    //Firebase
    private DatabaseReference TagDatabase;

    ArrayList<String> tags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_leisure);

        backLayout = findViewById(R.id.back_home_leisure);
        tagRecyclerView = findViewById(R.id.recyclerViewInterviewLeisureTags);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TagDatabase = FirebaseDatabase.getInstance().getReference().child("Tags").child("Graz");

        final TagAdapter adapter = new TagAdapter(this, tags);
        tagRecyclerView.setAdapter(adapter);
        TagDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren())
                {
                    tags.add(s.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Get Information by Intent
        Bundle bundle = getIntent().getExtras();
        group = bundle.getString("group"); // Gruppen Kategorie

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewLeisure.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

//        FirebaseRecyclerAdapter<UserModel, TagViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserModel, TagViewHolder>(
//                UserModel.class,
//                R.layout.single_layout_tag,
//                TagViewHolder.class,
//                TagDatabase
//        ) {
//            @Override
//            protected void populateViewHolder(final TagViewHolder viewHolder, UserModel user, int position) {
//                viewHolder.setName(user.getName());
//
//                final String tag = getRef(position).getKey();
//
//                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(InterviewLeisure.this, UserProfileActivity.class);
//                        intent.putExtra("tag", tag);
//                        startActivity(intent);
//                    }
//                });
//            }
//        };
//        tagRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public class TagAdapter extends RecyclerView.Adapter<TagViewHolder>{

        ArrayList<String> tags;
        Context c;

        public TagAdapter(Context c, ArrayList<String> tags)
        {
            this.tags = tags;
            this.c = c;
        }

        @NonNull
        @Override
        public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(c).inflate(R.layout.single_layout_tag, parent, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(InterviewLeisure.this, UserProfileActivity.class);
                    intent.putExtra("tag", tags.get(tagRecyclerView.getChildLayoutPosition(v)));
                    startActivity(intent);
                }
            });
            return new TagViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull TagViewHolder holder, final int position) {
            holder.setName(tags.get(position));
        }

        @Override
        public int getItemCount() {
            return tags.size();
        }
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TagViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView nameView = mView.findViewById(R.id.tag_text);
            nameView.setText(name);
        }
    }
}
package com.example.alexander.groupup.interviews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.models.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InterviewActivityLeisure extends AppCompatActivity {

    //XML
    private RelativeLayout backLayout;
    private RecyclerView tagRecyclerView;

    //Firebase
    private DatabaseReference TagDatabase;

    ArrayList<String> tags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_leisure);

        backLayout = findViewById(R.id.back_layout_leisure);
        tagRecyclerView = findViewById(R.id.recyclerViewInterviewLeisureTags);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TagDatabase = FirebaseDatabase.getInstance().getReference().child("Tags").child("Graz");

        final TagAdapter adapter = new TagAdapter(this, tags);
        tagRecyclerView.setAdapter(adapter);
        TagDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    tags.add(s.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewActivityLeisure.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    public class TagAdapter extends RecyclerView.Adapter<TagViewHolder> {

        ArrayList<String> tags;
        Context c;

        public TagAdapter(Context c, ArrayList<String> tags) {
            this.tags = tags;
            this.c = c;
        }


        /*@Override
        protected void populateViewHolder(TagViewHolder viewHolder, UserModel user, int position) {

            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String tag = tags.get(tagRecyclerView.getChildLayoutPosition(v));
                    Intent intent = new Intent(InterviewActivityLeisure.this, InterviewPublic.class);
                    intent.putExtra("tag", tag);
                    startActivity(intent);
                }
            });
        } */

        @NonNull
        @Override
        public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View mView;

            View v = LayoutInflater.from(c).inflate(R.layout.single_layout_tag, parent, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(InterviewActivityLeisure.this, InterviewPublic.class);
                    intent.putExtra("group", "leisure");
                    intent.putExtra("activity", tags.get(tagRecyclerView.getChildLayoutPosition(v)));
                    //intent.putExtra("group_image", ... //ToDO Add Leisure Group Images (just random from about 15 Images)
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
package com.example.alexander.groupup.profile;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.alexander.groupup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SelectLanguageActivity extends AppCompatActivity {

    private RecyclerView languagesRecyclerView = null;

    private DatabaseReference Languages;

    private HashMap<String, Boolean> languagesData;

    private LanguagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laguage_select);
        languagesRecyclerView = findViewById(R.id.recyclerviewSelectLanguages);
        languagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Languages = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Languages");

        Toolbar myToolbar = findViewById(R.id.toolbar_language_select);
        setSupportActionBar(myToolbar);

        languagesData = new HashMap<>();
        languagesData.put("Arabic", false);
        languagesData.put("Bengali", false);
        languagesData.put("English", false);
        languagesData.put("French", false);
        languagesData.put("German", false);
        languagesData.put("Hindi", false);
        languagesData.put("Japanese", false);
        languagesData.put("Javanese", false);
        languagesData.put("Korean", false);
        languagesData.put("Mandarin", false);
        languagesData.put("Marathi", false);
        languagesData.put("Portuguese", false);
        languagesData.put("Punjabi", false);
        languagesData.put("Russian", false);
        languagesData.put("Spanish", false);
        languagesData.put("Tamil", false);
        languagesData.put("Telugu", false);
        languagesData.put("Turkish", false);
        languagesData.put("Urdu", false);
        languagesData.put("Vietnamese", false);

        adapter = new LanguagesAdapter(this, languagesData);

        Languages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String[] myLanguages = dataSnapshot.getValue().toString().split(", ");

                    for (String lang : myLanguages)
                        if(languagesData.containsKey(lang))
                            languagesData.put(lang, true);
                    adapter.notifyDataSetChanged();
                }
                catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        languagesRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        String mylanguages = "";

        for(Map.Entry<String, Boolean> entry : languagesData.entrySet())
            if(entry.getValue())
                mylanguages += entry.getKey() + ", ";

        if(mylanguages.length()>0)
            mylanguages = mylanguages.substring(0, mylanguages.length()-2);

        Languages.setValue(mylanguages);

        super.onBackPressed();
    }

    public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesViewHolder>{

        HashMap<String, Boolean> languagesData = new HashMap<>();
        ArrayList<String> languagesDataIdexes = new ArrayList<>();
        Context c;

        public HashMap<String, Boolean> getLanguagesData() {
            return languagesData;
        }

        public LanguagesAdapter(Context c, HashMap<String, Boolean> languagesData)
        {
            this.languagesData = languagesData;
            for(Map.Entry<String, Boolean> entry : languagesData.entrySet())
                languagesDataIdexes.add(entry.getKey());
            this.c = c;
        }

        @NonNull
        @Override
        public LanguagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View mView;

            View v = LayoutInflater.from(c).inflate(R.layout.singlelayout_select_language, parent, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String language = languagesDataIdexes.get(languagesRecyclerView.getChildLayoutPosition(v));
                    languagesData.put(language, !languagesData.get(language)); //TODO
                    notifyDataSetChanged();
                }
            });
            return new LanguagesViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull LanguagesViewHolder holder, final int position) {
            holder.setLanguage(languagesDataIdexes.get(position));
            holder.setCheckbox(languagesData.get(languagesDataIdexes.get(position)));
        }

        @Override
        public int getItemCount() {
            return languagesDataIdexes.size();
        }
    }

    class LanguagesViewHolder extends ViewHolder{

        View mView;
        public LanguagesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setLanguage(String language)
        {
            ((TextView)mView.findViewById(R.id.textviewSelectedLanguage)).setText(language);
        }

        public void setCheckbox(Boolean ischecked)
        {
            ((CheckBox)mView.findViewById(R.id.checkboxSelectedLanguage)).setChecked(ischecked);
        }
    }
}
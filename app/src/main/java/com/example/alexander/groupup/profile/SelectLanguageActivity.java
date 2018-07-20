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

    private ArrayList<LanguageBooleanMap> languagesData;

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

        languagesData = new ArrayList<>();
        languagesData.add(new LanguageBooleanMap("Arabic", false));
        languagesData.add(new LanguageBooleanMap("Bengali", false));
        languagesData.add(new LanguageBooleanMap("English", false));
        languagesData.add(new LanguageBooleanMap("French", false));
        languagesData.add(new LanguageBooleanMap("German", false));
        languagesData.add(new LanguageBooleanMap("Hindi", false));
        languagesData.add(new LanguageBooleanMap("Japanese", false));
        languagesData.add(new LanguageBooleanMap("Javanese", false));
        languagesData.add(new LanguageBooleanMap("Korean", false));
        languagesData.add(new LanguageBooleanMap("Mandarin", false));
        languagesData.add(new LanguageBooleanMap("Marathi", false));
        languagesData.add(new LanguageBooleanMap("Portuguese", false));
        languagesData.add(new LanguageBooleanMap("Punjabi", false));
        languagesData.add(new LanguageBooleanMap("Russian", false));
        languagesData.add(new LanguageBooleanMap("Spanish", false));
        languagesData.add(new LanguageBooleanMap("Tamil", false));
        languagesData.add(new LanguageBooleanMap("Telugu", false));
        languagesData.add(new LanguageBooleanMap("Turkish", false));
        languagesData.add(new LanguageBooleanMap("Urdu", false));
        languagesData.add(new LanguageBooleanMap("Vietnamese", false));

        adapter = new LanguagesAdapter(this, languagesData);

        Languages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String[] myLanguages = dataSnapshot.getValue().toString().split(", ");

                    for (String lang : myLanguages)
                        for(LanguageBooleanMap map : languagesData)
                            if(map.getLanguage().equals(lang))
                                map.setSelected(true);
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

        for(LanguageBooleanMap map : languagesData)
            if(map.getSelected())
                mylanguages += map.getLanguage() + ", ";

        if(mylanguages.length()>0)
            mylanguages = mylanguages.substring(0, mylanguages.length()-2);

        Languages.setValue(mylanguages);

        super.onBackPressed();
    }

    public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesViewHolder>{

        ArrayList<LanguageBooleanMap> languagesData = new ArrayList<>();
        Context c;

        public ArrayList<LanguageBooleanMap> getLanguagesData() {
            return languagesData;
        }

        public LanguagesAdapter(Context c, ArrayList<LanguageBooleanMap> languagesData)
        {
            this.languagesData = languagesData;
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
                    int currpos = languagesRecyclerView.getChildLayoutPosition(v);
                    LanguageBooleanMap map = languagesData.get(currpos);
                    map.setSelected(!map.getSelected());
                    languagesData.set(currpos, map);
                    notifyDataSetChanged();
                }
            });
            return new LanguagesViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull LanguagesViewHolder holder, final int position) {
            holder.setLanguage(languagesData.get(position).getLanguage());
            holder.setCheckbox(languagesData.get(position).getSelected());
        }

        @Override
        public int getItemCount() {
            return languagesData.size();
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

    private class LanguageBooleanMap {
        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public Boolean getSelected() {
            return isSelected;
        }

        public void setSelected(Boolean selected) {
            isSelected = selected;
        }

        public LanguageBooleanMap(String language, Boolean isSelected) {
            this.language = language;
            this.isSelected = isSelected;
        }

        private String language;
        private Boolean isSelected;
    }
}
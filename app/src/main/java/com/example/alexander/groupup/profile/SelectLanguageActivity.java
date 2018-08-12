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

        languagesData = new ArrayList<>();
        languagesData.add(new LanguageBooleanMap(getString(R.string.arabic), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.english), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.chinese), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.french), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.german), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.croatian), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.finnish), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.greek), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.hungarian), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.serbian), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.ukrainian), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.polish), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.korean), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.japanese), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.arabic), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.spanish), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.slovak), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.italian), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.vietnamese), false));
        languagesData.add(new LanguageBooleanMap(getString(R.string.swedish), false));

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

    //OnClicks
    public void backLanguages(View view) {
        super.onBackPressed();
    }

    public void addLanguagesClick(View view) {
        String mylanguages = "";

        for(LanguageBooleanMap map : languagesData)
            if(map.getSelected())
                mylanguages += map.getLanguage() + ", ";

        if(mylanguages.length()>0)
            mylanguages = mylanguages.substring(0, mylanguages.length()-2);

        Languages.setValue(mylanguages);
    }
}
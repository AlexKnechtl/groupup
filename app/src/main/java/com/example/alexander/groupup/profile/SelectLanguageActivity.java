package com.example.alexander.groupup.profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.models.LanguageStringsModel;
import com.example.alexander.groupup.singletons.LanguageStringsManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectLanguageActivity extends AppCompatActivity {

    private RecyclerView languagesRecyclerView = null;
    private DatabaseReference databaseRef;
    private ArrayList<DataBoleanMap> data;
    private DataAdapter adapter;
    private DatabaseReference hobbydb = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("hobbys");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laguage_select);

        String action = getIntent().getStringExtra("action");

        //if(getIntent().getStringExtra() != null && savedInstanceState.containsKey("action"))
        //action = savedInstanceState.getString("action");

        languagesRecyclerView = findViewById(R.id.recyclerviewSelectLanguages);
        languagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        data = new ArrayList<>();
        if(action == null || action.equals("language_select")) {
            databaseRef = hobbydb.child("languages");

            data.add(new DataBoleanMap(getString(R.string.arabic), false));
            data.add(new DataBoleanMap(getString(R.string.chinese), false));
            data.add(new DataBoleanMap(getString(R.string.croatian), false));
            data.add(new DataBoleanMap(getString(R.string.english), false));
            data.add(new DataBoleanMap(getString(R.string.finnish), false));
            data.add(new DataBoleanMap(getString(R.string.french), false));
            data.add(new DataBoleanMap(getString(R.string.german), false));
            data.add(new DataBoleanMap(getString(R.string.greek), false));
            data.add(new DataBoleanMap(getString(R.string.hungarian), false));
            data.add(new DataBoleanMap(getString(R.string.italian), false));
            data.add(new DataBoleanMap(getString(R.string.japanese), false));
            data.add(new DataBoleanMap(getString(R.string.korean), false));
            data.add(new DataBoleanMap(getString(R.string.polish), false));
            data.add(new DataBoleanMap(getString(R.string.serbian), false));
            data.add(new DataBoleanMap(getString(R.string.slovak), false));
            data.add(new DataBoleanMap(getString(R.string.spanish), false));
            data.add(new DataBoleanMap(getString(R.string.swedish), false));
            data.add(new DataBoleanMap(getString(R.string.ukrainian), false));
            data.add(new DataBoleanMap(getString(R.string.vietnamese), false));

        }
        else if(action.equals("music_select")){
            databaseRef = hobbydb.child("music");

            data.add(new DataBoleanMap("Country", false));
            data.add(new DataBoleanMap("Electronic/Dance", false));
            data.add(new DataBoleanMap("Folk", false));
            data.add(new DataBoleanMap("Hip Hop", false));
            data.add(new DataBoleanMap("Jazz", false));
            data.add(new DataBoleanMap("Klassisch", false));
            data.add(new DataBoleanMap("Latin", false));
            data.add(new DataBoleanMap("Metal", false));
            data.add(new DataBoleanMap("Pop", false));
            data.add(new DataBoleanMap("R&B", false));
            data.add(new DataBoleanMap("Rock", false));
        }
        else if(action.equals("sport_select"))
        {
            databaseRef = hobbydb.child("sport");
            FirebaseDatabase.getInstance().getReference().child("LanguageStrings").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot s : dataSnapshot.getChildren())
                    {
                        LanguageStringsModel lsm = s.getValue(LanguageStringsModel.class);
                        data.add(new DataBoleanMap(lsm.getLocalLanguageString(), false));
                        if(adapter != null)
                            adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            Log.e("Select something", "ERROR... Action \"" + action + "\" is not valid!!!");
            throw new IllegalArgumentException();
        }

        adapter = new DataAdapter(this, data);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String[] myLanguages = dataSnapshot.getValue().toString().split(", ");

                    for (String lang : myLanguages)
                        for(DataBoleanMap map : data)
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

    public class DataAdapter extends RecyclerView.Adapter<DataViewHolder>{
        ArrayList<DataBoleanMap> languagesData = new ArrayList<>();
        Context c;

        public ArrayList<DataBoleanMap> getLanguagesData() {
            return languagesData;
        }

        public DataAdapter(Context c, ArrayList<DataBoleanMap> languagesData)
        {
            this.languagesData = languagesData;
            this.c = c;
        }

        @NonNull
        @Override
        public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(c).inflate(R.layout.singlelayout_select_language, parent, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currpos = languagesRecyclerView.getChildLayoutPosition(v);
                    DataBoleanMap map = languagesData.get(currpos);
                    map.setSelected(!map.getSelected());
                    languagesData.set(currpos, map);
                    notifyDataSetChanged();
                }
            });
            return new DataViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull DataViewHolder holder, final int position) {
            holder.setLanguage(languagesData.get(position).getLanguage());
            holder.setCheckbox(languagesData.get(position).getSelected());
        }

        @Override
        public int getItemCount() {
            return languagesData.size();
        }
    }

    class DataViewHolder extends ViewHolder{
        View mView;
        public DataViewHolder(View itemView) {
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

    private class DataBoleanMap {
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

        public DataBoleanMap(String language, Boolean isSelected) {
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

        for(DataBoleanMap map : data)
            if(map.getSelected())
                mylanguages += map.getLanguage() + ", ";

        if(mylanguages.length() > 0)
            mylanguages = mylanguages.substring(0, mylanguages.length()-2);

        databaseRef.setValue(mylanguages);
        super.onBackPressed();
    }
}
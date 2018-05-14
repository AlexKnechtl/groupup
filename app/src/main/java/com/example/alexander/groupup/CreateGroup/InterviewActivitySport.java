package com.example.alexander.groupup.CreateGroup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.alexander.groupup.Models.LanguageStringsModel;
import com.example.alexander.groupup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by alexander on 26.03.18.
 */

public class InterviewActivitySport extends AppCompatActivity {

    //XML
    private RecyclerView sportsRecyclerView;
    private EditText searchEditText;

    //Variables
    String group;

    private ArrayList<LanguageStringsModel> sportItems;
    //private ArrayList<Integer> sportImages;
    private CustomSportAdapter adapter;

    private DatabaseReference mLanguageStringsReference = FirebaseDatabase.getInstance().getReference().child("LanguageStrings");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLanguageStringsReference.keepSynced(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_sports_activity);
        System.out.println("Test");
        //Get Information by Intent
        Bundle bundle = getIntent().getExtras();
        group = bundle.getString("group"); // Gruppen Kategorie


        sportItems = new ArrayList<>();
        //sportImages = new ArrayList<>();

        // ForEach through Items
        mLanguageStringsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    //System.out.println(snapshot.getKey());
                    LanguageStringsModel languageStrings = snapshot.getValue(LanguageStringsModel.class);
                    languageStrings.setId(snapshot.getKey());
                    sportItems.add(languageStrings);
                    adapter.notifyDataSetChanged();
                    //System.out.println(languageStrings.Deutsch);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
/*
        sportItems.add(getString(R.string.running));
        sportItems.add(getString(R.string.basketball));
        sportItems.add(getString(R.string.football));
        sportItems.add(getString(R.string.jogging));
        sportItems.add(getString(R.string.dance));
        sportItems.add(getString(R.string.biken));
        sportItems.add(getString(R.string.tennis));
        sportItems.add(getString(R.string.table_tennis));
        sportItems.add(getString(R.string.skateboarden));
        sportItems.add(getString(R.string.swimming));
        sportItems.add(getString(R.string.ice_skating));
        sportItems.add(getString(R.string.ice_hockey));
        sportItems.add(getString(R.string.fishing));
        sportItems.add(getString(R.string.volleyball));
        sportItems.add(getString(R.string.climbing));
        sportItems.add(getString(R.string.chess));
        sportItems.add(getString(R.string.baseball));
        sportItems.add(getString(R.string.martial_arts));
        sportItems.add(getString(R.string.gym));
        sportItems.add(getString(R.string.mountainbiken));*/
/*
        sportImages.add(R.drawable.sport_activity_run);
        sportImages.add(R.drawable.sport_activity_run);
        sportImages.add(R.drawable.sport_activity_soccer);
        sportImages.add(R.drawable.sport_activity_run);
        sportImages.add(R.drawable.sport_activity_run);
        sportImages.add(R.drawable.sport_activity_biking);
        sportImages.add(R.drawable.sport_activity_tennis);
        sportImages.add(R.drawable.sport_activity_run);
        sportImages.add(R.drawable.sport_activity_skateboarden);
        sportImages.add(R.drawable.sport_activity_swimming);
        sportImages.add(R.drawable.sport_activity_run);
        sportImages.add(R.drawable.sport_activity_run);
        sportImages.add(R.drawable.sport_activity_run);
        sportImages.add(R.drawable.sport_activity_run);
        sportImages.add(R.drawable.sport_activity_climbing);
        sportImages.add(R.drawable.sport_activity_run);
        sportImages.add(R.drawable.sport_activity_run);
        sportImages.add(R.drawable.sport_activity_run);
        sportImages.add(R.drawable.sport_activity_gym);
        sportImages.add(R.drawable.sport_activity_mountainbiken);
*/


        sportsRecyclerView = findViewById(R.id.recycler_view_sport);
        searchEditText = findViewById(R.id.search_sport_activitys);

        sportsRecyclerView.setHasFixedSize(true);
        sportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CustomSportAdapter(this, sportItems, group);

        sportsRecyclerView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //After the change calling the method and passing the search input
                filter(editable.toString());
            }
        });
    }

    private void filter(String text) {
        //New array list that will hold the filtered data
        ArrayList<LanguageStringsModel> filteredNames = new ArrayList<>();

        //Looping through existing elements
        for (LanguageStringsModel s : sportItems) {
            //If the existing elements contains the search input
            if (s.getLocalLanguageString().toLowerCase().contains(text.toLowerCase())) {
                //Adding the element to filtered list
                filteredNames.add(s);
            }
        }

        //Calling a method of the adapter class and passing the filtered list
        adapter.filterList(filteredNames);
    }
}
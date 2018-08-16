package com.example.alexander.groupup.interviews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.adapters.CustomSportAdapter;
import com.example.alexander.groupup.models.*;
import com.example.alexander.groupup.singletons.*;
import com.example.alexander.groupup.R;

import java.util.ArrayList;

/**
 * Created by alexander on 26.03.18.
 */

public class InterviewActivitySport extends BaseActivity {

    //XML
    private RecyclerView sportsRecyclerView;
    private EditText searchEditText;

    //Variables
    private GroupModel group;

    private ArrayList<LanguageStringsModel> sportItems;
    private CustomSportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_sports_activity);

        //Get Information by Intent
        Bundle bundle = getIntent().getExtras();
        group = (GroupModel) bundle.getSerializable("group"); // Gruppen Kategorie

        sportItems = LanguageStringsManager.getInstance().getLanguageStrings();

        sportsRecyclerView = findViewById(R.id.recycler_view_sport);
        searchEditText = findViewById(R.id.search_sport_activitys);

        sportsRecyclerView.setHasFixedSize(true);
        sportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CustomSportAdapter(this, sportItems);

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
package com.example.alexander.groupup.CreateGroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.alexander.groupup.MainActivities.HomeActivity;
import com.example.alexander.groupup.R;

public class InterviewBusinessNightlife extends AppCompatActivity {

    //XML
    private LinearLayout backLayout;
    private TextView headline, option1, option2, option3, option4;

    //Variables
    private String group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interview_newgroup);

        initializeXML();

        //Get Information by Intent
        Bundle bundle = getIntent().getExtras();
        group = bundle.getString("group"); // Gruppen Kategorie

        headline.setText("Was möchtest du mit deiner Gruppe machen?");

        if (group.equals("business")) {
            option1.setText("Veranstaltung");
            option2.setText("Seminar");
            option3.setText("Motivation");
            option4.setText("Connection Group");
        } else if(group.equals("nightlife")) {
            option1.setText("Disco");
            option2.setText("Pub / Bar");
            option3.setText("Shisha");
            option4.setText("Hausparty");
        }

        backLayout = findViewById(R.id.back_to_home);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterviewBusinessNightlife.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    //OnClicks
    public void sportClick(View view) {
        Intent intent = new Intent(InterviewBusinessNightlife.this, InterviewActivitySport.class);
        intent.putExtra("group", getString(R.string.sport));
        startActivity(intent);
    }

    public void leisureClick(View view) {
        Intent intent = new Intent(InterviewBusinessNightlife.this, InterviewActivitySport.class);
        intent.putExtra("group", getString(R.string.leisure));
        startActivity(intent);
    }

    public void nightlifeClick() {
        Intent intent = new Intent(InterviewBusinessNightlife.this, InterviewActivitySport.class);
        intent.putExtra("group", getString(R.string.nightlife));
        startActivity(intent);
    }

    public void businessClick(View view) {
        Intent intent = new Intent(InterviewBusinessNightlife.this, InterviewActivitySport.class);
        intent.putExtra("group", getString(R.string.business));
        startActivity(intent);
    }

    private void initializeXML() {
        headline = findViewById(R.id.headline_leisure_business);
        option1 = findViewById(R.id.option_one);
        option2 = findViewById(R.id.option_two);
        option3 = findViewById(R.id.optione_three);
        option4 = findViewById(R.id.option_four);
    }
}
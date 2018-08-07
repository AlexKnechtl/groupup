package com.example.alexander.groupup.singletons;

import android.util.Log;

import com.example.alexander.groupup.models.LanguageStringsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LanguageStringsManager {
    private static LanguageStringsManager ourInstance = null;
    private DatabaseReference languageStringsReference = FirebaseDatabase.getInstance().getReference().child("LanguageStrings");
    private ArrayList<LanguageStringsModel> languageStrings;
    private static boolean initialized = false;

    private LanguageStringsManager() {
        System.out.println("IN CONSTRUCTOR");
        languageStringsReference.keepSynced(true);
        languageStrings = new ArrayList<>();
        languageStringsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s: dataSnapshot.getChildren()) {
                    LanguageStringsModel languageString = s.getValue(LanguageStringsModel.class);
                    languageString.setId(s.getKey());
                    languageStrings.add(languageString);
                }
                initialized = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        System.out.println("OUT CONSTRUCTOR");
    }

    public static LanguageStringsManager getInstance() {
        if(!initialized || ourInstance == null)
            ourInstance = new LanguageStringsManager();
        int count = 0;
        while(!initialized || ourInstance == null){
            Log.w("LanguageStringsManager:", "In WHile LOOP");count++; if(count>20) {count = 0; ourInstance = new LanguageStringsManager();}};
        return ourInstance;
    }

    public static void initialize()
    {
        if(ourInstance == null)
            ourInstance = new LanguageStringsManager();
    }

    public LanguageStringsModel getLanguageStringByStringId(String id) {
        for (LanguageStringsModel s: languageStrings) {
            if(s.getId().equals(id))
                return s;
        }
//        return new LanguageStringsModel("ERROR"+id,"ERROR"+id,"ERROR"+id); //TODO
        return new LanguageStringsModel(id,id,id); //TODO
    }

    public ArrayList<LanguageStringsModel> getLanguageStrings()
    {
        return languageStrings;
    }

    public LanguageStringsModel getLanguageStringByLocalString(String localString) {
        for (LanguageStringsModel s: languageStrings) {
            if(s.getLocalLanguageString().equals(localString))
                return s;
        }
        return new LanguageStringsModel("ERROR"+localString,"ERROR"+localString,"ERROR"+localString); //TODO
    }

    public boolean isInitialized(){return initialized;}
}

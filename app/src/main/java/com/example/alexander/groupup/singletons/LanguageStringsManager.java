package com.example.alexander.groupup.singletons;

import android.content.Context;
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
    private static Context context;

    private LanguageStringsManager() {
        //System.out.println("IN CONSTRUCTOR");
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
        //System.out.println("OUT CONSTRUCTOR");
    }

    public static LanguageStringsManager getInstance() {
        if(!initialized || ourInstance == null)
            ourInstance = new LanguageStringsManager();
        while(!initialized || ourInstance == null){
            Log.w("LanguageStringsManager:", "In WHile LOOP");};
        return ourInstance;
    }

    public static void initialize(Context context)
    {
        LanguageStringsManager.context = context;
        if(ourInstance == null)
            ourInstance = new LanguageStringsManager();
    }

    private String getStringResourceByName(String aString) {
        String packageName = context.getPackageName();
        int resId = context.getResources()
                .getIdentifier(aString, "string", packageName);
        if (resId == 0) {
            return aString;
        } else {
            return context.getString(resId);
        }
    }

    public LanguageStringsModel getLanguageStringByStringId(String id) {
        for (LanguageStringsModel s: languageStrings) {
            if(s.getId().equals(id))
                return s;
        }
//        return new LanguageStringsModel("ERROR"+id,"ERROR"+id,"ERROR"+id); //TODO
        String str = getStringResourceByName(id);
        LanguageStringsModel lm =  new LanguageStringsModel(str,str,str);
        lm.setId(id);
        return lm; //TODO
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

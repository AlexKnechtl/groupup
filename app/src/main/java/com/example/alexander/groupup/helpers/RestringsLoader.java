package com.example.alexander.groupup.helpers;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ice.restring.Restring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestringsLoader implements Restring.StringsLoader {
    HashMap<String, HashMap<String, String>> langStings;

    public RestringsLoader(){
        langStings = new HashMap<>();
        DatabaseReference r = FirebaseDatabase.getInstance().getReference().child("Strings");
        r.keepSynced(true);
        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot sn : dataSnapshot.getChildren()) {
                    HashMap<String, String> map = new HashMap<>();
                    for (DataSnapshot str : sn.getChildren()) {
                        map.put(str.getKey(), str.getValue().toString());
                    }
                    langStings.put(sn.getKey(), map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public List<String> getLanguages() {
        ArrayList<String> keys = new ArrayList<>();
        for(String s : langStings.keySet())
        {
            keys.add(s);
        }
        return keys;
    }

    @Override
    public Map<String, String> getStrings(String language) {
        return langStings.get(language);
    }
}

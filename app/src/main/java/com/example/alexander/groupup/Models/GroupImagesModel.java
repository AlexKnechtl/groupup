package com.example.alexander.groupup.Models;

import com.example.alexander.groupup.Helpers.OnGetResultListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class GroupImagesModel {

    private static DatabaseReference sportImageMapReference = FirebaseDatabase.getInstance().getReference().child("SportImageMap");

    public static void getRandomImageURL(String activity, final OnGetResultListener<String> listener) {
        sportImageMapReference.keepSynced(true);
        DatabaseReference reference;
        try {
            reference = sportImageMapReference.child(activity);
//            System.out.println("\n\n------------------ Out TRY");
        } catch (Exception e) {
            reference = sportImageMapReference.child("default");
//            System.out.println("\n\n------------------ In Catch");
        }
//        System.out.println("\n\n------------------ finally");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> imageURLs = new ArrayList<>();
//              System.out.print("-------ParentKey: " + dataSnapshot.getKey());
//              System.out.println("------Value: " + dataSnapshot.getValue());

                try {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        imageURLs.add(s.getValue().toString());
                        System.out.println("----" + s.getValue());
                    }

//                  System.out.println("\n------------------ ArraySize: " + imageURLs.size());

                    Random rnd = new Random();
                    listener.OnSuccess(imageURLs.get(rnd.nextInt(imageURLs.size())));
                } catch (Exception e) {
                    sportImageMapReference.child("default").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> imageURLs = new ArrayList<>();
//                          System.out.print("-------ParentKey: " + dataSnapshot.getKey());
//                          System.out.println("------Value: " + dataSnapshot.getValue());
                            for (DataSnapshot s : dataSnapshot.getChildren()) {
                                imageURLs.add(s.getValue().toString());
                                System.out.println("----" + s.getValue());
                            }

//                          System.out.println("\n------------------ ArraySize: " + imageURLs.size());

                            Random rnd = new Random();
                            listener.OnSuccess(imageURLs.get(rnd.nextInt(imageURLs.size())));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

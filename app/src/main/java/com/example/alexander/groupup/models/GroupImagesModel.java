package com.example.alexander.groupup.models;

import com.example.alexander.groupup.helpers.OnGetResultListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class GroupImagesModel {

    private static DatabaseReference sportImageMapReference = FirebaseDatabase.getInstance().getReference().child("GroupImages");
    private static DatabaseReference imageMapReference = FirebaseDatabase.getInstance().getReference().child("GroupImages");

    public static void getRandomSportImageURL(String activity, final OnGetResultListener<String> listener) {
        sportImageMapReference.keepSynced(true);
        DatabaseReference reference = sportImageMapReference.child(activity);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> imageURLs = new ArrayList<>();

                try {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        imageURLs.add(s.getValue().toString());
                        System.out.println("----" + s.getValue());
                    }

                    Random rnd = new Random();
                    listener.OnSuccess(imageURLs.get(rnd.nextInt(imageURLs.size())));
                } catch (Exception e) {
                    sportImageMapReference.child("default").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> imageURLs = new ArrayList<>();
                            for (DataSnapshot s : dataSnapshot.getChildren()) {
                                imageURLs.add(s.getValue().toString());
                                System.out.println("----" + s.getValue());
                            }

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

    public static void getRandomImageURL(String activity, GroupType groupType, final OnGetResultListener<String> listener) {

        imageMapReference.keepSynced(true);
        String childstr = "";
        switch (groupType) {
            case leisure:
                childstr = "leisure";
                break;
            default:
                childstr = activity;
                break;
        }

        imageMapReference.child(childstr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> imageURLs = new ArrayList<>();
                try {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        imageURLs.add(s.getValue().toString());
                        System.out.println("----" + s.getValue());
                    }

                    Random rnd = new Random();
                    int i = rnd.nextInt(imageURLs.size());
                    listener.OnSuccess(imageURLs.get(i));
                } catch (Exception e) {
                    imageMapReference.child("default").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> imageURLs = new ArrayList<>();
                            for (DataSnapshot s : dataSnapshot.getChildren()) {
                                imageURLs.add(s.getValue().toString());
                                System.out.println("----" + s.getValue());
                            }

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
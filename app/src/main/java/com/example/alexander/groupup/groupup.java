package com.example.alexander.groupup;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.example.alexander.groupup.singletons.LanguageStringsManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

public class groupup extends Application {

    private DatabaseReference UserDatabase;
    private FirebaseAuth user_id;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        LanguageStringsManager.initialize(getApplicationContext());

        //Enable crashlytics in debug mode
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)           // Enables Crashlytics debugger
                .build();
        Fabric.with(fabric);

        /*user_id = FirebaseAuth.getInstance();
        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id.getCurrentUser().getUid());

        UserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    UserDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    UserDatabase.child("online").setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }
}
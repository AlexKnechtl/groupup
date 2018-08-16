package com.example.alexander.groupup;

import android.app.Application;

import com.example.alexander.groupup.helpers.RestringsLoader;
import com.example.alexander.groupup.singletons.LanguageStringsManager;
import com.google.firebase.database.FirebaseDatabase;
import com.ice.restring.Restring;
import com.ice.restring.RestringConfig;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class groupup extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

//        Restring.init(this, new RestringConfig.Builder().persist(true).stringsLoader(new RestringsLoader()).build());

        LanguageStringsManager.initialize();
    }
}
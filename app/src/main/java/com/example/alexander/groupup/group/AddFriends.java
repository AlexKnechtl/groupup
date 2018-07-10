package com.example.alexander.groupup.group;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import android.support.v7.widget.Toolbar;

import com.example.alexander.groupup.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class AddFriends extends AppCompatActivity {

    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        Toolbar toolbar = findViewById(R.id.toolbar_add_friends);
        searchView = findViewById(R.id.material_search_view);

        setSupportActionBar(toolbar);

        MaterialSearchView searchView = findViewById(R.id.material_search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
    }

    //Add Search Menu Icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.material_searchbar, menu);

        MenuItem item = menu.findItem(R.id.material_search_icon);
        searchView.setMenuItem(item);

        return true;
    }
}

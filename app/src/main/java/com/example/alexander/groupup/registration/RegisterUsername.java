package com.example.alexander.groupup.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.singletons.LanguageStringsManager;

public class RegisterUsername extends AppCompatActivity {

    //XML
    private EditText editFirstName, editSurname, editUsername;

    //Variables
    private String firstName, surname, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageStringsManager.initialize();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_username);

        editFirstName = findViewById(R.id.first_name_edit);
        editSurname = findViewById(R.id.surname_edit);
        editUsername = findViewById(R.id.username_edit);
    }

    public void continueRegister1 (View view) {
        firstName = editFirstName.getText().toString();
        surname = editSurname.getText().toString();
        username = editUsername.getText().toString();

        Intent intent = new Intent(RegisterUsername.this, RegisterAgeCity.class);
        intent.putExtra("username", username);
        intent.putExtra("name", firstName + " " + surname);
        startActivity(intent);
    }
}
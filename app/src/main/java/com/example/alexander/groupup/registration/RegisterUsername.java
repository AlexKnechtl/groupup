package com.example.alexander.groupup.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.singletons.LanguageStringsManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUsername extends BaseActivity {

    //XML
    private EditText editFirstName, editSurname, editUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_username);

        editFirstName = findViewById(R.id.first_name_edit);
        editSurname = findViewById(R.id.surname_edit);
        editUsername = findViewById(R.id.username_edit);
    }

    public void continueRegister1(View view) {
        String firstName = editFirstName.getText().toString().toLowerCase();
        firstName = firstName.replace(" ", "");
        firstName = firstName.substring(0,1).toUpperCase() + firstName.substring(1);

        String surname = editSurname.getText().toString().toLowerCase();
        surname = surname.replace(" ", "");
        surname = surname.substring(0,1).toUpperCase() + surname.substring(1);

        String username = editUsername.getText().toString().toLowerCase();
        username.replace(" ", "");

        Toast.makeText(this, firstName + surname + username, Toast.LENGTH_SHORT).show();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(surname) || TextUtils.isEmpty(username)) {
            Toast.makeText(this, R.string.information_missing, Toast.LENGTH_SHORT).show();

        } else {
            Intent intent = new Intent(RegisterUsername.this, RegisterAgeCity.class);
            intent.putExtra("username", username);
            intent.putExtra("name", firstName + " " + surname);
            startActivity(intent);
        }
    }
}
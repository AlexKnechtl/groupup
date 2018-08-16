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

public class RegisterUsername extends BaseActivity {

    //XML
    private EditText editFirstName, editSurname, editUsername;

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
        String firstName = editFirstName.getText().toString();
        String surname = editSurname.getText().toString();
        String username = editUsername.getText().toString().toLowerCase();

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
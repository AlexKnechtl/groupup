package com.example.alexander.groupup.registration;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.singletons.LanguageStringsManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.Date;

public class RegisterAgeCity extends BaseActivity {

    //XML
    private TextView cityTextView, birthdayTextView;

    //Variables
    private String city, birthday_day, birthday_year, username, name;
    private boolean oldEnough = false;

    //Date Picker
    private Calendar birthday_user;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    //Constants
    private static final String TAG = "RegisterAgeCity";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    //Firebase
    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageStringsManager.initialize();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_age_city);

        getGroupInformation();

        birthdayTextView = findViewById(R.id.birthday_tv);
        cityTextView = findViewById(R.id.city_tv);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Log.d(TAG, "onDateSet: dd/mm/yyy " + day + "/" + month + "/" + year);

                birthday_user = Calendar.getInstance();
                Calendar today = Calendar.getInstance();

                birthday_user.setTime(new Date());
                birthday_user.set(year, month, day);

                birthday_day = String.valueOf(birthday_user.get(Calendar.DAY_OF_YEAR));
                birthday_year = String.valueOf(birthday_user.get(Calendar.YEAR));

                month = month + 1;
                String date = day + "." + month + "." + year;

                int userage = today.get(Calendar.YEAR) - birthday_user.get(Calendar.YEAR);

                if (today.get(Calendar.DAY_OF_YEAR) < birthday_user.get(Calendar.DAY_OF_YEAR)) {
                    userage--;
                }

                if (userage > 11) {
                    birthdayTextView.setText(date);
                    oldEnough = true;
                } else {
                    birthdayTextView.setText(getString(R.string.too_young));
                }
            }
        };
    }

    public void yourCityClick(View view) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {

        } catch (GooglePlayServicesNotAvailableException e) {

        }
    }

    public void yourAgeClick(View view) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(RegisterAgeCity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(this, data);
                city = "" + place.getName();
                cityTextView.setText(city);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void getGroupInformation() {
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        name = bundle.getString("name");
    }

    public void continueRegister2(View view) {
        if (oldEnough) {
            if (!TextUtils.isEmpty(city)) {
                Intent intent = new Intent(RegisterAgeCity.this, RegistrationImage.class);
                intent.putExtra("name", name);
                intent.putExtra("username", username);
                intent.putExtra("city", city);
                intent.putExtra("age_day", birthday_day);
                intent.putExtra("age_year", birthday_year);
                startActivity(intent);
            }

        } else if (!oldEnough) {
            Toast.makeText(RegisterAgeCity.this, getString(R.string.not_possible), Toast.LENGTH_SHORT).show();
        }
    }
}
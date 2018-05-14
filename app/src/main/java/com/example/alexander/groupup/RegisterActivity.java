package com.example.alexander.groupup;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.groupup.MainActivities.HomeActivity;
import com.example.alexander.groupup.Singletons.LanguageStringsManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.alexander.groupup.MainActivities.HomeActivity.ANONYMOUS;

/**
 * Created by alexk on 20.02.2018.
 */

public class RegisterActivity extends AppCompatActivity {

    //XML
    private EditText mProfileName;
    private TextView mProfileBirthday, mLocation;
    private Button mRegButton;
    private ProgressBar progressBar;
    private TextInputLayout textInputLayout;
    private RelativeLayout registerLayout;

    //Variables
    private String mUsername, city, state, birthday_day, birthday_year, age;
    private boolean gpsStatus;
    public boolean oldEnough = false;

    //Date Picker
    private Calendar birthday_user;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    //Constants
    private static final String TAG = "RegisterActivity";
    private static final int MY_PERMISSION_REQUEST_LOCATION = 1;
    private static final int RC_SIGN_IN = 1;

    //Firebase
    private DatabaseReference myRef;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageStringsManager.initialize();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Close when Back is pressed in MainActivity
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        //Initialize Firebase
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsername = ANONYMOUS;
        mFireBaseAuth = FirebaseAuth.getInstance();

        //Find IDs
        textInputLayout = findViewById(R.id.usernameWrapper);
        mRegButton = findViewById(R.id.button_reg);
        mProfileName = findViewById(R.id.username);
        mProfileBirthday = findViewById(R.id.reg_profile_birthday);
        mLocation = findViewById(R.id.reg_location);
        registerLayout = findViewById(R.id.register_layout);
        progressBar = findViewById(R.id.progress_bar_register);

        textInputLayout.setHint("Username");

        //Registration Process
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    //user is signed out
                    registerLayout.setVisibility(View.VISIBLE);
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setTheme(R.style.AppTheme)
                                    .setLogo(R.drawable.logo_groupup)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (oldEnough) {
                    String profile_name = mProfileName.getText().toString();
                    String profile_birthday = mProfileBirthday.getText().toString();
                    String profile_location = mLocation.getText().toString();

                    if (!TextUtils.isEmpty(profile_name) || !TextUtils.isEmpty(profile_birthday) || !TextUtils.isEmpty(profile_location)) {
                        progressBar.setVisibility(View.VISIBLE);

                        register_user(profile_name, profile_birthday);
                    }

                } else if (!oldEnough) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.not_possible), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mProfileBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);


                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

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

                age = ((Integer) userage).toString();

                if (userage > 11) {
                    mProfileBirthday.setText(date);
                    oldEnough = true;
                } else {
                    mProfileBirthday.setText(getString(R.string.too_young));
                }
            }
        };

        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (gpsStatus) {

                    if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this
                                , Manifest.permission.ACCESS_FINE_LOCATION)) {
                            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
                        } else {
                            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
                        }
                    } else {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {
                            mLocation.setText(getCity(location.getLatitude(), location.getLongitude()) + ", " + getState(location.getLatitude(), location.getLongitude()));
                        } else {
                            Toast.makeText(RegisterActivity.this, getString(R.string.activate_gps_manually), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {

                    if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this
                                , Manifest.permission.ACCESS_FINE_LOCATION)) {
                            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
                        } else {
                            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
                        }
                    }

                    Toast.makeText(RegisterActivity.this, getString(R.string.activate_gps), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }

            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        try {
                            mLocation.setText(getCity(location.getLatitude(), location.getLongitude()) + ", " + getState(location.getLatitude(), location.getLongitude()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, getString(R.string.not_found_location), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_permission), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void register_user(final String profile_name, final String profile_birthday) {
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("name", profile_name);
        userMap.put("city", city);
        userMap.put("state", state);
        userMap.put("image", "default");
        userMap.put("age", age);
        userMap.put("birthday", profile_birthday);
        userMap.put("age_day", birthday_day);
        userMap.put("age_year", birthday_year);
        userMap.put("thumb_image", "default");
        userMap.put("friends_count", "0");

        mUserDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    progressBar.setVisibility(View.INVISIBLE);

                    Toast.makeText(RegisterActivity.this, getString(R.string.register_succes), Toast.LENGTH_SHORT).show();

                    Intent mainIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this, getString(R.string.cannot_sign_in), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getCity(double lat, double lon) {
        String currentCity = "";

        Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocation(lat, lon, 1);
            if (addressList.size() > 0) {
                currentCity = addressList.get(0).getLocality();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        city = currentCity;
        return currentCity;
    }

    public String getState(double lat, double lon) {
        String currentState = "";

        Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocation(lat, lon, 1);
            if (addressList.size() > 0) {
                currentState = addressList.get(0).getAdminArea();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        state = currentState;
        return currentState;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {

                if (!(mUsername.equals(ANONYMOUS))) {
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    startActivity(intent);
                }

                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFireBaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFireBaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void onSignedInInitialize(String username) {
        mUsername = username;

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = current_user.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(uid).exists()) {
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    mUserDatabase.child(uid).child("device_token").setValue(deviceToken);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(uid).child("name").exists()) {
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }
    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
    }
}
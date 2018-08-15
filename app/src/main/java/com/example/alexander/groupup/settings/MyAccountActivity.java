package com.example.alexander.groupup.settings;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * Created by alexk on 24.02.2018.
 */

public class MyAccountActivity extends BaseActivity {

    //XML
    private Button mBtnChangeName, mBtnChangeBirthday;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    //Constants
    private static final String TAG = "MyAccountActivity";

    //Variables
    public boolean oldEnough = false;

    //Firebase
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);

        //Initialize Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        //Find IDs
        mBtnChangeName = findViewById(R.id.btn_changeName);
        mBtnChangeBirthday = findViewById(R.id.btn_changeBirthday);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String age = dataSnapshot.child("age").getValue().toString();

                mBtnChangeName.setText("- " + getString(R.string.name) + " " + name);
                mBtnChangeBirthday.setText("- " + getString(R.string.age) + " " + age + getString(R.string.years));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Log.d(TAG, "onDateSet: dd/mm/yyy " + day + "/" + month + "/" + year);
                month = month + 1;
                String date = day + "." + month + "." + year;

                Calendar birthday = Calendar.getInstance();
                Calendar today = Calendar.getInstance();

                birthday.set(year, month, day);

                int age = today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);

                if (today.get(Calendar.DAY_OF_YEAR) < birthday.get(Calendar.DAY_OF_YEAR)) {
                    age--;
                }

                Integer ageInt = new Integer(age);

                if (ageInt > 11) {
                    mBtnChangeBirthday.setText(getString(R.string.birthday) + " " + date);
                    oldEnough = true;

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    mUserDatabase.child("changeBirth").setValue("1");
                    mUserDatabase.child("age").setValue(date);

                } else {
                    mBtnChangeBirthday.setText("Tut uns leid, du bist zu jung.");
                }
            }
        };

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    public void changeNameClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle(getString(R.string.change_name));

// Set up the input
        ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#26555a"));

        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(12, 14, 12, 14);

        final EditText input = new EditText(this);
        input.setTextColor(Color.BLACK);
        input.setHint("Neuer Name: ");
        input.setHintTextColor(Color.GRAY);
        input.setBackgroundTintList(colorStateList);
        input.setLayoutParams(params);

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBtnChangeName.setText(getString(R.string.name) + " " + input.getText().toString());
                mUserDatabase.child("name").setValue(input.getText().toString());
            }
        });
        builder.show();
    }

    public void changeBirthdayClick(View view) {

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("changeBirth").exists()) {
                    Toast.makeText(MyAccountActivity.this, "Du hast dein Geburtsdatum schon einmal geändert", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(MyAccountActivity.this, R.style.Theme_AppCompat_Dialog_Alert);
                    builder.setTitle(getString(R.string.note))
                            .setMessage(getString(R.string.note_change_birthday))
                            .setPositiveButton(getString(R.string.yes_continue), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Calendar cal = Calendar.getInstance();
                                    int year = cal.get(Calendar.YEAR);
                                    int month = cal.get(Calendar.MONTH);
                                    int day = cal.get(Calendar.DAY_OF_MONTH);

                                    DatePickerDialog dialog = new DatePickerDialog(MyAccountActivity.this,
                                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                            mDateSetListener,
                                            year, month, day);

                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog.show();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
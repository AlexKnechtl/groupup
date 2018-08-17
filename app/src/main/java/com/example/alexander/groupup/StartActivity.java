package com.example.alexander.groupup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.registration.RegisterUsername;
import com.example.alexander.groupup.singletons.LanguageStringsManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;

import static com.example.alexander.groupup.main.HomeActivity.ANONYMOUS;

public class StartActivity extends BaseActivity {

    public static boolean IS_APP_RUNNING =false;

    //Constants
    private static final int RC_SIGN_IN = 1;

    //Firebase
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //Variables
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_view);
        IS_APP_RUNNING = true;
        Log.w("IS_APP_RUNNING: ", "TRUE-------------");
        //Close when Back is pressed in MainActivity
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        mFireBaseAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsername = ANONYMOUS;

        //Registration Process
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //User is signed in
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    //User is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false, true)
                                    .setTheme(R.style.RegisterTheme)
                                    .setLogo(R.drawable.logo_groupup)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                if (!(mUsername.equals(ANONYMOUS))) {
                    Intent intent = new Intent(StartActivity.this, HomeActivity.class);
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

        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(uid).exists()) {
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    String deviceToken = instanceIdResult.getToken();
                                    mUserDatabase.child(uid).child("device_token").setValue(deviceToken);
                                    Intent intent = new Intent(StartActivity.this, HomeActivity.class);
                                    intent.putExtra("user_id", uid);
                                    startActivity(intent);
                                }
                            });
                }else {
                    Intent intent = new Intent(StartActivity.this, RegisterUsername.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IS_APP_RUNNING = false;
        Log.w("APP_RUNNING: ", "FALSE-------------------------");
    }
}
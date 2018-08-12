package com.example.alexander.groupup.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.groupup.profile.FriendsActivity;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.profile.SelectLanguageActivity;
import com.example.alexander.groupup.settings.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ProfileActivity extends AppCompatActivity {

    //XML
    private CircleImageView mProfileImageView;
    private TextView mProfileName, mProfileLocation, mProfileStatus, friendsCounter, languagesTextView;
    private RelativeLayout relativeLayout, languages;
    private FloatingActionButton fabSettings;

    //Constants
    private static final int GALLERY_PICK = 1;
    private static final int ACTIVITY_NUM = 3;

    //Popup
    private String status;
    private PopupWindow mPopupWindow;

    //Firebase
    private DatabaseReference UserDatabase;
    private StorageReference ProfileImageStorage;

    //Variables
    private Context mContext = getApplicationContext();
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_profile);

        Bundle bundle = getIntent().getExtras();
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        initializeXML();
        setupBottomNavigationView();

        ProfileImageStorage = FirebaseStorage.getInstance().getReference();
        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        UserDatabase.keepSynced(true);

        UserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String location = dataSnapshot.child("city").getValue().toString();
                String age_day = dataSnapshot.child("age_day").getValue().toString();
                String age_year = dataSnapshot.child("age_year").getValue().toString();
                String friends_count = dataSnapshot.child("friends_count").getValue().toString();

                languagesTextView.setText(dataSnapshot.child("Languages").getValue().toString());
                if (languagesTextView.getText().equals(""))
                    languagesTextView.setText("Keine Sprachen");

                final String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(ProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.profile_white_border).into(mProfileImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.profile_white_border).into(mProfileImageView);
                    }
                });

                if (dataSnapshot.child("status").exists()) {
                    status = dataSnapshot.child("status").getValue().toString();
                    mProfileStatus.setText(status);
                }

                int age_day_value = Integer.parseInt(age_day);
                int age_year_value = Integer.parseInt(age_year);

                Calendar today = Calendar.getInstance();

                int age = today.get(Calendar.YEAR) - age_year_value;

                if (today.get(Calendar.DAY_OF_YEAR) < age_day_value)

                {
                    age--;
                }

                String ageUser = Integer.toString(age);
                UserDatabase.child("age").

                        setValue(ageUser);

                mProfileName.setText(name + ", " + age);
                mProfileLocation.setText(location);
                friendsCounter.setText(friends_count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        friendsCounter.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FriendsActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });

        mProfileStatus.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                // Initialize a new instance of LayoutInflater service
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

                View customView = inflater.inflate(R.layout.popup_edittext, null);

                final EditText editStatus = customView.findViewById(R.id.edit_text_popup);
                final TextView countCharactersText = customView.findViewById(R.id.count_characters);

                editStatus.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            status = editStatus.getText().toString();
                            mProfileStatus.setText(status);
                            UserDatabase.child("status").setValue(status);
                            mPopupWindow.dismiss();
                            return true;
                        }
                        return false;
                    }
                });

                if (status != null) {
                    editStatus.setText(status);
                    editStatus.setHorizontallyScrolling(false);
                    editStatus.setLines(6);
                    countCharactersText.setText(status.length() + " / 220");
                }

                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

                ImageButton closeButton = customView.findViewById(R.id.close_popup);

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                    }
                });

                TextWatcher mTextEditorWatcher = new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        countCharactersText.setText(String.valueOf(s.length()) + " / 220");
                    }

                    public void afterTextChanged(Editable s) {
                    }
                };

                mPopupWindow.setFocusable(true);
                editStatus.addTextChangedListener(mTextEditorWatcher);

                mPopupWindow.showAtLocation(relativeLayout, Gravity.START, 0, 0);
            }
        });

        mProfileImageView.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        languages.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent selectLanguages = new Intent(ProfileActivity.this, SelectLanguageActivity.class);
                startActivity(selectLanguages);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                final File thumb_filePath = new File(resultUri.getPath());

                final Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(130)
                        .setMaxHeight(130)
                        .setQuality(30)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filepath = ProfileImageStorage.child("profile_images").child(user_id + ".jpg");
                final StorageReference thumb_filepath = ProfileImageStorage.child("profile_images").child("thumbs").child(user_id + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()) {

                                        Map update_hashMap = new HashMap<>();
                                        update_hashMap.put("image", download_url);
                                        update_hashMap.put("thumb_image", thumb_downloadUrl);

                                        UserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ProfileActivity.this, "Profile picture updated.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Sorry, that shouldn´t happen", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(ProfileActivity.this, "Sorry, that shouldn´t happen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void showSettings(View view) {
        Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void initializeXML() {
        languagesTextView = findViewById(R.id.languages_interest_text_view);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileImageView = findViewById(R.id.user_image);
        mProfileName = findViewById(R.id.profile_name_textview);

        mProfileLocation = findViewById(R.id.profile_location);
        relativeLayout = findViewById(R.id.coordinator_layout_profile);
        languages = findViewById(R.id.aboutme_languages);
        friendsCounter = findViewById(R.id.friends_counter_profile);
    }

    public void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext, user_id, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Languages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    languagesTextView.setText(dataSnapshot.getValue().toString());
                    if (languagesTextView.getText().equals(""))
                        languagesTextView.setText("Keine Sprachen");
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Languages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    languagesTextView.setText(dataSnapshot.getValue().toString());
                    if (languagesTextView.getText().equals(""))
                        languagesTextView.setText("Keine Sprachen");
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Languages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    languagesTextView.setText(dataSnapshot.getValue().toString());
                    if (languagesTextView.getText().equals(""))
                        languagesTextView.setText("Keine Sprachen");
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
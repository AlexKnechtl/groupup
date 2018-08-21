package com.example.alexander.groupup.registration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.main.HomeActivity;
import com.example.alexander.groupup.main.ProfileActivity;
import com.example.alexander.groupup.singletons.LanguageStringsManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class RegistrationImage extends BaseActivity {

    //XML
    private CircleImageView profilePicture;
    private ProgressBar progressBar;

    //Variables
    private String city, birthday_day, birthday_year, username, name, download_url, thumb_download_url;

    //Constants
    private static final int GALLERY_PICK = 1;

    //Firebase
    private DatabaseReference UserDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference ProfileImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_image);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String current_uid = mCurrentUser.getUid();

        ProfileImageStorage = FirebaseStorage.getInstance().getReference();

        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        UserDatabase.keepSynced(true);

        getGroupInformation();

        profilePicture = findViewById(R.id.registration_image);
        progressBar = findViewById(R.id.progress_bar_register);

        UserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("image").exists()) {
                    final String image = dataSnapshot.child("image").getValue().toString();
                    Picasso.with(RegistrationImage.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.profile_white_border).into(profilePicture, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(RegistrationImage.this).load(image).placeholder(R.drawable.profile_white_border).into(profilePicture);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Toast.makeText(this, "Bild wird hochgeladen.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);

                Uri resultUri = result.getUri();
                String current_user_id = mCurrentUser.getUid();

                final File thumb_filePath = new File(resultUri.getPath());

                final Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(130)
                        .setMaxHeight(130)
                        .setQuality(30)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                final byte[] thumb_byte = baos.toByteArray();

                final StorageReference filepath = ProfileImageStorage.child("profile_images").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath = ProfileImageStorage.child("thumb_images").child(current_user_id + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    download_url = task.getResult().toString();
                                }
                            });

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    thumb_filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            thumb_download_url = task.getResult().toString();
                                            Map update_hashMap = new HashMap<>();
                                            update_hashMap.put("image", download_url);
                                            update_hashMap.put("thumb_image", thumb_download_url);

                                            UserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RegistrationImage.this, "Profile picture updated.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegistrationImage.this, "Sorry, that shouldn´t happen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void chooseImageClick(View view) {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
    }

    private void getGroupInformation() {
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        name = bundle.getString("name");
        city = bundle.getString("city");
        birthday_day = bundle.getString("age_day");
        birthday_year = bundle.getString("age_year");
    }

    public void continueRegister2(View view) {
        progressBar.setVisibility(View.VISIBLE);
        register_user();
        Intent intent = new Intent(RegistrationImage.this, HomeActivity.class);
        startActivity(intent);
    }

    private void register_user() {
        Map userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("name", name);
        userMap.put("city", city);
        userMap.put("age_day", birthday_day);
        userMap.put("age_year", birthday_year);
        userMap.put("friends_count", "0");

        UserDatabase.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);

                    Toast.makeText(RegistrationImage.this, getString(R.string.register_succes), Toast.LENGTH_SHORT).show();

                    Intent mainIntent = new Intent(RegistrationImage.this, HomeActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegistrationImage.this, getString(R.string.cannot_sign_in), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
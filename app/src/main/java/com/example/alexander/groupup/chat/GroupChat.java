package com.example.alexander.groupup.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.adapters.MessagesAdapter;
import com.example.alexander.groupup.models.MessagesModel;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChat extends AppCompatActivity {

    //XML
    private CircleImageView userImage;
    private TextView userName;
    private EditText messageText;
    private RecyclerView messagesRecyclerView;

    //Variables
    private String user_id, receiver_user_id;
    private final List<MessagesModel> messagesList = new ArrayList<>();
    private MessagesAdapter messagesAdapter;

    //Firebase
    private DatabaseReference ChatDatabase;
    private DatabaseReference UserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_activity);

        Bundle bundle = getIntent().getExtras();
        receiver_user_id = bundle.getString("receiver_user_id");
        user_id = bundle.getString("user_id");

    }
}

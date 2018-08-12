package com.example.alexander.groupup.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alexander.groupup.adapters.MessagesAdapter;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.main.ChatActivity;
import com.example.alexander.groupup.models.MessagesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SingleChat extends AppCompatActivity {

    //XML
    private CircleImageView userImage;
    private TextView userName;
    private EditText messageText;
    private RecyclerView messagesRecyclerView;

    //Variables
    private String user_id, receiver_user_id, date;
    private final List<MessagesModel> messagesList = new ArrayList<>();
    private MessagesAdapter messagesAdapter;

    //Firebase
    private DatabaseReference ChatDatabase;
    private DatabaseReference UserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_single);

        Bundle bundle = getIntent().getExtras();
        receiver_user_id = bundle.getString("receiver_user_id");
        user_id = bundle.getString("user_id");

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        date = day + "." + month + "." + year;

        //Find IDs
        userImage = findViewById(R.id.chat_user_image);
        userName = findViewById(R.id.chat_user_name);
        messageText = findViewById(R.id.single_chat_et);
        messagesRecyclerView = findViewById(R.id.single_chat_list);

        //Set Adapter
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        messagesAdapter = new MessagesAdapter(messagesList);

        messagesRecyclerView.setAdapter(messagesAdapter);

        //Initialize FireBase
        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatDatabase = FirebaseDatabase.getInstance().getReference().child("Chats");
        ChatDatabase.keepSynced(true);

        loadMessages();

        UserDatabase.child(receiver_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();

                final String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(SingleChat.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.profile_white_border).into(userImage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Picasso.with(SingleChat.this).load(image).placeholder(R.drawable.profile_white_border).into(userImage);
                    }
                });
                userName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ChatDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiver_user_id)) {

                    Map chatMap = new HashMap();
                    chatMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chats/" + user_id + "/" + receiver_user_id, chatMap);
                    chatUserMap.put("Chats/" + receiver_user_id + "/" + user_id, chatMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendMessageClick(View view) {
        final String message = messageText.getText().toString();

        //ToDo Add Date Headline
        if (!TextUtils.isEmpty(message)) {

            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("HH:mm");
            String currentTime = formatter.format(new Date());

            messageText.setText("");

            String currentUserRef = user_id + "/" + receiver_user_id;
            String receiverUserRef = receiver_user_id + "/" + user_id;

            DatabaseReference user_message_push = ChatDatabase.child(user_id).child(receiver_user_id).push();
            String pushId = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("time", currentTime);
            messageMap.put("from", user_id);

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef + "/" + pushId, messageMap);
            messageUserMap.put(receiverUserRef + "/" + pushId, messageMap);

            ChatDatabase.updateChildren(messageUserMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    messagesRecyclerView.smoothScrollToPosition(messagesList.size());

                    Map userMessageMap = new HashMap();
                    userMessageMap.put("message", message);
                    userMessageMap.put("time", ServerValue.TIMESTAMP);
                    userMessageMap.put("date", date);
                    UserDatabase.child(user_id).child("chats").child(receiver_user_id).updateChildren(userMessageMap);
                    UserDatabase.child(receiver_user_id).child("chats").child(user_id).updateChildren(userMessageMap);
                }
            });
        }
    }

    private void loadMessages() {
        ChatDatabase.child(user_id).child(receiver_user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                MessagesModel messages = dataSnapshot.getValue(MessagesModel.class);

                messagesList.add(messages);
                messagesAdapter.notifyDataSetChanged();
                messagesRecyclerView.scrollToPosition(messagesList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SingleChat.this, ChatActivity.class);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
    }
}
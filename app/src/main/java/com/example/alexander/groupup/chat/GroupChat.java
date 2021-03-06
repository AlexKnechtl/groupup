package com.example.alexander.groupup.chat;

import android.content.Context;
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
import android.widget.Toast;

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.adapters.GroupChatAdapter;
import com.example.alexander.groupup.models.MessagesModel;
import com.example.alexander.groupup.singletons.LanguageStringsManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChat extends BaseActivity {

    //XML
    private CircleImageView groupImage;
    private TextView groupActivity;
    private EditText messageText;
    private RecyclerView groupChatRecyclerView;

    //Variables
    private String user_id, group_id, activity, location, group_category, name;
    private final List<MessagesModel> messagesList = new ArrayList<>();
    private GroupChatAdapter groupChatAdapter;
    private Context context = GroupChat.this;

    //FireBase
    private DatabaseReference GroupChatDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        group_id = bundle.getString("group_id");
        user_id = bundle.getString("user_id");
        group_category = bundle.getString("category");

        switch (group_category) {
            case "sport":
                setTheme(R.style.SportTheme);
                break;
            case "nightlife":
                setTheme(R.style.NightlifeTheme);
                break;
            case "business":
                setTheme(R.style.BusinessTheme);
                break;
            case "leisure":
                setTheme(R.style.LeisureTheme);
                break;
        }

        setContentView(R.layout.chat_group);

        //Find IDs
        groupImage = findViewById(R.id.group_chat_image);
        groupActivity = findViewById(R.id.group_chat_activity);
        messageText = findViewById(R.id.group_chat_et);
        groupChatRecyclerView = findViewById(R.id.group_chat_list);

        //Set Adapter
        groupChatRecyclerView.setHasFixedSize(true);
        groupChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        groupChatAdapter = new GroupChatAdapter(messagesList, context, group_id);

        groupChatRecyclerView.setAdapter(groupChatAdapter);

        //Initialize FireBase
        GroupChatDatabase = FirebaseDatabase.getInstance().getReference().child("GroupChat").child(group_id);

        DatabaseReference UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        DatabaseReference GroupDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id);
        GroupDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                activity = dataSnapshot.child("activity").getValue().toString();
                group_category = dataSnapshot.child("category").getValue().toString();
                location = dataSnapshot.child("location").getValue().toString();

                groupActivity.setText(LanguageStringsManager.getInstance().getLanguageStringByStringId(activity).getLocalLanguageString()
                        + " @" + location);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setGroupImage();
        loadGroupChats();

        UserDatabase.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendGroupMessage(View view) {
        String message = messageText.getText().toString();

        if (!TextUtils.isEmpty(message)) {
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("HH:mm");
            String currentTime = formatter.format(new Date());

            messageText.setText("");

            DatabaseReference user_message_push = GroupChatDatabase.push();
            String pushId = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("time", currentTime);
            messageMap.put("from", user_id);
            messageMap.put("name", name);
            messageMap.put("id", pushId);
            messageMap.put("type", "message");

            GroupChatDatabase.child(pushId).updateChildren(messageMap);
            groupChatRecyclerView.smoothScrollToPosition(messagesList.size());
        }
    }

    private void loadGroupChats() {
        GroupChatDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessagesModel message = dataSnapshot.getValue(MessagesModel.class);

                messagesList.add(message);
                groupChatAdapter.notifyDataSetChanged();
                groupChatRecyclerView.scrollToPosition(messagesList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for (MessagesModel testModel : messagesList) {
                    if (key.equals(testModel.id)) {
                        messagesList.remove(testModel);
                        groupChatAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setGroupImage() {
        switch (group_category) {
            case "sport":
                groupImage.setImageResource(R.drawable.group_chat_sport);
                break;
            case "nightlife":
                groupImage.setImageResource(R.drawable.group_chat_icon_nightlife);
                break;
            case "business":
                groupImage.setImageResource(R.drawable.group_chat_icon_business);
                break;
            case "leisure":
                groupImage.setImageResource(R.drawable.group_chat_icon_leisure);
                break;
        }
    }
}
package com.example.alexander.groupup.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.models.MessagesModel;
import com.example.alexander.groupup.profile.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.GroupChatViewHolder> {

    private List<MessagesModel> messagesList;
    private Context context;
    private String groupId;

    public GroupChatAdapter(List<MessagesModel> messagesList, Context context, String groupId) {
        this.messagesList = messagesList;
        this.context = context;
        this.groupId = groupId;
    }

    @Override
    public GroupChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_layout_group_message, parent, false);
        return new GroupChatViewHolder(v);
    }

    public class GroupChatViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;
        private LinearLayout messageLayout;
        private TextView timeText;
        private TextView name;
        private CardView background;
        private ImageButton acceptRequest;

        public GroupChatViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.group_chat_text);
            messageLayout = view.findViewById(R.id.group_chat_layout);
            acceptRequest = view.findViewById(R.id.request_accept_btn);
            timeText = view.findViewById(R.id.group_chat_timestamp);
            name = view.findViewById(R.id.group_chat_author);
            background = view.findViewById(R.id.group_chat_background);
        }
    }

    @Override
    public void onBindViewHolder(final GroupChatViewHolder viewHolder, final int position) {

        final MessagesModel c = messagesList.get(position);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String from_user = c.getFrom();
        String type = c.getType();

        viewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("user_id", c.getFrom());
            }
        });

        if (type.equals("message")) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(28, 14, 28, 14);
            viewHolder.background.setLayoutParams(params);
            viewHolder.messageLayout.setGravity(Gravity.NO_GRAVITY);
            if (from_user.equals(mAuth.getCurrentUser().getUid())) {
                viewHolder.messageLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                viewHolder.background.setCardBackgroundColor(viewHolder.background.getResources().getColor(R.color.colorChatUser));
                viewHolder.acceptRequest.setVisibility(View.GONE);
                viewHolder.name.setVisibility(View.GONE);
                viewHolder.timeText.setText(c.getTime());
                viewHolder.timeText.setVisibility(View.VISIBLE);

            } else {
                viewHolder.messageLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                viewHolder.background.setCardBackgroundColor(viewHolder.messageLayout.getResources().getColor(R.color.colorChat));
                viewHolder.acceptRequest.setVisibility(View.GONE);
                viewHolder.name.setVisibility(View.VISIBLE);
                viewHolder.name.setText(c.getName());
                viewHolder.timeText.setText(c.getTime());
                viewHolder.timeText.setVisibility(View.VISIBLE);
            }

        } else if (type.equals("request")) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(28, 14, 28, 14);
            viewHolder.background.setLayoutParams(params);
            viewHolder.messageLayout.setGravity(Gravity.NO_GRAVITY);
            viewHolder.messageLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            viewHolder.background.setCardBackgroundColor(viewHolder.messageLayout.getResources().getColor(R.color.colorChat));
            viewHolder.timeText.setVisibility(View.GONE);
            viewHolder.name.setVisibility(View.VISIBLE);
            viewHolder.name.setText(c.getName() + " - Anfrage");
            viewHolder.acceptRequest.setVisibility(View.VISIBLE);

            viewHolder.acceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, viewHolder.acceptRequest);
                    popupMenu.inflate(R.menu.menu_group_request);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                            switch (item.getItemId()) {
                                case R.id.accept_request:

                                    Toast.makeText(context, c.getFrom(), Toast.LENGTH_SHORT).show();

                                    reference.child("Users").child(c.getFrom()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("my_group").exists()) {
                                                reference.child("Users").child(c.getFrom()).child("my_group").setValue(groupId);
                                                reference.child("Groups").child(groupId).child("members").child(c.getFrom())
                                                        .child("rank").setValue("member");
                                                reference.child("GroupChat").child(groupId).child(c.getId()).removeValue();
                                                reference.child("Groups").child(groupId).child("member_count").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        reference.child("Groups").child(groupId).child("member_count").setValue(Integer.parseInt(dataSnapshot.getValue().toString()) + 1);

                                                        DatabaseReference GroupChatDatabase = FirebaseDatabase.getInstance().getReference()
                                                                .child("GroupChat").child(groupId);

                                                        DatabaseReference user_message_push = GroupChatDatabase.push();
                                                        String pushId = user_message_push.getKey();

                                                        Map messageMap = new HashMap();
                                                        messageMap.put("message", c.getName() + " joined the Group.");
                                                        messageMap.put("from", c.getFrom());
                                                        messageMap.put("type", "information");

                                                        GroupChatDatabase.child(pushId).updateChildren(messageMap);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                            } else if (!dataSnapshot.child("my_group").exists()) {
                                                Toast.makeText(context, "User ist bereits einer anderen Gruppe beigetreten.", Toast.LENGTH_SHORT).show();
                                                reference.child("GroupChat").child(groupId).child(c.getId()).removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    break;
                                case R.id.decline_request:
                                    reference.child("GroupChat").child(groupId).child(c.getId()).removeValue();
                                    break;
                                case R.id.show_user:
                                    Intent intent = new Intent(context, UserProfileActivity.class);
                                    intent.putExtra("user_id", c.getFrom());
                                    context.startActivity(intent);
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        } else if (type.equals("information")) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 24, 0, 24);
            viewHolder.background.setLayoutParams(params);
            viewHolder.messageLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            viewHolder.background.setCardBackgroundColor(viewHolder.messageLayout.getResources().getColor(R.color.softGreyBackground));
            viewHolder.acceptRequest.setVisibility(View.GONE);
            viewHolder.name.setVisibility(View.GONE);
            viewHolder.timeText.setVisibility(View.GONE);
        }
        viewHolder.messageText.setText(c.getMessage());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
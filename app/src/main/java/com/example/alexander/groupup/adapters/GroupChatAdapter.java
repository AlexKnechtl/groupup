package com.example.alexander.groupup.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.models.MessagesModel;
import com.example.alexander.groupup.profile.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.GroupChatViewHolder> {

    private List<MessagesModel> messagesList;
    private Context context;

    public GroupChatAdapter(List<MessagesModel> messagesList, Context context) {
        this.messagesList = messagesList;
        this.context = context;
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
        private FloatingActionButton acceptRequest;

        public GroupChatViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.group_chat_text);
            messageLayout = view.findViewById(R.id.group_chat_layout);
            timeText = view.findViewById(R.id.group_chat_timestamp);
            name = view.findViewById(R.id.group_chat_author);
            background = view.findViewById(R.id.group_chat_background);
        }
    }

    @Override
    public void onBindViewHolder(GroupChatViewHolder viewHolder, final int position) {

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
            if (from_user.equals(mAuth.getCurrentUser().getUid())) {
                viewHolder.messageLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                viewHolder.background.setCardBackgroundColor(viewHolder.messageLayout.getResources().getColor(R.color.colorChatUser));
                viewHolder.name.setVisibility(View.GONE);
            } else {
                viewHolder.messageLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                viewHolder.background.setCardBackgroundColor(viewHolder.messageLayout.getResources().getColor(R.color.colorChat));
                viewHolder.name.setVisibility(View.VISIBLE);
                viewHolder.name.setText(c.getName());
            }
        } else if (type.equals("request")) {

        }

        viewHolder.timeText.setText(c.getTime());
        viewHolder.messageText.setText(c.getMessage());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
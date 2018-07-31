package com.example.alexander.groupup.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.models.MessagesModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.GroupChatViewHolder> {

    private List<MessagesModel> messagesList;
    private FirebaseAuth mAuth;

    public GroupChatAdapter(List<MessagesModel> messagesList) {
        this.messagesList = messagesList;
    }

    @Override
    public GroupChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_layout_message, parent, false);

        return new GroupChatViewHolder(v);
    }

    public class GroupChatViewHolder extends RecyclerView.ViewHolder {

        private TextView messageText;
        private LinearLayout messageLayout;
        private TextView timeText;
        private TextView name;
        private CardView background;

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

        //ToDo Add Date Headline (Performance sparend)
        MessagesModel c = messagesList.get(position);
        String from_user = c.getFrom();

        mAuth = FirebaseAuth.getInstance();

        if (from_user.equals(mAuth.getCurrentUser().getUid())) {
            viewHolder.messageLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            viewHolder.background.setCardBackgroundColor(viewHolder.messageLayout.getResources().getColor(R.color.colorChatUser));
        } else {
            viewHolder.messageLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            viewHolder.background.setCardBackgroundColor(viewHolder.messageLayout.getResources().getColor(R.color.colorChat));
        }

        viewHolder.name.setText(c.getName());
        viewHolder.timeText.setText(c.getTime());
        viewHolder.messageText.setText(c.getMessage());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
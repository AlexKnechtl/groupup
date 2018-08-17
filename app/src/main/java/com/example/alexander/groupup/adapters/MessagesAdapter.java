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

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<MessagesModel> messagesList;

    public MessagesAdapter(List<MessagesModel> messagesList) {
        this.messagesList = messagesList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_layout_message, parent, false);

        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;
        private TextView dateText;
        private TextView timeText;
        private LinearLayout messageLayout;
        private CardView background;

        public MessageViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.message_text);
            messageLayout = view.findViewById(R.id.message_layout);
            timeText = view.findViewById(R.id.message_timestamp);
            background = view.findViewById(R.id.message_background);
            dateText = view.findViewById(R.id.message_date);
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder viewHolder, final int position) {
        MessagesModel c = messagesList.get(position);
        String from_user = c.from;

        if (position > 0) {
            MessagesModel d = messagesList.get(position - 1);
            if (c.date.equalsIgnoreCase(d.date)) {
                viewHolder.dateText.setVisibility(View.GONE);
            } else {
                viewHolder.dateText.setText(c.date);
                viewHolder.dateText.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.dateText.setText(c.date);
            viewHolder.dateText.setVisibility(View.VISIBLE);
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (from_user.equals(mAuth.getCurrentUser().getUid())) {
            viewHolder.messageLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            viewHolder.messageText.setTextColor(viewHolder.messageText.getResources().getColor(R.color.colorAccent));
            viewHolder.timeText.setTextColor(viewHolder.timeText.getResources().getColor(R.color.colorAccent));
            viewHolder.background.setCardBackgroundColor(viewHolder.messageLayout.getResources().getColor(R.color.colorChatUser));
        } else {
            viewHolder.messageLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            viewHolder.messageText.setTextColor(viewHolder.messageText.getResources().getColor(R.color.black));
            viewHolder.timeText.setTextColor(viewHolder.timeText.getResources().getColor(R.color.black));
            viewHolder.background.setCardBackgroundColor(viewHolder.messageLayout.getResources().getColor(R.color.softGreyBackground));
        }

        viewHolder.timeText.setText(c.time);
        viewHolder.messageText.setText(c.message);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
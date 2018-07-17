package com.example.alexander.groupup.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.models.MessagesModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<MessagesModel> messagesList;
    private FirebaseAuth mAuth;

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
        private TextView timeText;
        private CardView messageLayout;

        public MessageViewHolder(View view) {
            super(view);

            messageText = view.findViewById(R.id.message_text);
            messageLayout = view.findViewById(R.id.message_layout);
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder viewHolder, final int position) {

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();

        MessagesModel c = messagesList.get(position);
        String from_user = c.getFrom();

        if (from_user.equals(user_id)) {
            viewHolder.messageLayout.setCardBackgroundColor(viewHolder.messageLayout.getResources().getColor(R.color.colorPrimaryDark));
        }

        viewHolder.messageText.setText(c.getMessage());
        //viewHolder.timeText.setText("" + c.getTime());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
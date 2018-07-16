package com.example.alexander.groupup.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexander.groupup.R;
import com.example.alexander.groupup.models.MessagesModel;

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
        public TextView messageText;

        public MessageViewHolder(View view) {
            super(view);

            messageText = view.findViewById(R.id.message_text);
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder viewHolder, final int position) {

        MessagesModel c = messagesList.get(position);
        viewHolder.messageText.setText(c.getMessage());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
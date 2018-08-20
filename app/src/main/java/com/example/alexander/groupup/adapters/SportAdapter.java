package com.example.alexander.groupup.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.alexander.groupup.interviews.InterviewPublic;
import com.example.alexander.groupup.models.GroupModel;
import com.example.alexander.groupup.models.GroupType;
import com.example.alexander.groupup.models.LanguageStringsModel;
import com.example.alexander.groupup.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SportAdapter extends RecyclerView.Adapter<SportAdapter.ViewHolder> {

    private ArrayList<LanguageStringsModel> sportItems;
    private Context mContext;

    public SportAdapter(Context context, ArrayList<LanguageStringsModel> sportItems) {
        mContext = context;
        this.sportItems = sportItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_layout_sport_activity, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.sportActivity.setText(sportItems.get(position).getLocalLanguageString());
        Glide.with(mContext).load(sportItems.get(position).Image).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                holder.sportImage.setImageDrawable(resource);
            }
        });

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, InterviewPublic.class);
                GroupModel group = new GroupModel();
                group.activity = sportItems.get(position).getId();
                group.category = GroupType.sport;
                intent.putExtra("group", group);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sportItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parentLayout;
        TextView sportActivity;
        CircleImageView sportImage;

        ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parent_layout_sport);
            sportActivity = itemView.findViewById(R.id.sport_activity_text);
            sportImage = itemView.findViewById(R.id.sport_activity_image);
        }
    }

    public void filterList(ArrayList<LanguageStringsModel> filteredNames) {
        this.sportItems = filteredNames;
        notifyDataSetChanged();
    }
}
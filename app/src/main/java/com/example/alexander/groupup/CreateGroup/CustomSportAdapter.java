package com.example.alexander.groupup.CreateGroup;

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
import com.example.alexander.groupup.Models.LanguageStringsModel;
import com.example.alexander.groupup.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomSportAdapter extends RecyclerView.Adapter<CustomSportAdapter.ViewHolder> {

    private ArrayList<LanguageStringsModel> sportItems;
    private String category;
    private Context mContext;

    public CustomSportAdapter(Context context, ArrayList<LanguageStringsModel> sportItems, String category) {
        mContext = context;
        this.category = category;
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
                Intent intent = new Intent(mContext, InterviewMembers.class);
                intent.putExtra("group", category);
                intent.putExtra("activity", sportItems.get(position).getId());
                intent.putExtra("group_image", new Integer(position).toString());
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

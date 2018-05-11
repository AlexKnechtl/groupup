package com.example.alexander.groupup.CreateGroup;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alexander.groupup.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomSportAdapter extends RecyclerView.Adapter<CustomSportAdapter.ViewHolder> {

    private ArrayList<String> sportItems;
    private ArrayList<Integer> sportImages;
    private String category;
    private Context mContext;

    public CustomSportAdapter(Context context, ArrayList<String> sportItems, ArrayList<Integer> sportImages, String category) {
        mContext = context;
        this.category = category;
        this.sportItems = sportItems;
        this.sportImages = sportImages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_layout_sport_activity, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.sportActivity.setText(sportItems.get(position));
        holder.sportImage.setImageResource(sportImages.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, InterviewMembers.class);
                intent.putExtra("group", category);
                intent.putExtra("activity", sportItems.get(position));
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

    public void filterList(ArrayList<String> filteredNames) {
        this.sportItems = filteredNames;
        notifyDataSetChanged();
    }
}
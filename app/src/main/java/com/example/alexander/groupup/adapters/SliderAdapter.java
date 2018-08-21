package com.example.alexander.groupup.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alexander.groupup.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slideImages = {
            R.drawable.sport_image,
            R.drawable.business_image,
            //R.drawable.nightlife_houseparty,
            //R.drawable.leisure_image
    };

    public String[] slide_headlines = {
            "GroupUp! VIP",
            "",
            //context.getResources().getString(R.string.houseparty),
            //context.getResources().getString(R.string.vip_style)
    };

    public String[] slide_descriptions = {
            "test",
            "test2"
            //context.getResources().getString(R.string.vip_headline) + "/n" + context.getResources().getString(R.string.vip_headline_2),
            //context.getResources().getString(R.string.vip_ranking_booster_info),
            //context.getResources().getString(R.string.vip_houseparty_info) + "/n" + context.getResources().getString(R.string.vip_houseparty_info_2),
            //context.getResources().getString(R.string.vip_style_info)
    };

    @Override
    public int getCount() {
        return slide_headlines.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.vip_slide_layout, container, false);

        ImageView vipSlideImage = view.findViewById(R.id.image_vip_slider);
        TextView vipSlideHeadline = view.findViewById(R.id.headline_vip_slider);
        TextView vipSlideDescription = view.findViewById(R.id.description_vip_slider);

        vipSlideImage.setImageResource(slideImages[position]);
        vipSlideHeadline.setText(slide_headlines[position]);
        vipSlideDescription.setText(slide_descriptions[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
package com.example.alexander.groupup.settings;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.alexander.groupup.BaseActivity;
import com.example.alexander.groupup.R;
import com.example.alexander.groupup.adapters.SliderAdapter;

public class GetVIP extends BaseActivity {

    //XML
    private ViewPager viewpager;
    private LinearLayout dotLayout;
    private Button getVipBtn;

    private TextView[] dots;

    //Variables
    private SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_get_vip);

        //Find IDs
        viewpager = findViewById(R.id.vip_view_pager);
        dotLayout = findViewById(R.id.vip_dot_layout);
        getVipBtn = findViewById(R.id.get_vip_button);

        //Set Adapter
        sliderAdapter = new SliderAdapter(GetVIP.this);
        viewpager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        viewpager.addOnPageChangeListener(viewListener);

        getVipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void addDotsIndicator(int position) {
        dots = new TextView[2];
        dotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {

            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.darkGray));

            dotLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorAccent));
        }

    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);

            if (position == dots.length - 1)
                getVipBtn.setVisibility(View.VISIBLE);
            else
                getVipBtn.setVisibility(View.GONE);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
package com.example.medicalappadmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.medicalappadmin.R;
import com.example.medicalappadmin.canvas.DetailedPageView;
import com.example.medicalappadmin.canvas.NotepadView;
import com.example.medicalappadmin.rest.response.ConfigurePageRP;
import com.example.medicalappadmin.rest.response.ViewCaseRP;

public class PageAdapter extends PagerAdapter {
    private Context context;

    private final ViewCaseRP viewCaseRP;
    int currentPageNo;
    DetailedPageView notepadView;
    TextView tvPageNumber;
    private final ConfigurePageRP configurePageRP;


    public PageAdapter(Context context, ConfigurePageRP configurePageRP, ViewCaseRP response, int currentPageNo) {
        this.context = context;
        this.viewCaseRP = response;
        this.currentPageNo = currentPageNo;
        this.configurePageRP = configurePageRP;
    }

    @Override
    public int getCount() {
        return viewCaseRP.getPages().size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.detailed_page_item, container, false);
        notepadView = view.findViewById(R.id.detailedPage);
        tvPageNumber = view.findViewById(R.id.tvPageNumber);

        tvPageNumber.setText("Page No: " + viewCaseRP.getPages().get(position).getPageNumber());
        notepadView.clearDrawing();
        if(configurePageRP != null){
            view.post(()->{
                notepadView.setBackgroundImageUrl(configurePageRP.getPageDetails().getPageBackground(),configurePageRP.getPageDetails().getPageWidth(),configurePageRP.getPageDetails().getPageHeight());
            });
        }
        notepadView.addCoordinates(viewCaseRP.getPages().get(position).getPoints());

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}

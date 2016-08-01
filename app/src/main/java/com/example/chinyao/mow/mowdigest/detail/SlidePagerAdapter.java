package com.example.chinyao.mow.mowdigest.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Jiaqi Ning on 4/5/2015.
 */
public class SlidePagerAdapter extends FragmentStatePagerAdapter {
    private int TOTAL_PAGE_COUNT;
    public String first_image;
    public String first_title;
    public String first_section;
    public String first_abstract;

    public SlidePagerAdapter(FragmentManager fm, int count) {
        super(fm);
        TOTAL_PAGE_COUNT = count;
    }


    @Override
    public Fragment getItem(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt(BundleKey.PAGE_INDEX, i);
        YahooSlidePageFragment fragment = new YahooSlidePageFragment();
        fragment.first_image = first_image;
        fragment.first_title = first_title;
        fragment.first_section = first_section;
        fragment.first_abstract = first_abstract;
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return TOTAL_PAGE_COUNT;
    }
}

package com.example.chinyao.mow.mowdigest.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.chinyao.mow.mowtweebook.model.MowtweebookTweet;

import java.util.List;

/**
 * Created by Jiaqi Ning on 4/5/2015.
 */
public class SlidePagerAdapter extends FragmentStatePagerAdapter {
    private int TOTAL_PAGE_COUNT;

    // Bundle
    public String first_image;
    public String first_title;
    public String first_section;
    public String first_abstract;

    // Parcels
    public List<MowtweebookTweet> tweets;

    public SlidePagerAdapter(FragmentManager fm, int count) {
        super(fm);
        TOTAL_PAGE_COUNT = count;
        tweets = null;
    }


    @Override
    public Fragment getItem(int i) {
        YahooSlidePageFragment fragment = new YahooSlidePageFragment();

        if (tweets == null) {
            // Bundle
            fragment.first_image = first_image;
            fragment.first_title = first_title;
            fragment.first_section = first_section;
            fragment.first_abstract = first_abstract;
        }
        else {
            // Parcels
            fragment.first_image = tweets.get(i).getMowtweebookImageUrl();
            fragment.first_title = tweets.get(i).getText();
            fragment.first_section = tweets.get(i).getUser().getScreen_name();
            fragment.first_abstract = tweets.get(i).getText();
        }

        // Arguments with Bundle
        Bundle bundle = new Bundle();
        bundle.putInt(BundleKey.PAGE_INDEX, i);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return TOTAL_PAGE_COUNT;
    }
}

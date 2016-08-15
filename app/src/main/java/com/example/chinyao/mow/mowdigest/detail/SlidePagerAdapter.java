package com.example.chinyao.mow.mowdigest.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.chinyao.mow.mowtweebook.model.MowtweebookTweet;
import com.example.chinyao.mow.mowtweebook.utility.MowtweebookRestApplication;
import com.example.chinyao.mow.mowtweebook.utility.MowtweebookRestClient;

import java.util.List;

/**
 * Created by Jiaqi Ning on 4/5/2015.
 */
public class SlidePagerAdapter extends FragmentStatePagerAdapter {
    private int TOTAL_PAGE_COUNT;

    // Bundle
    public String first_image;
    public String first_section;
    public String first_title;
    public String first_abstract;

    // Parcels
    public List<MowtweebookTweet> tweets;

    private ViewPager mPager;
    private MowtweebookRestClient client;

    public SlidePagerAdapter(FragmentManager fm, int count, ViewPager mPager) {
        super(fm);
        TOTAL_PAGE_COUNT = count;
        tweets = null;
        this.mPager = mPager;
        client = MowtweebookRestApplication.getRestClient();
    }


    @Override
    public Fragment getItem(int i) {
        YahooSlidePageFragment fragment = new YahooSlidePageFragment();

        if (tweets == null) {
            // Bundle
            fragment.first_image = first_image;
            fragment.first_section = first_section;
            fragment.first_title = first_title;
            fragment.first_abstract = first_abstract;
        }
        else {
            // Parcels
            fragment.first_image = tweets.get(i).getMowtweebookImageUrl();
            fragment.first_section = tweets.get(i).getUser().getScreen_name();
            fragment.first_title = tweets.get(i).getText();
            fragment.first_abstract = "";
        }

        fragment.tweets = tweets;
        fragment.mPager = mPager;
        fragment.client = client;

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

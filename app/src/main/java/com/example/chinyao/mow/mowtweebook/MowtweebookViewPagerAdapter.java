package com.example.chinyao.mow.mowtweebook;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.chinyao.mow.R;

import java.util.ArrayList;

/**
 * Created by chinyao on 8/14/2016.
 */
public class MowtweebookViewPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 2;

    private Context context;
    private ViewPager viewPager;
    private MowtweebookRestClient client;

    private ArrayList<Fragment> registeredFragments;
    // http://guides.codepath.com/android/ViewPager-with-FragmentPagerAdapter
    // https://gist.github.com/nesquena/c715c9b22fb873b1d259

    public MowtweebookViewPagerAdapter(FragmentManager fm,
                                   Context context,
                                   ViewPager viewPage,
                                   MowtweebookRestClient client) {
        super(fm);
        this.context = context;
        this.viewPager = viewPage;
        this.client = client;
        registeredFragments = new ArrayList<>();
        registeredFragments.add(MowtweebookFragment.newInstance(1, viewPager, client));
        registeredFragments.add(MowtweebookFragment.newInstance(2, viewPager, client));
    }

    @Override
    public Fragment getItem(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.home);
            case 1:
                return context.getResources().getString(R.string.profile);
            default:
                return "";
        }
    }

    // Returns the fragment for the position (if instantiated)
    public Fragment getRegisteredFragment(int position) {
        if (position < 0 || position >= NUM_ITEMS) {
            return null;
        }
        return registeredFragments.get(position);
    }
}

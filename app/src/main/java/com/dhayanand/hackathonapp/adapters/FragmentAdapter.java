package com.dhayanand.hackathonapp.adapters;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dhayanand.hackathonapp.activities.MainActivityFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dhayanand on 1/15/2017.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    @Override
    public Parcelable saveState() {
        return super.saveState();
    }

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(int fragmentType, String title) {

        MainActivityFragment fragment = new MainActivityFragment();
        Bundle args = new Bundle();
        args.putInt(MainActivityFragment.FRAGMENT_KEY, fragmentType);
        fragment.setArguments(args);

        this.addFragment(fragment, title);

    }

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }
}

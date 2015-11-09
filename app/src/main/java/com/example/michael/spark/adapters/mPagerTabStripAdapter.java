package com.example.michael.spark.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.michael.spark.FragmentActivities.CoursesFragment;
import com.example.michael.spark.FragmentActivities.FriendsFragment;
import com.example.michael.spark.FragmentActivities.ProfileFragment;
import com.example.michael.spark.R;

/**
 * Created by Michael on 6/6/2015.
 */
public class mPagerTabStripAdapter extends FragmentPagerAdapter {

    protected Context mContext;

    public mPagerTabStripAdapter(Context context, FragmentManager fm){
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new ProfileFragment();
            case 1:
                return new CoursesFragment();
            case 2:
                return new FriendsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section1);
            case 1:
                return mContext.getString(R.string.title_section2);
            case 2:
                return mContext.getString(R.string.title_section3);
        }
        return null;
    }
}

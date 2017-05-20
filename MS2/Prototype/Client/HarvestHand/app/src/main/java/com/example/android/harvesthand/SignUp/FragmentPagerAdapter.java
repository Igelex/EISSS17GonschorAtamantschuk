package com.example.android.harvesthand.SignUp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.android.harvesthand.R;

/**
 * Created by Pastuh on 19.05.2017.
 */

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private static final int COUNT_PAGES = 3;
    private Context mContext;

    public FragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.signin);
            case 1:
                return mContext.getString(R.string.signup);
            case 2:
                return mContext.getString(R.string.about);
            default:
                return null;
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SignInFragment();
            case 1:
                return new SignUpFragment();
            case 2:
                return new AboutFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return COUNT_PAGES;
    }
}

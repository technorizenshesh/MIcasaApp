package com.tech.micasa.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tech.micasa.ui.ProdUserAboutFragment;
import com.tech.micasa.ui.ProdUserListFragment;
import com.tech.micasa.ui.ProdUserReviewFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new ProdUserListFragment();
            case 1:
                return new ProdUserReviewFragment();

            case 2:
                return new ProdUserAboutFragment();
            default:
                return new ProdUserListFragment();

        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        String title = null;
        switch (position) {

            case 0:
                title = "Listing";
                break;

            case 1:
                title = "Review";
                break;

            case 2:
                title = "About";
                break;

        }
        return title;
    }

    @Override
    public int getCount() {
        return 3;
    }
}


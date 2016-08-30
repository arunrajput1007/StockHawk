package com.sam_chordas.android.stockhawk.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sam_chordas.android.stockhawk.ui.TabbedFragment;

/**
 * Created by arun on 9/8/16.
 */

public class GraphPagerAdapter extends FragmentPagerAdapter {

    private String symbol = null;

    public GraphPagerAdapter(FragmentManager fm, String symbol) {
        super(fm);
        this.symbol = symbol;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new TabbedFragment();
        Bundle args = new Bundle();
        if (position == 0)
            args.putInt("limit", 10);
        else if (position == 1)
            args.putInt("limit", 50);
        else if (position == 2)
            args.putInt("limit", 100);
        args.putString("symbol", symbol);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}

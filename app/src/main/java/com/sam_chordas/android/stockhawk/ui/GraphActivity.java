package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.adapter.GraphPagerAdapter;

public class GraphActivity extends AppCompatActivity {

    private ActionBar actionBar = null;
    private int[] tabArr = {10, 50, 100};
    private ViewPager mViewPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        String symbol = getIntent().getStringExtra("quoteSymbol");
        mViewPager = (ViewPager) findViewById(R.id.pager);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(symbol);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        for (int i = 0; i < 3; i++) {
            actionBar.addTab(actionBar.newTab().setText("Last " + tabArr[i] + " days").setTabListener(tabListener));
        }

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        GraphPagerAdapter graphPagerAdapter = new GraphPagerAdapter(getSupportFragmentManager(), symbol);
        mViewPager.setAdapter(graphPagerAdapter);
    }
}

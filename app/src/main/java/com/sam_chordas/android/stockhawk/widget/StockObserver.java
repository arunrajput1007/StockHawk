package com.sam_chordas.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.database.ContentObserver;
import android.os.Handler;

import com.sam_chordas.android.stockhawk.R;

/**
 * Created by arun on 23/8/16.
 */

public class StockObserver extends ContentObserver {
    private AppWidgetManager appWidgetManager = null;
    private int[] ids;

    public StockObserver(AppWidgetManager appWidgetManager, int[] ids) {
        super(new Handler());
        this.appWidgetManager = appWidgetManager;
        this.ids = ids;
    }

    @Override
    public void onChange(boolean selfChange) {
        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_list);
    }
}

package com.sam_chordas.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by arun on 22/8/16.
 */

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext = null;
    private Cursor cursor = null;
    private int mAppwidgetId;

    public WidgetDataProvider(Context mContext,Intent intent) {
        this.mContext = mContext;
        mAppwidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                null, QuoteColumns.SYMBOL + " is not null) GROUP BY (" + QuoteColumns.SYMBOL, null, null);
        if (cursor != null)
            cursor.moveToFirst();
    }

    @Override
    public void onDestroy() {
        cursor.close();
    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null)
            cursor.close();
        cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                null, QuoteColumns.SYMBOL + " is not null) GROUP BY (" + QuoteColumns.SYMBOL, null, null);
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        cursor.moveToPosition(position);
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);
        remoteView.setTextViewText(R.id.stock_symbol, cursor.getString(1));
        remoteView.setTextViewText(R.id.bid_price, cursor.getString(4));
        remoteView.setTextViewText(R.id.change, cursor.getString(3));
        if (cursor.getInt(cursor.getColumnIndex("is_up")) == 1) {
            remoteView.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            remoteView.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("quoteSymbol",cursor.getString(1));
        remoteView.setOnClickFillInIntent(R.id.widget_list_item,fillInIntent);
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

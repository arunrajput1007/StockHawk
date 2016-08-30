package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.GraphActivity;

/**
 * Implementation of App Widget functionality.
 */
public class StockWidget extends AppWidgetProvider {

    private final String LIST_ITEM_CLICKED = "list item clicked";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(LIST_ITEM_CLICKED)) {
            Intent activityIntent = new Intent(context,GraphActivity.class);
            activityIntent.putExtra("quoteSymbol",intent.getStringExtra("quoteSymbol"));
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_widget);
            views.setRemoteAdapter(R.id.widget_list,new Intent(context, WidgetService.class));

            Intent detailIntent = new Intent(context, StockWidget.class);
            detailIntent.setAction(LIST_ITEM_CLICKED);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, detailIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list,pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, StockWidget.class);
        int[] appWidgetId = appWidgetManager.getAppWidgetIds(componentName);
        StockObserver observer = new StockObserver(appWidgetManager, appWidgetId);
        context.getContentResolver().registerContentObserver(QuoteProvider.Quotes.CONTENT_URI, true, observer);
    }
}


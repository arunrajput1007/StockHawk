package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.widget.WidgetDataProvider;

/**
 * Created by arun on 22/8/16.
 */

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(this,intent);
    }
}

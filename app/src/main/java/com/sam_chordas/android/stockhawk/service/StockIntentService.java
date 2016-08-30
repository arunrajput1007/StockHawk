package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.TaskParams;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

    public StockIntentService() {
        super(StockIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        StockTaskService stockTaskService = new StockTaskService(getApplicationContext());
        Bundle args = new Bundle();
        if (intent.getStringExtra("tag").equals("add") || intent.getStringExtra("tag").equals("historicalData")) {
            args.putString("symbol", intent.getStringExtra("symbol"));
            if (intent.getStringExtra("tag").equals("historicalData")) {
                args.putString("startDate", intent.getStringExtra("startDate"));
                args.putString("endDate", intent.getStringExtra("endDate"));
            }
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
    }
}

package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

    public static boolean showPercent = true;
    private static String LOG_TAG = Utils.class.getSimpleName();
    private Context mContext;

    public Utils(Context mContext) {
        this.mContext = mContext;
    }

    public static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    public static String truncateChange(@NonNull String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format("%.2f", round);
        StringBuilder changeBuffer = new StringBuilder(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        try {
            String change = jsonObject.getString("Change");
            builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
            builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
            builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                    jsonObject.getString("ChangeinPercent"), true));
            builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
            builder.withValue(QuoteColumns.ISCURRENT, 1);
            if (change.charAt(0) == '-') {
                builder.withValue(QuoteColumns.ISUP, 0);
            } else {
                builder.withValue(QuoteColumns.ISUP, 1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    private static boolean isQuoteValid(JSONObject jsonObject) {
        try {
            if (!(jsonObject.getString("Change").equals("null") || jsonObject.getString("symbol").equals("null") || jsonObject.getString("ChangeinPercent").equals("null") ||
                    jsonObject.getString("Bid").equals("null")))
                return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static JSONArray getJsonArrayFromResponse(String jsonResponse) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonResponse);
            if (jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject("query");
                return jsonObject.getJSONObject("results").getJSONArray("quote");
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }
        return null;
    }

    public ArrayList<ContentProviderOperation> quoteJsonToContentVals(String JSON) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject;
        JSONArray resultsArray;

        try {
            jsonObject = new JSONObject(JSON);
            if (jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject("query");
                int count = Integer.parseInt(jsonObject.getString("count"));
                if (count == 1) {
                    jsonObject = jsonObject.getJSONObject("results")
                            .getJSONObject("quote");
                    if (isQuoteValid(jsonObject)) {
                        batchOperations.add(buildBatchOperation(jsonObject));
                    } else {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.stock_not_found,Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

                    if (resultsArray != null && resultsArray.length() != 0) {
                        for (int i = 0; i < resultsArray.length(); i++) {
                            jsonObject = resultsArray.getJSONObject(i);
                            if (isQuoteValid(jsonObject))
                                batchOperations.add(buildBatchOperation(jsonObject));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }
        return batchOperations;
    }

    public LinkedHashMap<String, Integer> extractDataFromJson(String JSON) {
        JSONObject jsonObject;
        JSONArray resultsArray;
        LinkedHashMap<String, Integer> dateClosePair = new LinkedHashMap<>();
        try {
            resultsArray = getJsonArrayFromResponse(JSON);
            for (int i = 0; i < (resultsArray != null ? resultsArray.length() : 0); i++) {
                jsonObject = resultsArray.getJSONObject(i);
                dateClosePair.put(jsonObject.get("Date").toString(), (int) Double.parseDouble(jsonObject.get("Close").toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dateClosePair;
    }
}
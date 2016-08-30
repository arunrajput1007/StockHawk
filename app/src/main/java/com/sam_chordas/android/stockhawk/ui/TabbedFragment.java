package com.sam_chordas.android.stockhawk.ui;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockTaskService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabbedFragment extends Fragment {

    private int nod = 0;
    private String symbol = null;
    private LineChart chart = null;
    private String startDate = null;
    private String endDate = null;

    public TabbedFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey("limit")) {
            if (args.getInt("limit") == 10) {
                nod = 10;
            } else if (args.getInt("limit") == 50) {
                nod = 50;
            } else if (args.getInt("limit") == 100) {
                nod = 100;
            }
            symbol = args.getString("symbol");
            endDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -nod);
            startDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabbed, container, false);
        chart = (LineChart) view.findViewById(R.id.chart);
        new BackgroundTask().execute();
        return view;
    }

    public void setAndPopulateChart(LineChart chart,LinkedHashMap<String,Integer> dateClosePair){
        chart.setPinchZoom(true);
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xvalues = new ArrayList<>();
        int index =0;

        for (String date:dateClosePair.keySet()) {
            xvalues.add(date);
            entries.add(new Entry(dateClosePair.get(date),index));
            index++;
        }

        XAxis xAxis = chart.getXAxis();
//        xAxis.setLabelsToSkip(4);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        YAxis left = chart.getAxisLeft();
        left.setEnabled(true);
        left.setLabelCount(5,true);

        xAxis.setTextColor(Color.WHITE);
        left.setTextColor(Color.WHITE);

        chart.getAxisRight().setEnabled(false);

        chart.getLegend().setTextSize(12f);
        chart.getLegend().setTextColor(Color.WHITE);

        LineDataSet dataSet = new LineDataSet(entries, symbol);
        LineData lineData = new LineData(xvalues, dataSet);
        lineData.setValueTextSize(8f);

        dataSet.setColor(Color.GREEN);
        dataSet.setValueTextColor(Color.WHITE);
        lineData.setValueTextColor(Color.WHITE);
        chart.setDescriptionColor(Color.WHITE);

        lineData.setDrawValues(true);
        dataSet.setDrawCircles(false);

        chart.setDescription("last "+ nod +" days historical data");
        chart.setData(lineData);
        chart.invalidate();
        chart.animateX(1000);
    }

    class BackgroundTask extends AsyncTask<Void, Integer, Void> {
        StockTaskService stockTaskService = null;
        Bundle args = null;

        @Override
        protected void onPreExecute() {
            args = new Bundle();
            args.putString("symbol", symbol);
            args.putString("startDate", startDate);
            args.putString("endDate", endDate);
            stockTaskService = new StockTaskService(getContext());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            stockTaskService.onRunTask(new TaskParams("historicalData", args));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            LinkedHashMap<String, Integer> dateClosePair = stockTaskService.getDateClosePair();
            if (dateClosePair != null) {
                setAndPopulateChart(chart,dateClosePair);
            }
        }
    }
}

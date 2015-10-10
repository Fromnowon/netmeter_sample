package com.netmeter.like.netmeter.Fragmments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ButtonFlat;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.netmeter.like.netmeter.DataBase.DataUsageDB;
import com.netmeter.like.netmeter.R;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by like on 15/6/28.
 */
public class Fragment_Usage extends Fragment {

    private CombinedChart mChart;
    private final int itemcount = 7;
    private RelativeLayout parent;
    private View v;
    private float total, wifi, mobile;
    private DataUsageDB db;
    private Cursor mCursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        v = inflater.inflate(R.layout.fragment2, container, false);
        IntentFilter filter = new IntentFilter("com.netmeter.like.netmeter.TRAFFIC_DATA");
        getActivity().registerReceiver(myReceiver, filter);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mChart = new CombinedChart(getActivity());
        mChart.setDescription("");
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        // draw bars behind lines
        mChart.setDrawOrder(new DrawOrder[]{
                DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.CANDLE, DrawOrder.LINE, DrawOrder.SCATTER
        });
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);

        //底部x轴描述
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        String[] mMonths = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        final CombinedData data = new CombinedData(mMonths);

        data.setData(generateLineData());
        data.setData(generateBarData());
        mChart.setData(data);
        mChart.invalidate();
        parent = (RelativeLayout) v.findViewById(R.id.fragment2);
        WindowManager wm = getActivity().getWindowManager();
        mChart.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, wm.getDefaultDisplay().getHeight() / 2));
        parent.addView(mChart);
        //更新绘图
        TextView tv0 = (TextView) v.findViewById(R.id.tv0);
        TextView tv1 = (TextView) v.findViewById(R.id.tv1);
        TextView tv2 = (TextView) v.findViewById(R.id.tv2);
        tv0.setText("总流量：" + total + " M");
        tv1.setText("无线：" + wifi + " M");
        tv2.setText("数据：" + mobile + " M");
    }


    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            //此时已数据库已更新数据库
            initData();

        }
    };

    public void initData(){
        db = new DataUsageDB(getActivity());
        mCursor = db.select();
        mCursor.moveToLast();
        total = mCursor.getFloat(6);
        wifi = mCursor.getFloat(7);
        mobile = mCursor.getFloat(8);
        mCursor.close();
    }


    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 1; index < itemcount; index++)
            entries.add(new Entry(0 , index));
        entries.add(new Entry(mobile, 0));

        LineDataSet set = new LineDataSet(entries, "数据流量");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleSize(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setDrawCubic(true);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {

        BarData d = new BarData();

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int index = 1; index < itemcount; index++)
            entries.add(new BarEntry(0, index));
        entries.add(new BarEntry(total, 0));

        BarDataSet set = new BarDataSet(entries, "总流量");
        set.setColor(Color.rgb(60, 220, 78));
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(10f);
        d.addDataSet(set);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return d;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myReceiver);
    }
}

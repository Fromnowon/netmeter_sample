package com.netmeter.like.netmeter.Fragmments;

import android.app.Fragment;
import android.graphics.Color;
import android.net.TrafficStats;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.netmeter.like.netmeter.R;

import java.util.ArrayList;

/**
 * Created by like on 15/6/28.
 */
public class Fragment_Usage extends Fragment {

    private CombinedChart mChart;
    private final int itemcount = 7;
    private RelativeLayout parent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment2, container, false);
        //mChart = (CombinedChart) getActivity().findViewById(R.id.usage_chart);
        mChart = new CombinedChart(getActivity());
        mChart.setDescription("");
        //mChart.setBackgroundColor(Color.WHITE);
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
        CombinedData data = new CombinedData(mMonths);

        data.setData(generateLineData());
        data.setData(generateBarData());
        mChart.setData(data);
        mChart.invalidate();
        parent = (RelativeLayout) v.findViewById(R.id.fragment2);
        WindowManager wm = getActivity().getWindowManager();
        mChart.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, wm.getDefaultDisplay().getHeight() / 2));
        parent.addView(mChart);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView tv0 = (TextView) getView().findViewById(R.id.tv0);
        TextView tv1 = (TextView) getView().findViewById(R.id.tv1);
        tv0.setText("总流量：" + (TrafficStats.getTotalRxBytes() / 1000) / 1000.0 + "M");
        tv1.setText("数据流量：" + (TrafficStats.getMobileRxBytes() / 1000) / 1000.0 + "M");

    }

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < itemcount; index++)
            entries.add(new Entry(getRandom(15, 10), index));

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

        for (int index = 0; index < itemcount; index++)
            entries.add(new BarEntry(getRandom(15, 30), index));

        BarDataSet set = new BarDataSet(entries, "总流量");
        set.setColor(Color.rgb(60, 220, 78));
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(10f);
        d.addDataSet(set);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return d;
    }

    private float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

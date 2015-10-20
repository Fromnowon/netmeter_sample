package com.netmeter.like.netmeter.Fragmments;

import android.app.Fragment;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netmeter.like.netmeter.R;
import com.txusballesteros.SnakeView;

import java.text.DecimalFormat;

/**
 * Created by like on 15/6/28.
 */
public class Fragment_Index extends Fragment {

    private SnakeView snakeView, snakeView_up;
    private TextView speedText, speedText_up;
    private boolean Run = true;
    private double DownloadSpeed, UploadSpeed;
    private Thread MyThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment0, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        snakeView = (SnakeView) getActivity().findViewById(R.id.index_snake);
        snakeView_up = (SnakeView) getActivity().findViewById(R.id.index_snake_up);
        speedText = (TextView) getActivity().findViewById(R.id.speed_text);
        speedText_up = (TextView) getActivity().findViewById(R.id.speed_text_up);

    }

    @Override
    public void onStart() {
        super.onStart();
        C_DownloadSpeed();
    }

    @Override
    public void onResume() {
        super.onResume();
        Run = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        Run = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Run = false;
        MyThread.interrupt();
        MyThread = null;
    }

    //计算下载速度
    public void C_DownloadSpeed() {
        final Handler DataHandler = new Handler() {
            long t1, t2 = TrafficStats.getTotalRxBytes(), t3, t4 = TrafficStats.getTotalTxBytes();

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0: {
                        t1 = TrafficStats.getTotalRxBytes();
                        t3 = TrafficStats.getTotalTxBytes();
                        DecimalFormat df = new DecimalFormat("#.0");
                        DownloadSpeed = Double.valueOf(df.format(change(t1 - t2))).doubleValue();
                        UploadSpeed = Double.valueOf(df.format(change(t3 - t4))).doubleValue();
                        Log.v("tag", DownloadSpeed + "/" + UploadSpeed);
                        if (DownloadSpeed > 4096) snakeView.addValue(4096);
                        else snakeView.addValue((float) DownloadSpeed);
                        if (UploadSpeed > 4096) snakeView_up.addValue(4096);
                        else snakeView_up.addValue((float) UploadSpeed);
                        speedText.setText("D: " + DownloadSpeed + "Kb/s");
                        speedText_up.setText("U: " + UploadSpeed + "Kb/s");
                        t2 = t1;
                        t4 = t3;
                    }
                }
            }

            private double change(long t) {
                if (t < 1024) {
                    return 0;
                } else {
                    return ((double) t / 1024);
                }
            }
        };
        MyThread = new Thread() {
            @Override
            public void run() {
                while (Run) {
                    Message m = new Message();
                    m.what = 0;
                    DataHandler.sendMessage(m);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        MyThread.start();
    }
}

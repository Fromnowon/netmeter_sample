package com.netmeter.like.netmeter_sample;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/*后台网速监控服务*/

public class NetMeterService extends Service {
    private static final String TMP_SAVED = "settings_save";
    private boolean flag = true;//用于判断是否显示悬浮窗
    private View view;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    static int SleepTime;
    static boolean Run = true;
    private TextView textView, textView0;
    private ImageView imageView, imageView0;
    private String textColorTmp;
    private Thread MyThread;
    private float textSize;

    @Override
    public IBinder onBind(Intent intent) {
        //服务在成功绑定的时候会调用onBind方法，返回一个IBinder对象
        return new MyBinder();
    }

    public class MyBinder extends Binder {
    }


    @Override
    public void onStart(Intent intent, int startId) {
        Run = true;
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //读取保存的设置
        readSetting();
        //textView.setTextColor(Color.parseColor("#FFFFFF"));
        view = LayoutInflater.from(this).inflate(R.layout.window, null);
        imageView = (ImageView) view.findViewById(R.id.net_arrow);
        imageView0 = (ImageView) view.findViewById(R.id.net_arrow0);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_expand_down));
        imageView0.setImageDrawable(getResources().getDrawable(R.drawable.ic_expand_up));
        //读取字体颜色
        textView = (TextView) view.findViewById(R.id.traffic);
        textView.setTextColor(Color.parseColor(textColorTmp));
        textView.setTextSize(textSize);
        textView0 = (TextView) view.findViewById(R.id.traffic0);
        textView0.setTextColor(Color.parseColor(textColorTmp));
        textView0.setTextSize(textSize);

        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.RGBA_8888);
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.x = layoutParams.y = 0;
        windowManager.addView(view, layoutParams);
        onMove();
        final Handler tempHandler = new Handler() {
            long t,_t,t1 =0,_t1=0, t2,_t2;
            double d = 0;
            String unit = "";
            public void handleMessage(Message m) {
                switch (m.what) {
                    case 0:
                        t2 = TrafficStats.getTotalRxBytes();
                        _t2 = TrafficStats.getTotalTxBytes();
                        _t = _t2 - _t1;
                        t = t2 - t1;
                        //设定阈值
                        if (t > 512&& t1!=0) {
                            //unit += "/s";
                            change(t);
                            if (!flag) {
                                windowManager.addView(view, layoutParams);
                                flag = true;
                            }
                        } else {
                            if (flag) {
                                windowManager.removeView(view);
                                flag = false;
                            }
                        }
                        textView.setText("" + d + unit);
                        textView0.setText("" + d + unit);
                        t1 = t2;
                        _t1 = _t2;
                }
            }

            private void change(long tt) {
                if (tt < 1024) {
                    d = (double) tt;
                    unit = "B/s";
                } else if (tt < 1024 * 1024) {
                    d = (double) tt / 1024;
                    unit = "KB/s";
                } else {
                    d = (double) tt / 1024 / 1024;
                    unit = "M/s";
                }
                //保留有效位
                d = (int) (d * 100) / 100.0;
            }
        };
        MyThread = new Thread() {
            @Override
            public void run() {
                while (Run) {
                    Message m = new Message();
                    m.what = 0;
                    tempHandler.sendMessage(m);
                    try {
                        Thread.sleep(SleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        MyThread.start();
    }

    private void readSetting() {
        SharedPreferences pre = getSharedPreferences(TMP_SAVED, MODE_WORLD_READABLE);
        SleepTime = (pre.getInt("reflash_time", 1)+1)*500;
        textColorTmp = pre.getString("ColorText", "#FFFFFF");
        textSize = pre.getFloat("text_size", 15);
    }

    private void onMove() {
        view.setOnTouchListener(new View.OnTouchListener() {
            float x, y;

            @Override
            public boolean onTouch(View p1, MotionEvent p2) {
                // TODO: Implement this method
                switch (p2.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = p2.getX();
                        y = p2.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        layoutParams.x += p2.getX() - x;
                        layoutParams.y += p2.getY() - y;
                        windowManager.updateViewLayout(view, layoutParams);
                        break;

                    default:
                        break;
                }
                return true;
            }


        });
    }

    @Override
    public void onDestroy() {
        //终止线程
        Run = false;
        MyThread.interrupt();
        MyThread = null;
        if (flag) windowManager.removeView(view);
        super.onDestroy();
    }
}

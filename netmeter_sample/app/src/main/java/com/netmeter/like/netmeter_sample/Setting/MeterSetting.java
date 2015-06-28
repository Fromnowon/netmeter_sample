package com.netmeter.like.netmeter_sample.Setting;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.netmeter.like.netmeter_sample.R;

/**
 * Created by like on 15/6/25.
 */
public class MeterSetting extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metersetting);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.metersetting_layout);
        linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }
}

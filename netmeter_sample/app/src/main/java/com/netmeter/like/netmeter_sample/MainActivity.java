package com.netmeter.like.netmeter_sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;


public class MainActivity extends ActionBarActivity {

    private static final String SETTINGS_SAVE = "settings_save";
    private Intent intent;
    private Switch aSwitch;
    private TextView textView, textView1, textView2;
    private ImageButton imageButton;
    private AlertDialog mAlertDialog;
    private String ColorText;
    private SeekBar seekBar;
    private float textSize;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapterTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSettings();
        intent = new Intent(MainActivity.this, com.netmeter.like.netmeter_sample.NetMeterService.class);

        //初始化时间间隔设置下拉菜单
        reflashTime();

        //设置颜色
        colorPicker();//初始化拾色器
        setColor();

        //设置进度条
        setSeekBar();

        //悬浮窗
        FloatWin();

        /*ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .setTheme(0)
                .build();
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        // repeat many times:
        ImageView itemIcon = new ImageView(this);
        ImageView itemIcon0 = new ImageView(this);
        ImageView itemIcon1 = new ImageView(this);
        itemIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        itemIcon0.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        itemIcon1.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(itemBuilder.setContentView(itemIcon).build())
                .addSubActionView(itemBuilder.setContentView(itemIcon0).build())
                .addSubActionView(itemBuilder.setContentView(itemIcon1).build())
                                // ...
                .attachTo(actionButton)
                .build();*/

    }

    private void reflashTime() {
        String[] flashtime = {"500ms", "1000ms", "1500ms", "2000ms"};//初始化函数中代码如下
        spinner = (Spinner) findViewById(R.id.reflashtime);
        this.adapterTime = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_dropdown_item, flashtime);//设置下拉框的数据适配器adapterCity
        this.spinner.setAdapter(adapterTime);
        final SharedPreferences pre = getSharedPreferences(SETTINGS_SAVE, MODE_WORLD_READABLE);
        //读取上一次设置
        spinner.setSelection(pre.getInt("reflash_time", 1), true);
        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = getSharedPreferences(SETTINGS_SAVE, MODE_WORLD_WRITEABLE).edit();
                editor.putInt("reflash_time", i);
                editor.commit();
                if (stopService(intent)) startService(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setSeekBar() {
        seekBar = (SeekBar) findViewById(R.id.set_textsize);
        seekBar.setMax(100);
        //seekBar.setProgress(50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress >= 0 && progress < 10) {
                    textSize = 10;
                    seekBar.setProgress(0);
                } else if (progress >= 10 && progress < 20) {
                    textSize = 11;
                    seekBar.setProgress(10);
                } else if (progress >= 20 && progress < 30) {
                    textSize = 12;
                    seekBar.setProgress(20);
                } else if (progress >= 30 && progress < 40) {
                    textSize = 13;
                    seekBar.setProgress(30);
                } else if (progress >= 40 && progress < 50) {
                    textSize = 14;
                    seekBar.setProgress(40);
                } else if (progress >= 50 && progress < 60) {
                    textSize = 15;
                    seekBar.setProgress(50);
                } else if (progress >= 60 && progress < 70) {
                    textSize = 16;
                    seekBar.setProgress(60);
                } else if (progress >= 70 && progress < 80) {
                    textSize = 17;
                    seekBar.setProgress(70);
                } else if (progress >= 80 && progress < 90) {
                    textSize = 18;
                    seekBar.setProgress(80);
                } else if (progress >= 90 && progress < 100) {
                    textSize = 19;
                    seekBar.setProgress(90);
                } else if (progress == 100) {
                    textSize = 20;
                    seekBar.setProgress(100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //更新预览textview
                textView2 = (TextView) findViewById(R.id.text_sample);
                textView2.setTextSize(textSize);

                //获得编辑器
                SharedPreferences.Editor editor = getSharedPreferences(SETTINGS_SAVE, MODE_WORLD_WRITEABLE).edit();
                //添加到编辑器
                editor.putFloat("text_size", textSize);
                editor.putInt("seekBarProgress", seekBar.getProgress());
                //提交编辑器内容
                editor.commit();
                //重启服务以应用更改
                if (stopService(intent)) startService(intent);
            }
        });
    }

    private void FloatWin() {
        aSwitch = (Switch) findViewById(R.id.netMeter);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(intent);
                    Toast.makeText(MainActivity.this, "悬浮窗开启！", Toast.LENGTH_SHORT).show();
                } else {
                    stopService(intent);
                    Toast.makeText(MainActivity.this, "悬浮窗关闭！", Toast.LENGTH_SHORT).show();
                }
                saveSettings(isChecked);
            }
        });
    }

    private void setColor() {
        textView = (TextView) findViewById(R.id.meterColor);
        imageButton = (ImageButton) findViewById(R.id.meterColor_click);
        textView.setText(ColorText);
        imageButton.setBackgroundColor(Color.parseColor(ColorText));
        //textView.setTextColor(Color.parseColor(ColorText));

        textView1 = (TextView) findViewById(R.id.text_sample);
        textView1.setTextColor(Color.parseColor(ColorText));
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mAlertDialog.show();
                return false;
            }
        });
    }


    private void loadSettings() {
        //获得SharedPreferences实例
        SharedPreferences pre = getSharedPreferences(SETTINGS_SAVE, MODE_WORLD_READABLE);
        //从SharedPreferences中获得内容
        String NetMeter = pre.getString("NetMeter", "");
        ColorText = pre.getString("ColorText", "#FFFFFF");
        aSwitch = (Switch) findViewById(R.id.netMeter);
        if (NetMeter.equals("ON")) aSwitch.setChecked(true);
        else aSwitch.setChecked(false);

        float textsize = pre.getFloat("text_size", 15);
        TextView textView_t = (TextView) findViewById(R.id.text_sample);
        textView_t.setTextColor(Color.parseColor(ColorText));
        textView_t.setTextSize(textsize);
        SeekBar seekBar_t = (SeekBar) findViewById(R.id.set_textsize);
        seekBar_t.setProgress(pre.getInt("seekBarProgress", 50));
    }

    public void saveSettings(Boolean isChecked) {
        //获得编辑器
        SharedPreferences.Editor editor = getSharedPreferences(SETTINGS_SAVE, MODE_WORLD_WRITEABLE).edit();
        //将内容添加到编辑器
        if (isChecked) editor.putString("NetMeter", "ON");
        else editor.putString("NetMeter", "OFF");
        //提交编辑器内容
        editor.commit();
    }

    private void colorPicker() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView colorText = new TextView(this);
        ColorPickerView colorPick = new ColorPickerView(this, Color.parseColor("#FFFFFF"), 0.8, colorText);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        layout.addView(colorPick, lp);
        layout.addView(colorText, lp);

        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.choose_a_color))
                .setView(layout)
                .setPositiveButton(getString(R.string.dialog_color_OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Log.d("TAG", "the color choose is " + colorText.getText());
                        //捕获最终颜色
                        ColorText = colorText.getText().toString();
                        textView.setText(ColorText);
                        //textView.setTextColor(Color.parseColor(ColorText));
                        textView1.setTextColor(Color.parseColor(ColorText));
                        imageButton.setBackgroundColor(Color.parseColor(ColorText));
                        //保存颜色
                        SharedPreferences.Editor editor = getSharedPreferences(SETTINGS_SAVE, MODE_WORLD_WRITEABLE).edit();
                        editor.putString("ColorText", ColorText);
                        editor.commit();
                        if (stopService(intent)) startService(intent);

                    }
                })
                .setNegativeButton(getString(R.string.dialog_color_cancle), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create();
    }

}

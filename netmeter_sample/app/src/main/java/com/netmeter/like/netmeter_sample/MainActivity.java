package com.netmeter.like.netmeter_sample;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gc.materialdesign.widgets.Dialog;
import com.kyleduo.switchbutton.SwitchButton;
import com.netmeter.like.netmeter_sample.Service.NetMeterService;
import com.netmeter.like.netmeter_sample.Setting.MeterSetting;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import de.keyboardsurfer.android.widget.crouton.Crouton;


public class MainActivity extends ActionBarActivity {

    private static final String SETTINGS_SAVE = "settings_save";
    private Intent intent;
    private SwitchButton aSwitch;
    private TextView textView, textView1, textView2;
    private ImageButton imageButton;
    private String ColorText;
    private DiscreteSeekBar seekBar;
    private float textSize;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapterTime = null;
    //AlertDialog mAlertDialog;
    ColorPickerDialog mAlertDialog;
    //用于回掉销毁主activity
    public static MainActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        intent = new Intent(MainActivity.this, NetMeterService.class);

        //初始化设置
        loadSettings();

        //所谓沉浸
        transLucentStatus();

        //初始化时间间隔设置下拉菜单
        reflashTime();

        //初始化拾色器
        colorPicker();
        setColor();

        //设置进度条
        setSeekBar();

        //悬浮窗
        FloatWin();

        //小彩蛋
        //floatBtn();

    }

    private void floatBtn() {
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_manage));
        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .setTheme(0)
                .build();
        // repeat many times:
        FrameLayout.LayoutParams tvParams = new FrameLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View view0 = LayoutInflater.from(this).inflate(R.layout.metersetting, null);
        view0.setLayoutParams(tvParams);
        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(view0)
                .setStartAngle(225)
                .attachTo(actionButton)
                .build();
    }

    public void transLucentStatus() {
        //取消actionbar阴影
        //getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Demo");
        //getSupportActionBar().hide();
        setTranslucentStatus(true);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.MyBar);
        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.mainlayout);
        relativeLayout.setPadding(0, config.getPixelInsetTop(true), 0, config.getPixelInsetBottom());
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
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
        seekBar = (DiscreteSeekBar) findViewById(R.id.set_textsize);
        seekBar.setMax(100);
        seekBar.setIndicatorPopupEnabled(false);
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int progress, boolean b) {
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
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {
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
        aSwitch = (SwitchButton) findViewById(R.id.netMeter);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(intent);
                    //Toast.makeText(MainActivity.this, "悬浮窗开启！", Toast.LENGTH_SHORT).show();
                    SnackbarManager.show(
                            Snackbar.with(MainActivity.this)
                                    .text("悬浮窗启动！")
                                    .color(Color.parseColor("#ff098173"))
                                    .actionLabel("确定")
                                    .actionLabelTypeface(Typeface.DEFAULT_BOLD)
                                    .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                                    .type(SnackbarType.MULTI_LINE));
                } else {
                    stopService(intent);
                    //Toast.makeText(MainActivity.this, "悬浮窗关闭！", Toast.LENGTH_SHORT).show();
                    SnackbarManager.show(
                            Snackbar.with(MainActivity.this)
                                    .text("悬浮窗关闭！")
                                    .color(Color.parseColor("#ffff4444"))
                                    .actionLabel("确定")
                                    .actionLabelTypeface(Typeface.DEFAULT_BOLD)
                                    .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                                    .type(SnackbarType.MULTI_LINE));
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
        aSwitch = (SwitchButton) findViewById(R.id.netMeter);
        if (NetMeter.equals("ON")) aSwitch.setChecked(true);
        else aSwitch.setChecked(false);

        float textsize = pre.getFloat("text_size", 15);
        TextView textView_t = (TextView) findViewById(R.id.text_sample);
        textView_t.setTextColor(Color.parseColor(ColorText));
        textView_t.setTextSize(textsize);
        DiscreteSeekBar seekBar_t = (DiscreteSeekBar) findViewById(R.id.set_textsize);
        seekBar_t.setProgress(pre.getInt("seekBarProgress", 50));
    }

    public void saveSettings(Boolean isChecked) {
        SharedPreferences.Editor editor = getSharedPreferences(SETTINGS_SAVE, MODE_WORLD_WRITEABLE).edit();
        if (isChecked) editor.putString("NetMeter", "ON");
        else editor.putString("NetMeter", "OFF");
        editor.commit();
    }

    private void colorPicker() {
        //新版本拾色器
        mAlertDialog = new ColorPickerDialog(this, Color.parseColor(ColorText));
        //mAlertDialog.setAlphaSliderVisible(true);
        //mAlertDialog.setHexValueEnabled(true);
        mAlertDialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                //捕获最终颜色
                StringBuffer s = new StringBuffer(Integer.toHexString(color));
                s.delete(0, 2);
                s.insert(0, "#");
                ColorText = s.toString();
                textView.setText(ColorText);
                //textView.setTextColor(Color.parseColor(ColorText));
                textView1.setTextColor(Color.parseColor(ColorText));
                imageButton.setBackgroundColor(Color.parseColor(ColorText));
                //保存颜色
                SharedPreferences.Editor editor = getSharedPreferences(SETTINGS_SAVE, MODE_WORLD_WRITEABLE).edit();
                editor.putString("ColorText", ColorText);
                editor.commit();
                if (stopService(intent)) startService(intent);
                mAlertDialog.dismiss();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_about: {
                //View view_about = LayoutInflater.from(this).inflate(R.layout.about_view, null);
                new Dialog(this, "呵呵", "A test！！！").show();
                break;
            }
            case R.id.action_exit: {
                finish();
                break;
            }
            case R.id.action_settings: {
                Intent intent_setting = new Intent(this, MeterSetting.class);
                startActivity(intent_setting);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }
}

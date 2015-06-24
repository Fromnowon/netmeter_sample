package com.netmeter.like.netmeter_sample;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


public class MainActivity extends ActionBarActivity {

    private static final String SETTINGS_SAVE = "settings_save";
    private Intent intent;
    private SwitchButton aSwitch;
    private TextView textView, textView1, textView2;
    private ImageButton imageButton;
    private AlertDialog mAlertDialog;
    private String ColorText;
    private DiscreteSeekBar seekBar;
    private float textSize;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapterTime = null;
    //用于回掉销毁主activity
    public static MainActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        loadSettings();
        intent = new Intent(MainActivity.this, com.netmeter.like.netmeter_sample.NetMeterService.class);

        //所谓沉浸
        transLucentStatus();

        //初始化时间间隔设置下拉菜单
        reflashTime();

        //设置颜色
        colorPicker();//初始化拾色器
        setColor();

        //设置进度条
        setSeekBar();

        //悬浮窗
        FloatWin();

        //小彩蛋
        //floatBtn();

    }

    //剪切bitmap实现圆角
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private void floatBtn() {
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .setTheme(0)
                .build();
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        // repeat many times:
        FrameLayout.LayoutParams tvParams = new FrameLayout.LayoutParams(100, 100);
        itemBuilder.setLayoutParams(tvParams);

        ImageView itemIcon = new ImageView(this);
        TextView textViewM = new TextView(this);
        textViewM.setText("点个赞哟!" + "\n" + "_(:з」∠)_");
        textViewM.setTextSize(18);
        textViewM.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //textViewM.setGravity(Gravity.CENTER_VERTICAL);
        //itemIcon.setImageDrawable(getResources().getDrawable(R.drawable.myic));

        Drawable drawable = getResources().getDrawable(R.drawable.myic);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(toRoundCorner(bitmap, 180));
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        //圆角函数存在局限性
        if (metric.densityDpi<=320){
            imageView.setImageBitmap(toRoundCorner(bitmap, 120));
            imageView.setLayoutParams(new ViewGroup.LayoutParams(120,120));
        }
        else {
            imageView.setImageBitmap(toRoundCorner(bitmap, 180));
            imageView.setLayoutParams(new ViewGroup.LayoutParams(180, 180));
        }

        itemIcon.setLayoutParams(tvParams);
        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(imageView)
                .addSubActionView(textViewM)
                .attachTo(actionButton)
                .setStartAngle(190)
                .setEndAngle(260)
                .build();
    }

    public void transLucentStatus() {
        //取消actionbar阴影
        //getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Demo");
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
        //seekBar.setProgress(50);
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
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView colorText = new TextView(this);
        //根据不同分辨率调整拾色器尺寸
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        double zoom;
        if (metric.densityDpi <= 320) zoom = 0.8;
        else zoom = 2.0;
        ColorPickerView colorPick = new ColorPickerView(this, Color.parseColor("#FFFFFF"), zoom, colorText);

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

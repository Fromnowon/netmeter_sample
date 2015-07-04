package com.netmeter.like.netmeter.Fragmments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.kyleduo.switchbutton.SwitchButton;
import com.netmeter.like.netmeter.R;
import com.netmeter.like.netmeter.Services.NetMeterService;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import net.margaritov.preference.colorpicker.ColorPickerDialog;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by like on 15/6/28.
 */
public class Fragment_MeterSetting extends Fragment {

    private DiscreteSeekBar seekBar;
    private Spinner spinner;
    private static final String SETTINGS_SAVE = "settings_save";
    Intent intent;
    private float textSize;
    ColorPickerDialog mAlertDialog;
    private SwitchButton aSwitch;
    private TextView textView, textView1, textView2;
    private ImageButton imageButton;
    private String ColorText;
    private ArrayAdapter<CharSequence> adapterTime = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment1, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        intent = new Intent(getActivity(), NetMeterService.class);
        loadSettings();
        setSeekBar();
        colorPicker();
        reflashTime();
        setColor();
        FloatWin();
    }

    private void setSeekBar() {
        seekBar = (DiscreteSeekBar) getView().findViewById(R.id.set_textsize);
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
                textView2 = (TextView) getView().findViewById(R.id.text_sample);
                textView2.setTextSize(textSize);

                //获得编辑器
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(SETTINGS_SAVE, Context.MODE_WORLD_WRITEABLE).edit();
                //添加到编辑器
                editor.putFloat("text_size", textSize);
                editor.putInt("seekBarProgress", seekBar.getProgress());
                //提交编辑器内容
                editor.commit();
                //重启服务以应用更改
                if (getActivity().stopService(intent)) getActivity().startService(intent);
            }
        });
    }

    private void FloatWin() {
        aSwitch = (SwitchButton) getView().findViewById(R.id.netMeter);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getActivity().startService(intent);
                    //Toast.makeText(MainActivity.this, "悬浮窗开启！", Toast.LENGTH_SHORT).show();
                    SnackbarManager.show(
                            Snackbar.with(getActivity())
                                    .text("悬浮窗启动！")
                                    .color(Color.parseColor("#ff098173"))
                                    .actionLabel("确定")
                                    .actionLabelTypeface(Typeface.DEFAULT_BOLD)
                                    .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                                    .type(SnackbarType.MULTI_LINE));
                } else {
                    getActivity().stopService(intent);
                    //Toast.makeText(MainActivity.this, "悬浮窗关闭！", Toast.LENGTH_SHORT).show();
                    SnackbarManager.show(
                            Snackbar.with(getActivity())
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

    private void colorPicker() {
        //新版本拾色器
        mAlertDialog = new ColorPickerDialog(getActivity(), Color.parseColor(ColorText));
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
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(SETTINGS_SAVE, Context.MODE_WORLD_WRITEABLE).edit();
                editor.putString("ColorText", ColorText);
                editor.commit();
                if (getActivity().stopService(intent)) getActivity().startService(intent);
                mAlertDialog.dismiss();
            }
        });
    }

    public void saveSettings(Boolean isChecked) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(SETTINGS_SAVE, Context.MODE_WORLD_WRITEABLE).edit();
        if (isChecked) editor.putString("NetMeter", "ON");
        else editor.putString("NetMeter", "OFF");
        editor.commit();
    }

    private void reflashTime() {
        String[] flashtime = {"500ms", "1000ms", "1500ms", "2000ms"};
        spinner = (Spinner) getView().findViewById(R.id.reflashtime);
        adapterTime = new ArrayAdapter<CharSequence>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, flashtime);//设置下拉框的数据适配器adapterCity
        this.spinner.setAdapter(adapterTime);
        final SharedPreferences pre = getActivity().getSharedPreferences(SETTINGS_SAVE, Context.MODE_WORLD_READABLE);
        //读取上一次设置
        spinner.setSelection(pre.getInt("reflash_time", 1), true);
        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(SETTINGS_SAVE, Context.MODE_WORLD_WRITEABLE).edit();
                editor.putInt("reflash_time", i);
                editor.commit();
                if (getActivity().stopService(intent)) getActivity().startService(intent);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setColor() {
        textView = (TextView) getView().findViewById(R.id.meterColor);
        imageButton = (ImageButton) getView().findViewById(R.id.meterColor_click);
        textView.setText(ColorText);
        imageButton.setBackgroundColor(Color.parseColor(ColorText));
        //textView.setTextColor(Color.parseColor(ColorText));
        textView1 = (TextView) getView().findViewById(R.id.text_sample);
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
        SharedPreferences pre = getActivity().getSharedPreferences(SETTINGS_SAVE, Context.MODE_WORLD_READABLE);
        //从SharedPreferences中获得内容
        String NetMeter = pre.getString("NetMeter", "");
        ColorText = pre.getString("ColorText", "#FFFFFF");
        aSwitch = (SwitchButton) getView().findViewById(R.id.netMeter);
        if (NetMeter.equals("ON")) aSwitch.setChecked(true);
        else aSwitch.setChecked(false);

        float textsize = pre.getFloat("text_size", 15);
        TextView textView_t = (TextView) getView().findViewById(R.id.text_sample);
        textView_t.setTextColor(Color.parseColor(ColorText));
        textView_t.setTextSize(textsize);
        DiscreteSeekBar seekBar_t = (DiscreteSeekBar) getView().findViewById(R.id.set_textsize);
        seekBar_t.setProgress(pre.getInt("seekBarProgress", 50));

    }
}

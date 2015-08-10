package com.netmeter.like.netmeter.Fragmments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.kyleduo.switchbutton.SwitchButton;
import com.netmeter.like.netmeter.R;
import com.netmeter.like.netmeter.Services.NetMeterService;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by like on 15/6/28.
 */
public class Fragment_MeterSetting extends Fragment {

    private DiscreteSeekBar seekBar, seekBar0;
    private Spinner spinner;
    private static final String SETTINGS_SAVE = "settings_save";
    private Intent intent;
    private float textSize;
    private int alpha;
    private ColorPickerDialog mAlertDialog, mAlertDialog0;
    private SwitchButton aSwitch;
    private TextView textView, textView1, textView2;
    private ImageButton imageButton, imageButton0;
    private String ColorText, ColorBG, alpha_hex;
    private ArrayAdapter<CharSequence> adapterTime = null;
    private SystemBarTintManager tintManager;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment1, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        intent = new Intent(getActivity(), NetMeterService.class);
        tintManager = new SystemBarTintManager(getActivity());
        editor = getActivity().getSharedPreferences(SETTINGS_SAVE, Context.MODE_WORLD_WRITEABLE).edit();
        loadSettings();
        setTextSize();
        colorPicker();
        reflashTime();
        setColor();
        setAlpha();
    }

    public void sendBroadcast_custum(String action, String param, String type, Object extra) {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
        Intent intent = new Intent(action);
        switch (type) {
            case "float": {
                intent.putExtra(param, (float) extra);
                break;
            }
            case "int": {
                intent.putExtra(param, (int) extra);
                break;
            }
            case "String": {
                intent.putExtra(param, (String) extra);
                break;
            }
            default:
                break;
        }
        lbm.sendBroadcast(intent);
    }

    private void setTextSize() {
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
                editor.putFloat("text_size", textSize);
                editor.putInt("seekBarProgress", seekBar.getProgress());
                editor.commit();
                sendBroadcast_custum("com.like.CHANGETEXTSIZE", "textsize", "float", textSize);
            }
        });
    }

    private void colorPicker() {
        //新版本拾色器
        mAlertDialog = new ColorPickerDialog(getActivity(), Color.parseColor(ColorText));
        mAlertDialog0 = new ColorPickerDialog(getActivity(), Color.parseColor(ColorBG));
        mAlertDialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                //捕获最终颜色
                StringBuffer s = new StringBuffer(Integer.toHexString(color));
                s.delete(0, 2);
                s.insert(0, "#");
                ColorText = s.toString();
                textView.setText(ColorText);
                textView1.setTextColor(Color.parseColor(ColorText));
                imageButton.setBackgroundColor(Color.parseColor(ColorText));
                //保存颜色
                editor.putString("ColorText", ColorText);
                editor.commit();
                sendBroadcast_custum("com.like.SETTEXTCOLOR", "colorText", "String", ColorText);
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog0.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                StringBuffer s = new StringBuffer(Integer.toHexString(color));
                //存储8位
                editor.putString("ColorBG_L", s.insert(0, "#").toString());
                sendBroadcast_custum("com.like.CHANGEBGCOLOR", "colorBG", "String", s.toString());
                s.delete(1, 3);
                ColorBG = s.toString();
                imageButton0.setBackgroundColor(Color.parseColor(ColorBG));
                editor.putString("ColorBG", ColorBG);
                editor.commit();
            }
        });
    }

    private void reflashTime() {
        String[] flashtime = {"500ms", "1000ms", "1500ms", "2000ms"};
        spinner = (Spinner) getView().findViewById(R.id.reflashtime);
        adapterTime = new ArrayAdapter<CharSequence>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, flashtime);
        this.spinner.setAdapter(adapterTime);
        final SharedPreferences pre = getActivity().getSharedPreferences(SETTINGS_SAVE, Context.MODE_WORLD_READABLE);
        //读取上一次设置
        spinner.setSelection(pre.getInt("reflash_time", 1), true);
        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putInt("reflash_time", i);
                editor.commit();
                sendBroadcast_custum("com.like.CHANGEREFLASHTIME", "reflashtime", "int", i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setColor() {
        textView = (TextView) getView().findViewById(R.id.meterColor);
        imageButton = (ImageButton) getView().findViewById(R.id.meterColor_click);
        imageButton0 = (ImageButton) getView().findViewById(R.id.bgColor_click);
        textView.setText(ColorText);
        imageButton.setBackgroundColor(Color.parseColor(ColorText));
        imageButton0.setBackgroundColor(Color.parseColor(ColorBG));
        textView1 = (TextView) getView().findViewById(R.id.text_sample);
        textView1.setTextColor(Color.parseColor(ColorText));
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mAlertDialog.show();
                return false;
            }
        });
        imageButton0.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mAlertDialog0.show();
                return false;
            }
        });
    }

    private void loadSettings() {
        SharedPreferences pre = getActivity().getSharedPreferences(SETTINGS_SAVE, Context.MODE_WORLD_READABLE);
        String NetMeter = pre.getString("NetMeter", "");
        ColorText = pre.getString("ColorText", "#FFFFFF");
        ColorBG = pre.getString("ColorBG", "#e3e3e3");
        aSwitch = (SwitchButton) getView().findViewById(R.id.netMeter);
        if (NetMeter.equals("ON")) {
            aSwitch.setChecked(true);
        } else {
            aSwitch.setChecked(false);
        }

        float textsize = pre.getFloat("text_size", 15);
        TextView textView_t = (TextView) getView().findViewById(R.id.text_sample);
        textView_t.setTextColor(Color.parseColor(ColorText));
        textView_t.setTextSize(textsize);
        DiscreteSeekBar seekBar_t = (DiscreteSeekBar) getView().findViewById(R.id.set_textsize);
        seekBar_t.setProgress(pre.getInt("seekBarProgress", 50));
        alpha = pre.getInt("BGAlpha_PG", 50);

    }

    private void setAlpha() {
        seekBar0 = (DiscreteSeekBar) getView().findViewById(R.id.set_alpha);
        seekBar0.setProgress(alpha);
        seekBar0.setMax(100);
        seekBar0.setIndicatorPopupEnabled(false);
        seekBar0.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {
                alpha = i;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {
                editor.putInt("BGAlpha_PG", alpha);
                editor.commit();
                alpha_hex = Integer.toHexString((int) ((alpha / 100f) * 255));
                if (alpha_hex.length() == 1) alpha_hex = "0"+alpha_hex;
                sendBroadcast_custum("com.like.CHANGEBGALPHA", "BGAlpha", "String", alpha_hex);
            }
        });
    }
}

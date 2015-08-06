package com.netmeter.like.netmeter.Fragmments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.netmeter.like.netmeter.R;
import com.netmeter.like.netmeter.Services.NetMeterService;
import com.rey.material.widget.CheckBox;

/**
 * Created by like on 15/6/28.
 */
public class Fragment_GlobleSetting extends Fragment {

    private CheckBox show_upload;
    private CheckBox auto_hide;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment3, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        show_upload = (CheckBox) getView().findViewById(R.id.show_upload_speed);
        auto_hide = (CheckBox) getView().findViewById(R.id.auto_hide);
        //还原设置
        SharedPreferences pre = getActivity().getSharedPreferences("settings_save", Context.MODE_WORLD_READABLE);
        if (pre.getBoolean("show_upload_speed", true))
            show_upload.setCheckedImmediately(true);
        if (pre.getBoolean("auto_hide", true))
            auto_hide.setCheckedImmediately(true);
        final SharedPreferences.Editor editor = getActivity().getSharedPreferences("settings_save", Context.MODE_WORLD_WRITEABLE).edit();
        final Intent intent = new Intent(getActivity(), NetMeterService.class);
        //上传速度的显示监听
        show_upload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("show_upload_speed", true);
                    editor.commit();
                    if (getActivity().stopService(intent)) getActivity().startService(intent);
                    Toast.makeText(getActivity(), "显示上传速度！", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("show_upload_speed", false);
                    editor.commit();
                    if (getActivity().stopService(intent)) getActivity().startService(intent);
                    Toast.makeText(getActivity(), "隐藏上传速度！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //自动隐藏的监听
        auto_hide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("auto_hide", true);
                    editor.commit();
                    if (getActivity().stopService(intent)) getActivity().startService(intent);
                    Toast.makeText(getActivity(), "空闲自动隐藏！", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("auto_hide", false);
                    editor.commit();
                    if (getActivity().stopService(intent)) getActivity().startService(intent);
                    Toast.makeText(getActivity(), "取消自动隐藏！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}

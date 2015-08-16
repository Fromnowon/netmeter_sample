package com.netmeter.like.netmeter;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.kyleduo.switchbutton.SwitchButton;
import com.netmeter.like.netmeter.DataBase.DataUsageDB;
import com.netmeter.like.netmeter.Fragmments.Fragment_About;
import com.netmeter.like.netmeter.Fragmments.Fragment_GlobleSetting;
import com.netmeter.like.netmeter.Fragmments.Fragment_MeterSetting;
import com.netmeter.like.netmeter.Fragmments.Fragment_Index;
import com.netmeter.like.netmeter.Fragmments.Fragment_Usage;
import com.netmeter.like.netmeter.Services.NetMeterService;
import com.readystatesoftware.systembartint.SystemBarTintManager;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int flag = 0;
    private SystemBarTintManager tintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDrawer();
        initShow();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //启动流量统计服务
        //Intent intent = new Intent(this, DataUsageService.class);
        //startService(intent);
    }


    private void initShow() {
        //处理headview


        SimpleAdapter adapter = new SimpleAdapter(this, getData(),
                R.layout.draweritemlayout, new String[]{"img", "title"},
                new int[]{R.id.drawer_iv, R.id.drawer_tv});
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup loadMoreLayout = (ViewGroup) mInflater.inflate(R.layout.drawer_headview, null);
        mDrawerList.addHeaderView(loadMoreLayout, null, false);
        mDrawerList.setAdapter(adapter);
        final Fragment fragment0 = new Fragment_Index();
        final Fragment fragment1 = new Fragment_MeterSetting();
        final Fragment fragment2 = new Fragment_Usage();
        final Fragment fragment3 = new Fragment_GlobleSetting();
        final Fragment fragment4 = new Fragment_About();
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .add(R.id.fragment_layout, fragment0)
                .add(R.id.fragment_layout, fragment1)
                .add(R.id.fragment_layout, fragment2)
                .add(R.id.fragment_layout, fragment3)
                .add(R.id.fragment_layout, fragment4)
                .show(fragment0).hide(fragment1).hide(fragment2).hide(fragment3).hide(fragment4)
                .commit();
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragmentTransaction = fragmentManager.beginTransaction();
                switch (position-1) {
                    case 0: {
                        if (flag != 0)
                            fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        tintManager.setStatusBarTintResource(R.color.my_bar_index);
                        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_index));
                        //延迟加载，解决加载卡顿问题
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragmentTransaction.hide(fragment1).hide(fragment2).hide(fragment3).hide(fragment4).show(fragment0).commit();
                            }
                        }, 200);
                        mDrawerLayout.closeDrawers();
                        flag = 0;
                        break;
                    }
                    case 1: {
                        if (flag != 1)
                            fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        final SwitchButton btn = (SwitchButton) findViewById(R.id.netMeter);
                        if (btn.isChecked()) {
                            tintManager.setStatusBarTintResource(R.color.my_bar_setting);
                            getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_setting));
                        } else {
                            tintManager.setStatusBarTintResource(R.color.colorAccent);
                            getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorAccent));
                        }
                        final SharedPreferences.Editor editor = getSharedPreferences("settings_save", Context.MODE_WORLD_WRITEABLE).edit();
                        btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    startService(new Intent(getApplicationContext(), NetMeterService.class));
                                    editor.putString("NetMeter", "ON");
                                    tintManager.setStatusBarTintResource(R.color.my_bar_setting);
                                    getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_setting));
                                    com.gitonway.lee.niftynotification.lib.Configuration cfg = new com.gitonway.lee.niftynotification.lib.Configuration.Builder()
                                            .setAnimDuration(700)
                                            .setDispalyDuration(700)
                                            .setBackgroundColor("#ff098173")
                                            .setTextColor("#ffffffff")
                                            .setIconBackgroundColor("#ff098173")
                                            .setTextPadding(12)
                                            .setViewHeight(40)
                                            .setTextLines(2)
                                            .setTextGravity(Gravity.CENTER)
                                            .build();
                                    NiftyNotificationView.build(MainActivity.this, "悬浮窗开启！", Effects.jelly, R.id.mLayout, cfg)
                                            .show();
                                }
                                else {
                                    stopService(new Intent(getApplicationContext(), NetMeterService.class));
                                    editor.putString("NetMeter", "OFF");
                                    tintManager.setStatusBarTintResource(R.color.colorAccent);
                                    getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorAccent));
                                    com.gitonway.lee.niftynotification.lib.Configuration cfg = new com.gitonway.lee.niftynotification.lib.Configuration.Builder()
                                            .setAnimDuration(700)
                                            .setDispalyDuration(700)
                                            .setBackgroundColor("#ff0000")
                                            .setTextColor("#ffffffff")
                                            .setIconBackgroundColor("#ff0000")
                                            .setTextPadding(12)
                                            .setViewHeight(40)
                                            .setTextLines(2)
                                            .setTextGravity(Gravity.CENTER)
                                            .build();
                                    NiftyNotificationView.build(MainActivity.this, "悬浮窗关闭！", Effects.jelly, R.id.mLayout, cfg)
                                            .show();
                                }
                                editor.commit();
                            }
                        });
                        mDrawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragmentTransaction.hide(fragment0).hide(fragment2).hide(fragment3).hide(fragment4).show(fragment1).commit();
                            }
                        }, 200);
                        flag = 1;
                        break;
                    }
                    case 2: {
                        if (flag != 2)
                            fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        tintManager.setStatusBarTintResource(R.color.my_bar_usage);
                        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_usage));
                        mDrawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragmentTransaction.hide(fragment0).hide(fragment1).hide(fragment3).hide(fragment4).show(fragment2).commit();
                            }
                        }, 200);
                        flag = 2;
                        break;
                    }
                    case 3: {
                        if (flag != 3)
                            fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        tintManager.setStatusBarTintResource(R.color.my_bar_globle);
                        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_globle));
                        mDrawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragmentTransaction.hide(fragment0).hide(fragment1).hide(fragment2).hide(fragment4).show(fragment3).commit();
                            }
                        }, 200);
                        flag = 3;
                        break;
                    }
                    case 4: {
                        if (flag != 4)
                            fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        tintManager.setStatusBarTintResource(R.color.my_bar_aboout);
                        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_aboout));
                        mDrawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragmentTransaction.hide(fragment0).hide(fragment1).hide(fragment2).hide(fragment3).show(fragment4).commit();
                            }
                        }, 200);
                        flag = 4;
                        break;
                    }
                }
            }
        });
    }

    private void initDrawer() {
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        transLucentStatus();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                drawerArrow, R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        else if (item.getItemId() == R.id.action_cleardata){
            DataUsageDB db = new DataUsageDB(this);
            db.restore();
            Toast.makeText(this, "流量统计数据已重置！", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (flag == 2){
            return true;
        }
        else return false;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("img", R.drawable.list_index);
        map.put("title", "主页概览");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.list_network);
        map.put("title", "悬浮窗设置");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.list_chart);
        map.put("title", "流量统计");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.list_settings);
        map.put("title", "全局设置");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.list_about);
        map.put("title", "关于");
        list.add(map);

        return list;
    }

    public void transLucentStatus() {
        //取消actionbar阴影
        //getSupportActionBar().setElevation(0);
        getActionBar().setTitle("Demo");
        setTranslucentStatus(true);
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.my_bar_index);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_index));
        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
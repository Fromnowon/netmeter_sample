package com.netmeter.like.netmeter;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.netmeter.like.netmeter.Fragmments.Fragment_GlobleSetting;
import com.netmeter.like.netmeter.Fragmments.Fragment_MeterSetting;
import com.netmeter.like.netmeter.Fragmments.Fragment_Index;
import com.netmeter.like.netmeter.Fragmments.Fragment_Usage;
import com.netmeter.like.netmeter.Services.DataUsageService;
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
        Intent intent = new Intent(this, DataUsageService.class);
        startService(intent);
    }



    private void initShow() {
        SimpleAdapter adapter = new SimpleAdapter(this, getData(),
                R.layout.draweritemlayout, new String[]{"img", "title"},
                new int[]{R.id.drawer_iv, R.id.drawer_tv});
        mDrawerList.setAdapter(adapter);
        final Fragment fragment0 = new Fragment_Index();
        final Fragment fragment1 = new Fragment_MeterSetting();
        final Fragment fragment2 = new Fragment_Usage();
        final Fragment fragment3 = new Fragment_GlobleSetting();
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.add(R.id.fragment_layout, new Fragment_Index(), "index").commit();
        //程序启动完成后全部实例化
        fragmentTransaction
                .add(R.id.fragment_layout, fragment0)
                .add(R.id.fragment_layout, fragment1)
                .add(R.id.fragment_layout, fragment2)
                .add(R.id.fragment_layout, fragment3)
                .show(fragment0).hide(fragment1).hide(fragment2).hide(fragment3)
                .commit();
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragmentTransaction = fragmentManager.beginTransaction();
                switch (position) {
                    case 0: {
                        if (flag != 0)
                            fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        tintManager.setStatusBarTintResource(R.color.my_bar_index);
                        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_index));
                        //fragmentTransaction.replace(R.id.fragment_layout, new Fragment_Index(), "index").commit();
                        //延迟加载，解决加载卡顿问题
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragmentTransaction.show(fragment0).hide(fragment1).hide(fragment2).hide(fragment3).commit();
                            }
                        }, 200);
                        mDrawerLayout.closeDrawers();
                        flag = 0;
                        break;
                    }
                    case 1: {
                        if (flag != 1)
                            fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        tintManager.setStatusBarTintResource(R.color.my_bar_setting);
                        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_setting));
                        //fragmentTransaction.replace(R.id.fragment_layout, new Fragment_MeterSetting(), "setting").commit();
                        mDrawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragmentTransaction.hide(fragment0).show(fragment1).hide(fragment2).hide(fragment3).commit();
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
                        //fragmentTransaction.replace(R.id.fragment_layout, new Fragment_Usage(), "usage").commit();
                        mDrawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragmentTransaction.hide(fragment0).hide(fragment1).show(fragment2).hide(fragment3).commit();                            }
                        }, 100);
                        flag = 2;
                        break;
                    }
                    case 3: {
                        if (flag != 3)
                            fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        tintManager.setStatusBarTintResource(R.color.my_bar_globle);
                        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_globle));
                        //fragmentTransaction.replace(R.id.fragment_layout, new Fragment_GlobleSetting(), "globlesetting").commit();
                        mDrawerLayout.closeDrawers();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragmentTransaction.hide(fragment0).hide(fragment1).hide(fragment2).show(fragment3).commit();
                            }
                        }, 100);
                        flag = 3;
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
        return super.onOptionsItemSelected(item);
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

        map.put("img", R.drawable.ic_menu_selectall_holo_light);
        map.put("title", "主页概览");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.perm_group_network);
        map.put("title", "悬浮窗设置");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.ic_perm_group_display);
        map.put("title", "流量统计");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.perm_group_system_tools);
        map.put("title", "全局设置");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.airbutton_icon_share);
        map.put("title", "关于");
        list.add(map);

        return list;
    }

    public void transLucentStatus() {
        //取消actionbar阴影
        //getSupportActionBar().setElevation(0);
        getActionBar().setTitle("Demo");
        //getSupportActionBar().hide();
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
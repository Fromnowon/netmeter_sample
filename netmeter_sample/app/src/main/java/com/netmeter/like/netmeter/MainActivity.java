package com.netmeter.like.netmeter;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
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
import com.netmeter.like.netmeter.Fragmments.Fragment_MeterSetting;
import com.netmeter.like.netmeter.Fragmments.Fragment_index;
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
    private Fragment[] mFragments;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private static final String SETTINGS_SAVE = "settings_save";
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDrawer();
        initShow();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void initShow() {
        SimpleAdapter adapter = new SimpleAdapter(this, getData(),
                R.layout.draweritemlayout, new String[]{"img", "title"},
                new int[]{R.id.drawer_iv, R.id.drawer_tv});
        mDrawerList.setAdapter(adapter);
        mFragments = new Fragment[2];
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left,R.animator.slide_out_right);
        mFragments[0] = new Fragment_index();
        mFragments[1] = new Fragment_MeterSetting();
        fragmentTransaction.add(R.id.fragment_layout, mFragments[0], "index").commit();
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_left,R.animator.slide_out_right);
                switch (position) {
                    case 0: {
                        fragmentTransaction.replace(R.id.fragment_layout, mFragments[0], "index").commit();
                        mDrawerLayout.closeDrawers();
                        flag = 0;
                        break;
                    }
                    case 1: {
                        //fragmentTransaction.show(mFragments[1]).commit();
                        fragmentTransaction.replace(R.id.fragment_layout, mFragments[1], "setting").commit();
                        mDrawerLayout.closeDrawers();
                        flag = 1;
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
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.setting_bar);
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
    protected void onSaveInstanceState(Bundle outState){
        //保存当前fragment标记
        SharedPreferences.Editor editor = getSharedPreferences(SETTINGS_SAVE, MODE_WORLD_WRITEABLE).edit();
        editor.putInt("restore_fragment", flag).commit();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        //恢复fragment
        SharedPreferences pre = getSharedPreferences(SETTINGS_SAVE, MODE_WORLD_READABLE);
        switch (pre.getInt("restore_fragment", 0)){
            case 0:{
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new Fragment_index()).commit();
                flag = 0;
                break;
            }
            case 1:{
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, new Fragment_MeterSetting()).commit();
                flag = 1;
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.closeDrawers();
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
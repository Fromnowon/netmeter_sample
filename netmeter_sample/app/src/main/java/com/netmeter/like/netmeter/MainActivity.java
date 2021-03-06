package com.netmeter.like.netmeter;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import java.util.Timer;
import java.util.TimerTask;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int flag = 0;
    private SystemBarTintManager tintManager;
    private int cur_pos = 0;// 当前显示的一行
    private String[] items_text = {"主页概览", "悬浮窗设置", "流量统计", "全局设置", "关于"};
    private int[] items_img = {R.drawable.list_index, R.drawable.list_network, R.drawable.list_chart, R.drawable.list_settings, R.drawable.list_about};

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
        final MyAdapter adapter = new MyAdapter(this);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
                switch (position - 1) {
                    case 0: {
                        if (flag == 0) {
                            mDrawerLayout.closeDrawers();
                            break;
                        }
                        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
                            tintManager.setStatusBarTintResource(R.color.my_bar_index_bar);
                        else
                            tintManager.setStatusBarTintResource(R.color.my_bar_index);
                        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_index));
                        fragmentTransaction.hide(fragment1).hide(fragment2).hide(fragment3).hide(fragment4).show(fragment0).commit();
                        mDrawerLayout.closeDrawers();
                        flag = 0;
                        break;
                    }
                    case 1: {
                        if (flag == 1) {
                            mDrawerLayout.closeDrawers();
                            break;
                        }
                        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        final SwitchButton btn = (SwitchButton) findViewById(R.id.netMeter);
                        if (btn.isChecked()) {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
                                tintManager.setStatusBarTintResource(R.color.my_bar_setting_bar);
                            else
                                tintManager.setStatusBarTintResource(R.color.my_bar_setting);
                            getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_setting));
                        } else {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
                                tintManager.setStatusBarTintResource(R.color.colorAccent_bar);
                            else
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
                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
                                        tintManager.setStatusBarTintResource(R.color.my_bar_setting_bar);
                                    else
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
                                } else {
                                    stopService(new Intent(getApplicationContext(), NetMeterService.class));
                                    editor.putString("NetMeter", "OFF");
                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
                                        tintManager.setStatusBarTintResource(R.color.colorAccent_bar);
                                    else
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
                        fragmentTransaction.hide(fragment0).hide(fragment2).hide(fragment3).hide(fragment4).show(fragment1).commit();
                        flag = 1;
                        break;
                    }
                    case 2: {
                        if (flag == 2) {
                            mDrawerLayout.closeDrawers();
                            break;
                        }
                        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
                            tintManager.setStatusBarTintResource(R.color.my_bar_usage_bar);
                        else
                            tintManager.setStatusBarTintResource(R.color.my_bar_usage);
                        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_usage));
                        mDrawerLayout.closeDrawers();
                        fragmentTransaction.hide(fragment0).hide(fragment1).hide(fragment3).hide(fragment4).show(fragment2).commit();
                        flag = 2;
                        break;
                    }
                    case 3: {
                        if (flag == 3) {
                            mDrawerLayout.closeDrawers();
                            break;
                        }
                        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
                            tintManager.setStatusBarTintResource(R.color.my_bar_globle_bar);
                        else
                            tintManager.setStatusBarTintResource(R.color.my_bar_globle);
                        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_globle));
                        mDrawerLayout.closeDrawers();
                        fragmentTransaction.hide(fragment0).hide(fragment1).hide(fragment2).hide(fragment4).show(fragment3).commit();
                        flag = 3;
                        break;
                    }
                    case 4: {
                        if (flag == 4) {
                            mDrawerLayout.closeDrawers();
                            break;
                        }
                        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
                            tintManager.setStatusBarTintResource(R.color.my_bar_aboout_bar);
                        else
                            tintManager.setStatusBarTintResource(R.color.my_bar_aboout);
                        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.my_bar_aboout));
                        mDrawerLayout.closeDrawers();
                        fragmentTransaction.hide(fragment0).hide(fragment1).hide(fragment2).hide(fragment3).show(fragment4).commit();
                        flag = 4;
                        break;
                    }
                }
                cur_pos = position - 1;
                adapter.notifyDataSetInvalidated();
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
        } else if (item.getItemId() == R.id.action_cleardata) {
            final DataUsageDB db = new DataUsageDB(this);
            final MaterialDialog clearData = new MaterialDialog(this);
            clearData.setTitle("警告！")
                    .setCanceledOnTouchOutside(true)
                    .setMessage("确定清除统计数据吗？")
                    .setNegativeButton("返回", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clearData.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            db.restore();
                            Toast.makeText(getApplicationContext(), "流量统计数据已重置！", Toast.LENGTH_SHORT).show();
                            clearData.dismiss();
                        }
                    });
            clearData.show();
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
        if (flag == 2) {
            return true;
        } else return false;
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

    public void transLucentStatus() {
        getActionBar().setTitle("Demo");
        setTranslucentStatus(true);
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            tintManager.setStatusBarTintResource(R.color.my_bar_index_bar);
        else
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click(); //调用双击退出函数
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            onDestroy();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return items_text.length;
        }

        @Override
        public Object getItem(int position) {
            return items_text[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.draweritemlayout, null, false);
            ImageView img = (ImageView) convertView
                    .findViewById(R.id.drawer_iv);
            TextView tv = (TextView) convertView
                    .findViewById(R.id.drawer_tv);
            tv.setText(items_text[position]);
            img.setImageResource(items_img[position]);
            if (position == cur_pos) {
                convertView.setBackgroundColor(0x669b9b9b);
                //tv.setTextColor(Color.RED);
            }
            return convertView;
        }
    }
}
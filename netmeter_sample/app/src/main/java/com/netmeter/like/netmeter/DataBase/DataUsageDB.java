package com.netmeter.like.netmeter.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

/**
 * Created by like on 15/7/5.
 */
public class DataUsageDB extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "DATAUSAGE.db";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "traffic";
    public final static String ID = "id";
    public final static String YEAR = "year";
    public final static String MONTH = "month";
    public final static String DAY = "day";
    public final static String WEEK = "week";
    public final static String TIME = "time";
    public final static String USAGE_TOTAL = "usage_total";
    public final static String USAGE_WIFI = "usage_wifi";
    public final static String UASGE_mobile = "usage_mobile";

    public DataUsageDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME +
                " (" +
                ID +
                " INTEGER primary key autoincrement, " +
                YEAR +
                " varchar(20), " +
                MONTH +
                " varchar(20), " +
                DAY +
                " varchar(20), " +
                WEEK +
                " varchar(20), " +
                TIME +
                " varchar(20), " +
                USAGE_TOTAL +
                " float, " +
                USAGE_WIFI +
                " float, " +
                UASGE_mobile +
                " float);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    public Cursor select() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query(TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }

    //增加操作
    public long insert(float total, float wifi, float mobile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USAGE_TOTAL, total);
        cv.put(USAGE_WIFI, wifi);
        cv.put(UASGE_mobile, mobile);
        //时间戳
        Calendar c = Calendar.getInstance();
        cv.put(YEAR, c.get(Calendar.YEAR)+"");
        cv.put(MONTH, (c.get(Calendar.MONTH)+1)+"");
        cv.put(DAY, c.get(Calendar.DAY_OF_MONTH)+"");
        cv.put(WEEK, c.get(Calendar.DAY_OF_WEEK)+"");
        cv.put(TIME, c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE));
        long row = db.insert(TABLE_NAME, null, cv);
        return row;
    }

    //删除操作
    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = ID + " = ?";
        String[] whereValue = {Integer.toString(id)};
        db.delete(TABLE_NAME, where, whereValue);
    }

    //修改操作
    public void update(int id, int total, int wifi, int mobile) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = ID + " = ?";
        String[] whereValue = {Integer.toString(id)};

        ContentValues cv = new ContentValues();
        cv.put(USAGE_TOTAL, total);
        cv.put(USAGE_WIFI, wifi);
        cv.put(UASGE_mobile, mobile);
        db.update(TABLE_NAME, cv, where, whereValue);
    }
}

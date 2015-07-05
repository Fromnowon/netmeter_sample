package com.netmeter.like.netmeter.Services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.TrafficStats;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.netmeter.like.netmeter.DataBase.DataUsageDB;

/**
 * Created by like on 15/7/5.
 */
public class DataUsageService extends Service {
    private DataUsageDB db;
    private Cursor mCursor;
    private float a,b,c;
    @Override
    public void onCreate() {
        super.onCreate();
        /*
        每一次启动服务，就往数据库写一次数据
         */
        db = new DataUsageDB(this);
        mCursor = db.select();

        a = (float) (((TrafficStats.getTotalRxBytes()) / 1000)/1000.0);
        b = (float) (((TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes()) / 1000)/1000.0);
        c = (float) (((TrafficStats.getMobileRxBytes()) / 1000)/1000.0);
        db.insert(a, b, c);
        mCursor.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new UsageBinder();
    }
    public class UsageBinder extends Binder {
        public DataUsageService getService() {
            // Return this instance of service so clients can call public methods
            return DataUsageService.this;
        }
    }

    public void fun(){

    }


    /*public void delete(){
        if (BOOK_ID == 0) {
            return;
        }
        mBooksDB.delete(BOOK_ID);
        mCursor.requery();
        BooksList.invalidateViews();
        BookName.setText("");
        BookAuthor.setText("");
        Toast.makeText(this, "Delete Successed!", Toast.LENGTH_SHORT).show();
    }

    public void update(){
        String bookname = BookName.getText().toString();
        String author = BookAuthor.getText().toString();

        if (bookname.equals("") || author.equals("")){
            return;
        }
        mBooksDB.update(BOOK_ID, bookname, author);
        mCursor.requery();
        BooksList.invalidateViews();
        BookName.setText("");
        BookAuthor.setText("");
        Toast.makeText(this, "Update Successed!", Toast.LENGTH_SHORT).show();
    }*/
}

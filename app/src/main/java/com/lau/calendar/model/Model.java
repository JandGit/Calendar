package com.lau.calendar.model;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lau.calendar.presenter.MainPresenter;

public class Model implements MainPresenter.CallbackOfModel{

    Context mContext;
    MyDatabaseHelper mDbHelper;

    public Model(Context mContext) {
        this.mContext = mContext;
        mDbHelper = new MyDatabaseHelper(mContext);
    }

    @Override
    public void saveUserAct(UserActBean data) {
        Log.i("app", "接受到写入数据库请求，开始写入");
        if(null == data){
            return;
        }
        String userName = data.getUserName();
        String title = data.getTitle();
        String time = data.getTime();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if(-1 == data.getId()){
            db.execSQL("insert into UserActivity values(null, ?, ?, ?)", new String[]{userName, title, time});
        }
        else{
            db.execSQL("update UserActivity set userName=?, actTitle=?, actTime=? where id=?", new String[]{
                userName, title, time, Integer.toString(data.getId())
            });
        }
        Log.i("app", "写入成功," + userName + "," + time + ", " + title);
    }

    @Override
    public UserActBean[] getUserAct(String time, String userName) {
        Log.i("app", "接受到读取数据库请求，开始读取");
        if(null == time || null == userName){
            return null;
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from UserActivity where userName=? and actTime=?", new String[]{
                userName, time
        });
        UserActBean[] data = null;
        if (cursor.getCount() != 0){
            data = new UserActBean[cursor.getCount()];
            UserActBean temp;
            int index = 0;
            while(cursor.moveToNext()){
                temp = new UserActBean();
                temp.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
                temp.setTitle(cursor.getString(cursor.getColumnIndex("actTitle")));
                temp.setTime(cursor.getString(cursor.getColumnIndex("actTime")));
                data[index++] = temp;
            }
        }
        cursor.close();
        Log.i("app", "读取成功");
        return data;
    }

    @Override
    public void closeDb() {
        if (mDbHelper != null){
            mDbHelper.close();
        }
    }
}

class MyDatabaseHelper extends SQLiteOpenHelper{

    public MyDatabaseHelper(Context context) {
        super(context, "calendar.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table UserActivity(id integer primary key autoincrement, userName varchar(20), " +
                "actTitle varchar(100), actTime varchar(40))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

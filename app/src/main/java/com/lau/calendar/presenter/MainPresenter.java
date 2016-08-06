package com.lau.calendar.presenter;


import android.content.Context;
import android.util.Log;

import com.lau.calendar.model.Model;
import com.lau.calendar.model.UserActBean;
import java.util.Calendar;

public class MainPresenter {

    public interface CallbackOfMainActivity{
        //标记选中指定日期,决定是否跳转
        void highlight(Calendar time, int drawableId, boolean locateIt);

        //将日程ListView的内容更新
        void updateSchedule(String[] titles, String[] time);

        void showToast(String text);
    }

    public interface CallbackOfModel{
        void saveUserAct(UserActBean data);
        UserActBean[] getUserAct(String time, String userName);
        void closeDb();
    }

    public MainPresenter(CallbackOfMainActivity mViewCallback, Context mContext) {
        this.mViewCallback = mViewCallback;
        this.mContext = mContext;
        this.mModelCallback = new Model(mContext);
    }

    //Context
    Context mContext;
    //View的接口
    CallbackOfMainActivity mViewCallback;
    //Model接口
    CallbackOfModel mModelCallback;

    //载入某一日期的日程，显示
    public void showAct(Calendar time){
        Log.i("presenter", "开始从数据库获取数据");
        UserActBean[] allActs = mModelCallback.getUserAct(UserActBean.getTimeString(time), "admin");
        if (allActs != null) {
            String[] titles = new String[allActs.length];
            String[] actTime = new String[allActs.length];
            int index = 0;
            for(UserActBean temp : allActs){
                titles[index] = temp.getTitle();
                actTime[index++] = temp.getTime();
            }
            mViewCallback.updateSchedule(titles, actTime);
            Log.i("presenter", "获取成功，更新日程");
        }
        else{
            mViewCallback.updateSchedule(null, null);
            Log.i("presenter", "无数据返回，更新日程");
        }
    }

    //保存
    public void saveAct(UserActBean act){
        Log.i("presenter", "保存用户数据");
        act.setUserName("admin");
        mModelCallback.saveUserAct(act);
        mViewCallback.showToast("添加成功");
        Log.i("presenter", "成功保存");
    }

    //程序退出时处理
    public void onDestory(){
        mModelCallback.closeDb();
    }
}

package com.lau.calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lau.calendar.model.UserActBean;
import com.lau.calendar.presenter.MainPresenter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements MainPresenter.CallbackOfMainActivity{

    //定义ActionBar菜单中的ItemID
    final static int ITEM_TO_TODAY = 0;
    final static int ITEM_ACT_MANAGE = 1;
    final static int ITEM_ACT_ADD = 2;
    final static int ITEM_SETTING = 3;
    //Activity中的控件
    ListView lv_showAct;
    ViewPager vp_calendar;
    ViewPagerAdapter mVpAdapter;
    //定义展示Toast
    Toast mToast;
    //Presenter
    MainPresenter mPresenter;

    //用于
    boolean isFirstCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置自定义ActionBar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setCustomView(R.layout.actionbar_main);
        actionBar.setDisplayShowCustomEnabled(true);

        mToast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
        initView();

        mPresenter = new MainPresenter(this, this);

        isFirstCreated = true;
    }

    //Activity内容加载完毕时执行的方法
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus && isFirstCreated){
            highlight(Calendar.getInstance(), R.drawable.blue, true);
        }
        isFirstCreated = false;
        super.onWindowFocusChanged(hasFocus);
    }

    public void showToast(String text){
        if(mToast != null){
            mToast.setText(text);
            mToast.show();
        }
    }

    //标记选中指定日期,决定是否跳转
    public void highlight(Calendar time, int drawableId, boolean locateIt){
        mVpAdapter.highlightDay(time, drawableId, locateIt);
    }

    //将日程ListView的内容更新
    public void updateSchedule(String[] titles, String[] time){
        if (titles != null && time != null) {
            ArrayList<HashMap<String, String>> data = new ArrayList<>(titles.length);
            HashMap<String, String> temp;
            for(int i = 0; i < titles.length; i++ ){
                temp = new HashMap<>(2);
                temp.put("title", titles[i]);
                temp.put("time", time[i]);
                data.add(temp);
            }
            SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.lv_act_item, new String[]{"title", "time"},
                        new int[]{R.id.tv_actTitle, R.id.tv_actTime});
            lv_showAct.setAdapter(adapter);
        }
        else {
            lv_showAct.setAdapter(null);
        }
    }

    //初始化界面
    private void initView(){
        lv_showAct = (ListView) findViewById(R.id.lv_showAct);
        vp_calendar = (ViewPager) findViewById(R.id.vp_calendar);
        mVpAdapter = new ViewPagerAdapter(vp_calendar);
        vp_calendar.setAdapter(mVpAdapter);
        vp_calendar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Calendar month = Calendar.getInstance();
                month.set(Calendar.MONTH, month.get(Calendar.MONTH) + (position - ViewPagerAdapter.firstPageIndex));
                String s = month.get(Calendar.YEAR) + "年" + (month.get(Calendar.MONTH) + 1) + "月";
                TextView tv = (TextView) findViewById(R.id.tv_month);
                assert tv != null;
                tv.setText(s);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        vp_calendar.setCurrentItem(ViewPagerAdapter.firstPageIndex, false);

        //设置标题按下事件
        View tv_month = findViewById(R.id.tv_month);
        assert tv_month != null;
        tv_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    //日历页面ViewPager的Adapter
    private class ViewPagerAdapter extends PagerAdapter{
        //设置第一页日历的位置position,即现在的月份页面
        final static int firstPageIndex = 2000;
        //
        ViewPager attachedVP;
        //
        HashMap<Integer, GridView> mMap = new HashMap<>();

        public ViewPagerAdapter(ViewPager attachedVP) {
            this.attachedVP = attachedVP;
        }

        //得到位置position处的页面GridView，如果存在，直接返回，若不存在
        //则先添加到集合中，待ViewPager调用添加
        public GridView getViewByPosition(int position){
            if(mMap.containsKey(position)){
                return mMap.get(position);
            }
            final Calendar selectedMonth = Calendar.getInstance();
            selectedMonth.set(Calendar.MONTH, selectedMonth.get(Calendar.MONTH) + (position - firstPageIndex));
            GridView gridView = new GridView(MainActivity.this);
            gridView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.MATCH_PARENT));
            gridView.setNumColumns(7);
            gridView.setGravity(Gravity.CENTER);
            gridView.setAdapter(new GridViewAdapter(gridView, selectedMonth));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                Calendar month = selectedMonth;
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Calendar temp = (Calendar) month.clone();
                    temp.set(Calendar.DAY_OF_MONTH, 1);
                    int lastMonth_days = temp.get(Calendar.DAY_OF_WEEK) - temp.getFirstDayOfWeek();
                    temp.set(Calendar.DAY_OF_MONTH, position - lastMonth_days + 1);
                    temp.getTimeInMillis();
                    ((GridViewAdapter)parent.getAdapter()).selectItem(position, R.drawable.blue);
                    Log.i("app", temp.toString());
                    mPresenter.showAct(temp);
                }
            });
            mMap.put(position, gridView);

            return gridView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
            mMap.remove(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GridView gridView;
            gridView = getViewByPosition(position);
            if(null != container){
                container.addView(gridView);
            }

            return gridView;
        }

        @Override
        public int getCount() {
            return 4000;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //标记选定的日期，可以选择是否定位到该日期
        public void highlightDay(final Calendar time, int drawableId, boolean locateIt){
            Calendar now = Calendar.getInstance();
            int position = (int)((time.getTimeInMillis() - now.getTimeInMillis()) / (1000 * 60 * 24 * 30));
            position += firstPageIndex;
            if(0 > position || position > 4000){
                return ;
            }
            GridView gv = getViewByPosition(position);
            if(gv != null){
                Calendar temp = (Calendar) time.clone();
                temp.set(Calendar.DAY_OF_MONTH, 1);
                int lastMonth_days = temp.get(Calendar.DAY_OF_WEEK) - temp.getFirstDayOfWeek();
                int i = lastMonth_days + time.get(Calendar.DAY_OF_MONTH) - 1;
                ((GridViewAdapter)gv.getAdapter()).selectItem(i, drawableId);
            }
            if (locateIt) {
                attachedVP.setCurrentItem(position, false);
                mPresenter.showAct(time);
            }
        }
    }

    //日历GridView的Adapter
    private class GridViewAdapter extends BaseAdapter {
        //关键GridView的引用及所有展示的原始数据，展示的月份
        final GridView attachedGV;
        final String[] daysToShow;
        final Calendar month;
        //所有的View数据
        View[] allView;
        //用于标记选中的Item
        View lastSelectedItem;
        Drawable lastSelectedItemBackground;

        public GridViewAdapter(final GridView gv, final Calendar month) {
            this.attachedGV = gv;
            this.month = month;
            lastSelectedItem = null;

            //得到本页面要显示的上个月的天数,下个月的天数，本月的天数
            Calendar lastMonth = (Calendar) month.clone();
            lastMonth.set(Calendar.MONTH, lastMonth.get(Calendar.MONTH) - 1);
            Calendar nextMonth = (Calendar) month.clone();
            nextMonth.set(Calendar.MONTH, nextMonth.get(Calendar.MONTH) + 1);
            Calendar temp = (Calendar) month.clone();
            temp.set(Calendar.DAY_OF_MONTH, 1);
            int lastMonth_days = temp.get(Calendar.DAY_OF_WEEK) - month.getFirstDayOfWeek();
            temp.set(Calendar.DAY_OF_MONTH, month.getActualMaximum(Calendar.DAY_OF_MONTH));
            int nextMonth_days = month.getActualMaximum(Calendar.DAY_OF_WEEK) - temp.get(Calendar.DAY_OF_WEEK);
            int thisMonth_days = month.getActualMaximum(Calendar.DAY_OF_MONTH);
            //设置日历显示的数字数组，上个月负号加数字表示，本月用正数，下月用正号加数字
            daysToShow = new String[lastMonth_days + thisMonth_days + nextMonth_days];
            int index = 0;
            for(int i = 0, num = lastMonth.getActualMaximum(Calendar.DAY_OF_MONTH) - lastMonth_days + 1; i < lastMonth_days; i++, num++){
                daysToShow[index++] = "-" + Integer.toString(num);
            }
            for(int i = 0; i < thisMonth_days; i++){
                daysToShow[index++] = Integer.toString(i + 1);
            }
            for(int i = 0; i < nextMonth_days; i++){
                daysToShow[index++] = "+" + Integer.toString(i + 1);
            }

            //初始化View集合
            allView = new View[daysToShow.length];
            for(int i = 0; i < allView.length; i++){
                allView[i] = null;
            }
        }

        @Override
        public int getCount() {
            return allView.length;
        }

        @Override
        public Object getItem(int position) {
            return allView[position];
        }

        @Override
        public long getItemId(int position) {
            if (null != allView[position]) {
                return allView[position].getId();
            }
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(allView[position] != null){
                convertView = allView[position];

                return convertView;
            }

            if(null == convertView){
                convertView = LayoutInflater.from(MainActivity.this).inflate
                        (R.layout.gridview_item, parent, false);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.tv_number);
            if('-' == daysToShow[position].charAt(0) || '+' == daysToShow[position].charAt(0)){
                String s = daysToShow[position];
                tv.setText(s.subSequence(1, s.length()));
                tv.setTextColor(Color.GRAY);
            }
            else{
                tv.setText(daysToShow[position]);
            }
            allView[position] = convertView;

            return convertView;
        }

        //选中Item并高亮标记,同时载入日程数据
        public void selectItem(int position, int drawableId){
            //高亮标记
            View view = allView[position];
            if (view != null) {
                if(lastSelectedItem != null){
                    lastSelectedItem.setBackground(lastSelectedItemBackground);
                }
                lastSelectedItemBackground = view.getBackground();
                lastSelectedItem = view;
                view.setBackgroundResource(drawableId);
            }
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, ITEM_ACT_MANAGE, 2, "日程管理").setIcon(R.mipmap.ic_launcher).
                setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(1, ITEM_ACT_ADD, 3, "添加日程").setIcon(R.mipmap.ic_launcher).
                setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(1, ITEM_SETTING, 4, "设置").setIcon(R.mipmap.ic_launcher).
                setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        //使用反射使溢出菜单中的隐藏图标显示
        try {
            Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
            method.setAccessible(true);
            method.invoke(menu, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case ITEM_ACT_MANAGE://日程管理选项
                mToast.setText("日程管理");
                mToast.show();
                break;
            case ITEM_ACT_ADD://添加日程选项
                startActivityForResult(new Intent(this, AddActActivity.class), 1);
                break;
            case ITEM_SETTING://设置选项
                mToast.setText("设置");
                mToast.show();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(RESULT_OK == resultCode){
            if(1 == requestCode){
                UserActBean act = (UserActBean) data.getSerializableExtra("data");
                if (act != null){
                    mPresenter.saveAct(act);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestory();
        super.onDestroy();
    }
}

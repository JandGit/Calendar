package com.lau.calendar.model;

import java.io.Serializable;
import java.util.Calendar;

public class UserActBean implements Serializable{

    private int id = -1;
    private String userName;
    private String title;
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserActBean(String time, String userName, String title) {
        this.time = time;
        this.userName = userName;
        this.title = title;
    }
    public UserActBean(){
        super();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static String getTimeString(Calendar time){
        return time.get(Calendar.YEAR) + "-" + (time.get(Calendar.MONTH) + 1) + "-" +
                time.get(Calendar.DAY_OF_MONTH);
    }
}

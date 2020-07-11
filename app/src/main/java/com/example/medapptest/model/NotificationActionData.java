package com.example.medapptest.model;

import android.app.PendingIntent;

public class NotificationActionData {
    private String name;
    private PendingIntent pendingIntent;
    private int icon;

    public NotificationActionData(String name, PendingIntent pendingIntent, int drawable){
        this.name = name;
        this.pendingIntent = pendingIntent;
        this.icon = drawable;
    }

    public int getIcon(){
        return icon;
    }
    public String getName(){
        return name;
    }
    public  PendingIntent getPendingIntent(){
        return pendingIntent;
    }
}

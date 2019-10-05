package com.stel.app.local.util;

import android.util.Log;

public class LogUtil
{
    boolean enableLogs=true;
    public static void Log(String message)
    {
        Log.e("LogUtil",message);
    }

    public static void Log(String title,String message)
    {
        Log.e(title,message);
    }
}

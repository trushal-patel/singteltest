package com.stel.app.local.util;

import android.util.Log;

public class LogUtil
{
    private final static boolean  enableLogs=true;

    public static void Log(String title,String message)
    {
        if(enableLogs)
        Log.e(title,message);
    }
}

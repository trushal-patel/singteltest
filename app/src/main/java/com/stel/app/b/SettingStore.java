package com.stel.app.b;

import android.content.Context;
import android.content.SharedPreferences;

class SettingStore
{
    private static SettingStore ourInstance;
    private SharedPreferences pref;

    static SettingStore getInstance(Context context)
    {
        if(ourInstance==null)
        {
            ourInstance=new SettingStore(context);
        }
        return ourInstance;
    }

    private SettingStore(Context c)
    {
        if(c==null)
            return;

        pref= c.getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    int getCallCounter()
    {
        final String PREF_COUNTER = "pref_counter";
        int count= pref.getInt(PREF_COUNTER,1);
        //Let's Auto increment count by 1.
        pref.edit().putInt(PREF_COUNTER,count+1).apply();
        return count;
    }

}

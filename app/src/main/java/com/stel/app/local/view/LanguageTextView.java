package com.stel.app.local.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.AttributeSet;
import android.widget.TextView;


import com.stel.app.local.util.LanguageUtility;
import com.stel.app.local.util.LogUtil;

import java.util.Locale;

@SuppressLint("AppCompatCustomView")
public class LanguageTextView extends TextView
{

    public static String ACTION_TRANSLATE="com.app.ACTION_TRANSLATE";
    public static String ACTION_TRANSLATE_RES="com.app.ACTION_TRANSLATE_RES";


    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(ACTION_TRANSLATE_RES.equals(intent.getAction()))
            { try {
                        int stringRes =  getId();
                        String newText = getDefaultString(context, stringRes, LanguageUtility.getInstance().getCurrentTranslation());
                        setText(newText);
                }catch(Exception ignored){}


            } else {
                setText(LanguageUtility.getInstance().getTranslation(getText().toString()));
            }
            LogUtil.Log("LanguageTextView","onReceive"+intent.getAction());
        }
    };


    public static String getDefaultString(Context context,  int stringId,String language){
        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());
        Locale defaultLocale = new Locale(language);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(defaultLocale);
            configuration.setLocales(localeList);
            return context.createConfigurationContext(configuration).getString(stringId);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            configuration.setLocale(defaultLocale);
            return context.createConfigurationContext(configuration).getString(stringId);
        }
        return context.getString(stringId);
    }


    Context context;
    public LanguageTextView(Context context) {
        super(context);
        this.context=context;
    }

    public LanguageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    public LanguageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }


    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        LogUtil.Log("LanguageTextView","onDetachedFromWindow");
        context.unregisterReceiver(receiver);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogUtil.Log("LanguageTextView","onAttachedToWindow");

        IntentFilter filter = new IntentFilter();
        filter.addAction(LanguageTextView.ACTION_TRANSLATE);
        filter.addAction(LanguageTextView.ACTION_TRANSLATE_RES);
        context.registerReceiver(receiver,filter);
    }
}

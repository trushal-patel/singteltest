package com.stel.app.local.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.widget.TextView;

import com.stel.app.local.util.LanguageUtility;
import com.stel.app.local.util.LogUtil;

@SuppressLint("AppCompatCustomView")
public class LanguageTextView extends TextView
{

    public static String ACTION_TRANSLATE="com.app.ACTION_TRANSLATE";

    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            setText(LanguageUtility.getInstance().getTranslation(getText().toString()));
            LogUtil.Log("LanguageTextView","onReceive"+intent.getAction());
        }
    };

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
        context.registerReceiver(receiver,filter);
    }
}

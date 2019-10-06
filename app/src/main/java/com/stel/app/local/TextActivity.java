package com.stel.app.local;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.widget.Toolbar;

import com.stel.app.local.util.LanguageUtility;

public class TextActivity extends LocalBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState==null)
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    if(!isFinishing() && !isDestroyed() && MainActivity.localInstance==null) {
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                    }
                }
            }, 2000);
        }
    }

    @Override
    protected void onDestroy()
    {
        LanguageUtility.getInstance().destroy();
        super.onDestroy();
    }
}

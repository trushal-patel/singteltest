package com.stel.app.local;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

public class TextActivity extends LocalBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState==null || !savedInstanceState.containsKey("isRecreate"))
        {
            Toast.makeText(this, "This screen will stay in background. Opening main activity in 5 seconds",Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    if(!isFinishing() && !isDestroyed()) {
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                    }
                }
            }, 5000);
        }
    }
}

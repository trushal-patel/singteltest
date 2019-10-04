package com.stel.app.local;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

public class MainActivity extends LocalBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}

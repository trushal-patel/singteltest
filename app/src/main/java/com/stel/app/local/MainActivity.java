package com.stel.app.local;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.stel.app.local.util.FileDownloadListner;
import com.stel.app.local.util.LanguageUtility;

public class  MainActivity extends LocalBaseActivity {

    public static Object localInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localInstance=this;
        setContentView(R.layout.activity_main);
        if(!(LanguageUtility.getInstance().isFileDownloaded(this)))
        {
            attemptFileDownload();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        new MenuInflater(this).inflate(R.menu.main_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.menu_download) {
            attemptFileDownload();
        }
        return super.onOptionsItemSelected(item);
    }

    public void attemptFileDownload()
    {
        showProgressDialog();
        LanguageUtility.getInstance().downloadFile(this,new FileDownloadListner() {
            @Override
            public void downloadSuccessful()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        Toast.makeText(MainActivity.this,"File downloaded successful",Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void downloadFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        Toast.makeText(MainActivity.this,"Download failed",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        localInstance=null;
    }
}

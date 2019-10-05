package com.stel.app.local;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.stel.app.local.util.FileDownloadListner;
import com.stel.app.local.util.LanguageUtility;

public class MainActivity extends LocalBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        switch (item.getItemId())
        {
            case R.id.menu_download:
                attemptFileDownload();
                break;
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

    ProgressDialog mProgressDialog;
    void showProgressDialog()
    {
        if(mProgressDialog!=null) {
            // May be previous progress-bar still active
            hideProgressDialog();
        }
        mProgressDialog = ProgressDialog.show(MainActivity.this, "", "Downloading File...");
    }

    void hideProgressDialog()
    {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }catch(Exception e){
        }
    }



}

package com.stel.app.local;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

public class MainActivity extends LocalBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!(isFileDownloaded()))
        {
            attemptFileDownload();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void attemptFileDownload()
    {
        showProgressDialog();
        downloadFile(new FileDownloadListner() {
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

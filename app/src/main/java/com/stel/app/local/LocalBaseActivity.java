package com.stel.app.local;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stel.app.local.thirdparty.CSVFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("Registered")
public class LocalBaseActivity extends AppCompatActivity {

    final String localfileName = "stel_translation.csv";
    String currentTranslation = "en";
    int currentTranslationPos = 0;


    List csvArray;
    public void parseFile() throws FileNotFoundException {
        if(csvArray==null)
        {
            CSVFile file = new CSVFile(new File(getFilesDir(), localfileName));
            csvArray = file.read();
        }
    }

    HashMap<String,String> languageMap;
    public void traverseChild(View root, Context c,String targetLanguage,boolean isCalledFromRootedView) {
        int newLangPos = -1;

        try
        {
            if(!isCalledFromRootedView)
            {
                parseFile();

                if (csvArray.size() > 0)
                {
                    String[] titleRow = (String[]) csvArray.get(0);
                    int pos = 0;
                    for (String langCode : titleRow)
                    {
                        if (targetLanguage.equals(langCode))
                            newLangPos = pos;
                        pos++;
                    }
                }

                if(newLangPos<=0)
                {
                    return;
                } else
                {

                        languageMap = new HashMap<>();
                        String[] languageRow;
                        for (int i = 1; i < csvArray.size(); i++) //skip title
                        {
                            languageRow = (String[]) csvArray.get(i);
                            languageMap.put(languageRow[currentTranslationPos], languageRow[newLangPos]);
                        }

                }

                ViewParent vp;
                while (root.getParent() != null && root.getParent() instanceof  View) {
                    //let's find topmost root element
                    root = (View) root.getParent();
                }
            }


            View childView;
            TextView tv;
            if (root instanceof ViewGroup)
            {
                ViewGroup relative = (ViewGroup) root;
                for (int i = 0; i < relative.getChildCount(); i++) {
                    childView = relative.getChildAt(i);
                    if (childView instanceof ViewGroup)
                        traverseChild(childView, c,targetLanguage,true);
                    else if (childView instanceof TextView)
                    {
                        tv = (TextView) childView;
                        String enText=tv.getText().toString();
                        if(languageMap!=null && languageMap.containsKey(enText))
                        {
                            tv.setText(languageMap.get(enText));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        currentTranslationPos=newLangPos;
        currentTranslation=targetLanguage;
    }


    boolean isFileDownloaded() {
        return new File(getFilesDir(), localfileName).exists();
    }

    public boolean isConnected() {
        try {
            final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            assert cm != null;
            final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
//// Lets try the server call for this case.
            return true;
        }
    }


    protected void downloadFile(final FileDownloadListner listner) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getString(R.string.csv_url))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
                if (listner != null)
                    listner.downloadFailed();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //boolean isSuccess=false;
                        try {
                            if (response.isSuccessful()) {
                                InputStream in = response.body().byteStream();
                                File file = new File(getFilesDir(), localfileName + ".temp");
                                boolean results;
                                if (file.exists()) {
                                    results = file.delete();
                                }
                                results = file.createNewFile();
                                FileOutputStream fos = new FileOutputStream(file);
                                BufferedInputStream bis = new BufferedInputStream(in);
                                byte[] buff = new byte[1024];
                                int len = 0;
                                while ((len = bis.read(buff)) > 0) {
                                    fos.write(buff, 0, len);
                                }
                                bis.close();
                                fos.close();
                                results = file.renameTo(new File(getFilesDir(), localfileName));

                                if (listner != null && results)
                                    listner.downloadSuccessful();
                            } else {

                                if (listner != null)
                                    listner.downloadFailed();
                            }
                        } catch (Exception e) {

                            if (listner != null)
                                listner.downloadFailed();
                        }
                    }
                }).start();
            }
        });
        /// TODO handle all cases of server error responses
    }

    public void doTranslate(View v) {
        if (v != null && v.getTag() != null) {
            String tag = v.getTag().toString();
            if (!tag.equals(currentTranslation))
            {
                traverseChild(v,this,tag,false);
//              Toast.makeText(this, "Translate " + tag, Toast.LENGTH_SHORT).show();
                //Do translation
            } else {
                Toast.makeText(this, "Already Translated", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

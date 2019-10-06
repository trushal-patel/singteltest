package com.stel.app.local.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import com.stel.app.local.R;
import com.stel.app.local.view.LanguageTextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LanguageUtility
{
    private final static String localfileName = "stel_translation.csv";

    public String getCurrentTranslation() {
        return currentTranslation;
    }

    private String currentTranslation = "en";
    private int currentTranslationPos = 0;
    private HashMap<String,String> languageMap;
    private static LanguageUtility utility;
    public static LanguageUtility getInstance()
    {
        if(utility==null)
            utility=new LanguageUtility();
        return utility;
    }

    public boolean isLanguageSame(String newLanguageCode)
    {
        return  currentTranslation.equals(newLanguageCode);
    }

    private boolean isConnected(Context context) {
        try {
            final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private List csvArray;
    private void parseFile(Context context, boolean fourceParse) throws FileNotFoundException
    {
        if(csvArray==null || fourceParse)
        {
            CSVFile file = new CSVFile(new File(context.getFilesDir(), localfileName));
            csvArray = file.read();
        }
    }

    public void downloadFile(final Context context,final FileDownloadListner listner) {

        if(!isConnected(context))
        {
            // TODO , For production app we can have apecific error message for user.
            if (listner != null)
                listner.downloadFailed();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(context.getString(R.string.csv_url))
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
                                File file = new File(context.getFilesDir(), localfileName + ".temp");
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
                                results = file.renameTo(new File(context.getFilesDir(), localfileName));

                                if (listner != null && results)
                                {
                                    parseFile(context,true);
                                    listner.downloadSuccessful();
                                }
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

    private void setLocale(final Activity context, final String lang, final int pos)
    {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
        new AlertDialog.Builder(context).setMessage(R.string.mesaage_app_restart)
                .setCancelable(true)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        applyLocal(context,lang,pos);
//                        Intent lIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
//                        lIntent.putExtra("language",lang);
//                        lIntent.putExtra("language_pos",pos);
//                        context.startActivity(lIntent);
//                        context.sendBroadcast(new Intent(LocalBaseActivity.ACTION_RECREATE));
                    }
                }).setNegativeButton(R.string.btn_cancel,null).create().show();
//            }
//        },500);
    }

    public void applyLocal(Context context,String lang,int pos)
    {
        context=context.getApplicationContext();
        Locale locale = new Locale (lang);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context.createConfigurationContext(configuration);
        } else {
            context.getResources().updateConfiguration(configuration,
                    context.getResources().getDisplayMetrics());
        }
        currentTranslation=lang;
        currentTranslationPos=pos;

    }

    public void doTranslate(Activity activity,String newLanguageCode)
    {
        if(currentTranslationPos==-1)
        {
            //Reset to english first.
            currentTranslation="end";
            currentTranslationPos=0;
            activity.sendBroadcast(new Intent(LanguageTextView.ACTION_TRANSLATE_RES));
//            return;
        }

        if(isFileDownloaded(activity))
        {
            try {
                parseFile(activity,false);

            int newLanguagePosition=-1;
            if(csvArray!=null && csvArray.size()>1) // atleast 2 rows needed, 1 for Title, 1 for translation
            {
                String[] titleRow = (String[]) csvArray.get(0);
                int pos = 0;
                for (String langCode : titleRow)
                {
                    if (newLanguageCode.equals(langCode))
                        newLanguagePosition = pos;
                    pos++;
                }
            }
            if(newLanguagePosition!=-1)
            {
                languageMap = new HashMap<>();
                String[] languageRow;
                for (int i = 1; i < csvArray.size(); i++) //skip title
                {
                    languageRow = (String[]) csvArray.get(i);
                    languageMap.put(languageRow[currentTranslationPos], languageRow[newLanguagePosition]);
                }
                currentTranslationPos=newLanguagePosition;
                currentTranslation=newLanguageCode;
                Toast.makeText(activity, "Applying remote localization", Toast.LENGTH_SHORT).show();
                activity.sendBroadcast(new Intent(LanguageTextView.ACTION_TRANSLATE));
                return;
            } else
            {
                /// Ignore it, it will go to default fallback.
            }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(activity, "Applying local resource localization", Toast.LENGTH_SHORT).show();


        /// Fallback Conditions

//            Validation Not working perperly
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
//            {
//                LocaleList localeList = activity.getResources().getConfiguration().getLocales();
//                boolean isLanguageExist=false;
//                if(localeList.size()>0)
//                {
//                    for(int i=0;i<localeList.size();i++)
//                    {
//                        if(newLanguageCode.equals(localeList.get(i).getLanguage()))
//                        {
//                            isLanguageExist=true;
//                        }
//                    }
//                }
//                if(isLanguageExist)
//                {
//                    setLocale(activity,newLanguageCode,-1);
//                }
//                else
//                {
//                    Toast.makeText(activity,"Languge Not available",Toast.LENGTH_SHORT).show();
//                }
//            } else
            {
                // Assuming app will have all locale language resources set.
                currentTranslation=newLanguageCode;
                currentTranslationPos=-1;
                activity.sendBroadcast(new Intent(LanguageTextView.ACTION_TRANSLATE_RES));
//                setLocale(activity,newLanguageCode,-1);
            }
    }

    public String getTranslation(String plainText)
    {
        if(languageMap!=null && languageMap.containsKey(plainText))
        {
            return languageMap.get(plainText);
        }
        //No match...
        return plainText;
    }


    public boolean isFileDownloaded(Context context)
    {
        return new File(context.getFilesDir(), localfileName).exists();
    }

    public void destroy() {

        currentTranslation=null;
        currentTranslationPos=0;

        if(languageMap!=null)
        {
            languageMap.clear();
            languageMap = null;
        }

        if(csvArray!=null) {
            csvArray.clear();
            csvArray = null;
        }
        utility=null;
    }
}

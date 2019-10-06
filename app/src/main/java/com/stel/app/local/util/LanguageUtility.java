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

    private final static String localFileName = "stel_translation.csv";

    private String currentTranslation = "en";
    private int currentTranslationPos = 0;


    private HashMap<String, String> languageMap;

    private static LanguageUtility utility;
    public static LanguageUtility getInstance() {
        if (utility == null)
            utility = new LanguageUtility();
        return utility;
    }

    /**
     * Get current translation lancuage of app
     * @return language code
     */
    public String getCurrentTranslation() {
        return currentTranslation;
    }

    /**
     * Use this method to Check if new & previous languages are same.
     *
     * @param newLanguageCode
     * @return true if both languages are same. false otherwise
     */
    public boolean isLanguageSame(String newLanguageCode) {
        return currentTranslation.equals(newLanguageCode);
    }

    private boolean isNetworkConnected(Context context) {
        try {
            final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert cm != null;
            final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        } catch (Exception e) {
            //// Lets try the server call for this case.
            return true;
        }
    }

    /**
     *  Lets save it. So don't reuire to parse file everytime. For production apps we can use database instead.
     */
    private List csvArray;

    /**
     *
     * Parse downloaded CSV file
     * @param context
     * @param fourceParse
     * @throws FileNotFoundException
     */
    private void parseFile(Context context, boolean fourceParse) throws FileNotFoundException {
        if (csvArray == null || fourceParse) {
            CSVFile file = new CSVFile(new File(context.getFilesDir(), localFileName));
            csvArray = file.read();
        }
    }

    /**
     * Download CSV File from server.
     * @param context
     * @param listner
     */
    public void downloadFile(final Context context, final FileDownloadListner listner) {

        if (!isNetworkConnected(context)) {
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
            public void onResponse(Call call, final Response response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //boolean isSuccess=false;
                        try {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                InputStream in = response.body().byteStream();
                                File file = new File(context.getFilesDir(), localFileName + ".temp");
                                if (file.exists()) {
                                    file.delete();
                                }
                                file.createNewFile();
                                FileOutputStream fos = new FileOutputStream(file);
                                BufferedInputStream bis = new BufferedInputStream(in);
                                byte[] buff = new byte[1024];
                                int len;
                                while ((len = bis.read(buff)) > 0) {
                                    fos.write(buff, 0, len);
                                }
                                bis.close();
                                fos.close();
                                file.renameTo(new File(context.getFilesDir(), localFileName));

                                if (listner != null) {
                                    parseFile(context, true);
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

    /**
     * Set local languge from source.
     *
     * @param context
     * @param lang  languge code
     * @param pos selected languge position. always -1 for suchcases.
     */
    private void setLocale(final Activity context, final String lang, final int pos) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
        new AlertDialog.Builder(context).setMessage(R.string.mesaage_app_restart)
                .setCancelable(true)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Locale locale = new Locale(lang);
                        Resources resources = context.getResources();
                        Configuration configuration = resources.getConfiguration();
                        configuration.locale = locale;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            context.createConfigurationContext(configuration);
                        } else {
                            context.getResources().updateConfiguration(configuration,
                                    context.getResources().getDisplayMetrics());
                        }
                        currentTranslation = lang;
                        currentTranslationPos = pos;
                        //Restart Activity.
                    }
                }).setNegativeButton(R.string.btn_cancel, null).create().show();
//            }
//        },500);
    }


    /**
     *  USe this function to apply translation.
     *
     * @param activity
     * @param newLanguageCode
     */
    public void doTranslate(Activity activity, String newLanguageCode) {
        if (currentTranslationPos == -1) {
            //Reset to english first.
            currentTranslation = "end";
            currentTranslationPos = 0;
            activity.sendBroadcast(new Intent(LanguageTextView.ACTION_TRANSLATE_RES));
//            return;
        }

        if (isFileDownloaded(activity)) {
            try {
                parseFile(activity, false);

                int newLanguagePosition = -1;
                if (csvArray != null && csvArray.size() > 1) // atleast 2 rows needed, 1 for Title, 1 for translation
                {
                    String[] titleRow = (String[]) csvArray.get(0);
                    int pos = 0;
                    for (String langCode : titleRow) {
                        if (newLanguageCode.equals(langCode))
                            newLanguagePosition = pos;
                        pos++;
                    }
                }
                if (newLanguagePosition != -1) {
                    languageMap = new HashMap<>();
                    String[] languageRow;
                    for (int i = 1; i < csvArray.size(); i++) //skip title
                    {
                        languageRow = (String[]) csvArray.get(i);
                        languageMap.put(languageRow[currentTranslationPos], languageRow[newLanguagePosition]);
                    }
                    currentTranslationPos = newLanguagePosition;
                    currentTranslation = newLanguageCode;
                    Toast.makeText(activity,activity.getString(R.string.message_doc_localization), Toast.LENGTH_SHORT).show();
                    activity.sendBroadcast(new Intent(LanguageTextView.ACTION_TRANSLATE));
                    return;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(activity, R.string.message_resource_localization, Toast.LENGTH_SHORT).show();


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
            currentTranslation = newLanguageCode;
            currentTranslationPos = -1;
            activity.sendBroadcast(new Intent(LanguageTextView.ACTION_TRANSLATE_RES));
//                setLocale(activity,newLanguageCode,-1);
        }
    }

    /**
     * Get translation from downloaded file.
     * @param plainText
     * @return
     */
    public String getTranslation(String plainText) {
        if (languageMap != null && languageMap.containsKey(plainText)) {
            return languageMap.get(plainText);
        }
        //No match...
        return plainText;
    }


    /**
     * Check if remote CSV file is downloaded.
     * @param context
     * @return
     */
    public boolean isFileDownloaded(Context context) {
        return new File(context.getFilesDir(), localFileName).exists();
    }

    /**
     * Clear singleton instance.
     *
     */
    public void destroy() {

        currentTranslation = null;
        currentTranslationPos = 0;

        if (languageMap != null) {
            languageMap.clear();
            languageMap = null;
        }

        if (csvArray != null) {
            csvArray.clear();
            csvArray = null;
        }
        utility = null;
    }
}

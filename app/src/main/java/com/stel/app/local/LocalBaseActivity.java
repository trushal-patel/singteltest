package com.stel.app.local;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.stel.app.local.util.LanguageUtility;

@SuppressLint("Registered")
public class LocalBaseActivity extends AppCompatActivity
{
//    public static final String ACTION_RECREATE="com.stel.app.ACTION_RECREATE";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ACTION_RECREATE);
//        registerReceiver(receiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(receiver);
    }

//    BroadcastReceiver receiver=new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            ///Local has been changed. Recreate activity to apply it
////            Bundle b=new Bundle();
////            b.putString("isRecreate","1");
////            onSaveInstanceState(b);
////            finish();
////            recreate();
//        }
//    };

    //    public void traverseChild(View root, Context c,String targetLanguage,boolean isCalledFromRootedView)
//    {
//        int newLangPos = -1;
//        try
//        {s
//            if(!isCalledFromRootedView)
//            {
//                parseFile();
//                if (csvArray.size() > 0)
//                {
//                    String[] titleRow = (String[]) csvArray.get(0);
//                    int pos = 0;
//                    for (String langCode : titleRow)
//                    {
//                        if (targetLanguage.equals(langCode))
//                            newLangPos = pos;
//                        pos++;
//                    }
//                }
//
//                if(newLangPos<0)
//                {
//                    setLocale(targetLanguage);
//                    return;
//                } else
//                {
//                        languageMap = new HashMap<>();
//                        String[] languageRow;
//                        for (int i = 1; i < csvArray.size(); i++) //skip title
//                        {
//                            languageRow = (String[]) csvArray.get(i);
//                            languageMap.put(languageRow[currentTranslationPos], languageRow[newLangPos]);
//                        }
//
//                }
//                ViewParent vp;
//                while (root.getParent() != null && root.getParent() instanceof  View) {
//                    //let's find topmost root element
//                    root = (View) root.getParent();
//                }
//            }
//
//
//            View childView;
//            TextView tv;
//            if (root instanceof ViewGroup)
//            {
//                ViewGroup relative = (ViewGroup) root;
//                for (int i = 0; i < relative.getChildCount(); i++) {
//                    childView = relative.getChildAt(i);
//                    if (childView instanceof ViewGroup)
//                        traverseChild(childView, c,targetLanguage,true);
//                    else if (childView instanceof TextView)
//                    {
//                        tv = (TextView) childView;
//                        String enText=tv.getText().toString();
//                        if(languageMap!=null && languageMap.containsKey(enText))
//                        {
//                            tv.setText(languageMap.get(enText));
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        currentTranslationPos=newLangPos;
//        //setLocale(targetLanguage);
//        currentTranslation=targetLanguage;
//    }


    public void doTranslate(View v)
    {
        LanguageUtility util=LanguageUtility.getInstance();
        if (v != null && v.getTag() != null)
        {
            String tag = v.getTag().toString();
            if (!util.isLanguageSame(tag))
            {
                LanguageUtility.getInstance().doTranslate(this,tag);
                //traverseChild(v,this,tag,false);
            } else {
                Toast.makeText(this, "Already Translated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    ProgressDialog mProgressDialog;
    void showProgressDialog()
    {
        // May be previous progress-bar still active
        hideProgressDialog();
        mProgressDialog = ProgressDialog.show(this, "", getString(R.string.message_progress_downloding_file));
    }

    void hideProgressDialog()
    {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }catch(Exception ignored){
        }
    }
}

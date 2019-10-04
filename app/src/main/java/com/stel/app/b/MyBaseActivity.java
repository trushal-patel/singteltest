package com.stel.app.b;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MyBaseActivity extends Activity
{

    public final static String ACTION_GET_RESULTS="com.app.stel.action_PROCESS_TEXT";
    public final static String ACTION_MATH_OPERATION="com.app.stel.action_PROCESS_MATH";

    public final static String EXTRA_PARAM_INPUT1="input1";

    ProgressDialog mProgressDialog;
    void showProgressDialog(String message)
    {
        if(mProgressDialog==null) {
            // May be previous progress-bar still active
            hideProgressDialog();
        }
        mProgressDialog = ProgressDialog.show(MyBaseActivity.this, "", message);
    }

    void hideProgressDialog()
    {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }catch(Exception e){
        }
    }

    void returnResults(String output)
    {
        Intent intent=new Intent();
        intent.putExtra(EXTRA_PARAM_INPUT1,output);
        setResult(RESULT_OK,intent);
    }

    public boolean isConnected() {
        try {
            final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null &&networkInfo.isAvailable() && networkInfo.isConnected())
            {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
//// Lets try the server call for this case.
            return true;
        }
    }

}

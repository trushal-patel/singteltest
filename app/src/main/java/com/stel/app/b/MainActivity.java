package com.stel.app.b;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity {

    public final static String ACTION_GET_RESULTS="com.app.stel.action_PROCESS_TEXT";
    public final static String ACTION_MATH_OPERATION="com.app.stel.action_PROCESS_MATH";

    public final static String EXTRA_PARAM_INPUT1="input1";
    public final static String EXTRA_PARAM_INPUT2="input2";//for future use

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getAction().equals(ACTION_GET_RESULTS))
        {
            ProcessTextHandler serverCallHandler = new ProcessTextHandler();
            serverCallHandler.execute(getIntent().getStringExtra(EXTRA_PARAM_INPUT1));
            //finish();
        } if(getIntent().getAction().equals(ACTION_MATH_OPERATION))
        {
            ProcessMATHHandler serverCallHandler = new ProcessMATHHandler();
            serverCallHandler.execute(getIntent().getStringExtra(EXTRA_PARAM_INPUT1));
            //finish();
        } else
        {
            ///Invalid action detected.
            Toast.makeText(this, "App is installed & ready to use", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public class ProcessTextHandler extends AsyncTask<String,Void,String>
    {

        @Override
        protected void onPreExecute() {
            showProgressDialog("Processing Text....");
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String[] input)
        {
            String parameter=getString(R.string.parameter_text_process);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(parameter, input[0])
                    .build();

            Request request = new Request.Builder().url(getString(R.string.server_url_text_process))
                    .post(requestBody).build();
            try {
                OkHttpClient client =new OkHttpClient();
                Response response = client.newCall(request).execute();

                String json= response.body().string();

//                Sample OutPut
//                {
//                    "args": {},
//                    "data": {},
//                    "files": {},
//                    "form": {
//                    "data": "in"
//                },
//                    "headers": {
//                    "x-forwarded-proto": "https",
//                            "host": "postman-echo.com",
//                            "content-length": "152",
//                            "accept-encoding": "gzip",
//                            "content-type": "multipart/form-data; boundary=3221d5c7-b2b4-4164-95d8-fa5b049c0c40",
//                            "user-agent": "okhttp/3.11.0",
//                            "x-forwarded-port": "443"
//                },
//                    "json": null,
//                        "url": "https://postman-echo.com/post"
//                }
                return new JSONObject(json).getJSONObject("form").getString(parameter);
            }catch (Exception e){
                e.printStackTrace();
            }
            return  "";
        }

        @Override
        protected void onPostExecute(String s)
        {
            hideProgressDialog();
            super.onPostExecute(s);
            returnResults(s + SettingStore.getInstance(MainActivity.this).getCallCounter());
            finish();
        }



    }


    public class ProcessMATHHandler extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            showProgressDialog("Calculating....");
            super.onPreExecute();
        }

        String expression;
        @Override
        protected String doInBackground(String[] input)
        {
            expression=input[0];
//            https://api.mathjs.org/v4/?expr=2^8
            String parameter=getString(R.string.parameter_math);
            Request request = new Request.Builder().url(getString(R.string.server_url_math)+"?"+parameter+"="+input[0])
                    .get().build();
            try {
                OkHttpClient client =new OkHttpClient();
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception e){
                e.printStackTrace();
            }
            return  "Server Error";
        }

        @Override
        protected void onPostExecute(String s)
        {
            hideProgressDialog();
            super.onPostExecute(s);
            returnResults(s);
            finish();
        }
    }

    void returnResults(String output)
    {
        Intent intent=new Intent();
        intent.putExtra(EXTRA_PARAM_INPUT1,output);
        setResult(RESULT_OK,intent);
    }


    ProgressDialog mProgressDialog;
    void showProgressDialog(String message)
    {
        if(mProgressDialog==null) {
            // May be previous progress-bar still active
            hideProgressDialog();
        }
        mProgressDialog = ProgressDialog.show(MainActivity.this, "", message);
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




}

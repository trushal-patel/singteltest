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

    public final static String ACTION_PROCESS_TEXT="com.app.stel.PROCESS_TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getAction().equals(ACTION_PROCESS_TEXT))
        {
            ServerCallHandler serverCallHandler = new ServerCallHandler();
            serverCallHandler.execute(getIntent().getStringExtra(Intent.EXTRA_TEXT));
            //finish();
        } else
        {
            ///Invalid action detected.
            Toast.makeText(this, "App is installed & ready to use", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public class ServerCallHandler extends AsyncTask<String,Void,String>
    {



        @Override
        protected void onPreExecute() {
            showProgressDialog();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String[] input)
        {
            String parameter=getString(R.string.parameter);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(parameter, input[0])
                    .build();



            Request request = new Request.Builder().url(getString(R.string.server_url))
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
            Intent intent=new Intent();
            intent.putExtra(Intent.EXTRA_TEXT,s + SettingStore.getInstance(MainActivity.this).getCallCounter());
            setResult(RESULT_OK,intent);
            hideProgressDialog();
            finish();
            super.onPostExecute(s);
        }


        ProgressDialog mProgressDialog;
        void showProgressDialog()
        {
            if(mProgressDialog==null) {
                // May be previous progress-bar still active
                hideProgressDialog();
            }
            mProgressDialog = ProgressDialog.show(MainActivity.this, "", "Processing Text...");
        }

        void hideProgressDialog()
        {
            if(mProgressDialog!=null && mProgressDialog.isShowing())
            {
                mProgressDialog.dismiss();
                mProgressDialog=null;
            }
        }
    }





}

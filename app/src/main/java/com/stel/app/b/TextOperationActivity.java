package com.stel.app.b;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TextOperationActivity extends MyBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getAction().equals(ACTION_GET_RESULTS))
        {
            ProcessTextHandler serverCallHandler = new ProcessTextHandler();
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
            if(!isConnected())
                return  "Failed: No network Connection !!";

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
//                OutPut Format
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
                return new JSONObject(json).getJSONObject("form").getString(parameter) +" "+( SettingStore.getInstance(TextOperationActivity.this).getCallCounter());
            }catch (Exception e){
                e.printStackTrace();
            }
            /// TODO handle all cases of server error responses
            return  "Service unreachable. Please try again later.";
        }

        @Override
        protected void onPostExecute(String s)
        {
            returnResults(s);
            hideProgressDialog();
            super.onPostExecute(s);
            finish();
        }
    }



}

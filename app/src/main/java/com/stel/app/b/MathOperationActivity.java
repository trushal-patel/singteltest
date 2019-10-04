package com.stel.app.b;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MathOperationActivity extends MyBaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getAction().equals(ACTION_MATH_OPERATION))
        {
            ProcessMATHHandler serverCallHandler = new ProcessMATHHandler();
            serverCallHandler.execute(getIntent().getStringExtra(EXTRA_PARAM_INPUT1));
        } else
        {
            ///Invalid action detected.
            Toast.makeText(this, "Unsupported operation", Toast.LENGTH_SHORT).show();
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
            if(!isConnected())
                return  "Failed: No network Connection !!";

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

    void returnResults(String output)
    {
        Intent intent=new Intent();
        intent.putExtra(EXTRA_PARAM_INPUT1,output);
        setResult(RESULT_OK,intent);
    }






}

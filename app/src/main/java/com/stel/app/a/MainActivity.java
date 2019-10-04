package com.stel.app.a;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    public final static String ACTION_PROCESS_TEXT="com.app.stel.action_PROCESS_TEXT";
    public final static String ACTION_MATH_OPERATION="com.app.stel.action_PROCESS_MATH";

    public final static String EXTRA_PARAM_INPUT1="input1";

    public  final static int REQUEST_PROCESS_TEXT=100;
    public  final static int REQUEST_PROCESS_MATH=101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((RadioButton)findViewById(R.id.rdo_btn_maths_operation)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                findViewById(R.id.ll_maths).setVisibility((b)?View.VISIBLE:View.GONE);
                findViewById(R.id.ll_text_processiong).setVisibility((b)?View.GONE:View.VISIBLE);
            }
        });

        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edtInput = findViewById(R.id.edt_input);
                String input = edtInput.getText().toString();
                if (TextUtils.isEmpty(input))
                {
                    edtInput.setError(getString(R.string.error_message_please_enter_the_text));
                    return;
                }

                Intent intent=new Intent(ACTION_PROCESS_TEXT);
                intent.putExtra(EXTRA_PARAM_INPUT1,input);
                startActivityForResult(intent,REQUEST_PROCESS_TEXT);
            }
        });

        findViewById(R.id.btn_calculate).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                EditText edtInput = findViewById(R.id.edt_input1);
                String input1 = edtInput.getText().toString().trim();
                int error=0;
                if (TextUtils.isEmpty(input1))
                {
                    edtInput.setError(getString(R.string.error_message_please_valid_number));
                    error++;
                }
                edtInput = findViewById(R.id.edt_input2);
                String input2 = edtInput.getText().toString().trim();
                if (TextUtils.isEmpty(input2))
                {
                    edtInput.setError(getString(R.string.error_message_please_valid_number));
                    error++;
                }

                String selectedOpertion= getResources().getStringArray(R.array.array_math_operation)[((Spinner) findViewById(R.id.spn_option)).getSelectedItemPosition()];
                if(error==0) {
                    expression=input1+ selectedOpertion +input2+"=";
                    Intent intent = new Intent(ACTION_MATH_OPERATION);
                    intent.putExtra(EXTRA_PARAM_INPUT1, input1+ URLEncoder.encode(selectedOpertion)+input2);
                    startActivityForResult(intent, REQUEST_PROCESS_MATH);
                }
            }
        });
    }

    String expression;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode==REQUEST_PROCESS_TEXT)
        {
            if (data!=null)
            {
                if(data.hasExtra(EXTRA_PARAM_INPUT1))
                {
                    new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.title_text_received))
                            .setMessage(data.getStringExtra(EXTRA_PARAM_INPUT1)).setPositiveButton(getString(R.string.btn_title_ok),null).create().show();
                } else {
                    //TODO we can show error message.
                }
            } else
            {
                //TODO we can show error message.
            }
        }else  if(requestCode==REQUEST_PROCESS_MATH)
        {
            if (data!=null)
            {
                if(data.hasExtra(EXTRA_PARAM_INPUT1))
                {
                    new AlertDialog.Builder(MainActivity.this).setTitle(expression)
                            .setMessage(data.getStringExtra(EXTRA_PARAM_INPUT1)).setPositiveButton(getString(R.string.btn_title_ok),null).create().show();
                } else
                {
                    //TODO we can show error message.
                }
            } else
            {
                //TODO we can show error message.
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

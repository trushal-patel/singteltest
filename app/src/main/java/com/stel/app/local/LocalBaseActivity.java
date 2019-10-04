package com.stel.app.local;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LocalBaseActivity extends AppCompatActivity
{
    public void traverseChild(View root, Context c)
    {

        try {

            if(root==null)
                return;

            View childView;
            TextView tv;
            if (root instanceof ViewGroup)
            {
                ViewGroup relative = (ViewGroup) root;
                for (int i = 0; i < relative.getChildCount(); i++) {
                    childView = relative.getChildAt(i);
                    if (childView instanceof ViewGroup)
                        traverseChild(childView, c);
                    else if (childView instanceof TextView)
                    {
                        tv = (TextView) childView;
                        tv.setText(localize(tv.getText()
                                .toString(), c));
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public String localize(String english,Context c)
    {

        c.getResources().getConfiguration().setLocale();


        return english;
    }
}

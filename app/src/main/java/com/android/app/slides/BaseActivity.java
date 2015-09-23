package com.android.app.slides;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by francisco on 23/9/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
    }


    protected abstract int getLayoutResource();
}

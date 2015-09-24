package com.android.app.slides;

import android.os.Bundle;

/**
 * Created by francisco on 23/9/15.
 */
public class Hall extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setTitle(CharSequence title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Hall");
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.hall;
    }
}

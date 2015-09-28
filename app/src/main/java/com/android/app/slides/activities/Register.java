package com.android.app.slides.activities;

import android.os.Bundle;

import com.android.app.slides.R;

public class Register extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        /*firstName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (firstName.getText().toString().length <= 0)
                    firstName.setError("Enter FirstName");
            }
        });*/

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.login;
    }
}


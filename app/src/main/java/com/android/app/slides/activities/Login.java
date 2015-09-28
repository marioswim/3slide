package com.android.app.slides.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.app.slides.R;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;

import butterknife.Bind;

public class Login extends BaseActivity {
    @Bind(R.id.email) AutoCompleteTextView email;
    @Bind(R.id.password) EditText password;
    @Bind(R.id.sign_in_button) ButtonRectangle sign_in_button;
    @Bind(R.id.forgotten) ButtonFlat forgotten;

    Boolean hasMail = false;
    Boolean hasPass = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFields();

    }

    public void checkFields(){

        sign_in_button.setEnabled(false);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (hasPass) {
                        sign_in_button.setEnabled(true);
                    } else {
                        sign_in_button.setEnabled(false);
                    }
                    hasMail = true;
                } else {
                    hasMail = false;
                    sign_in_button.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if(hasMail){
                        sign_in_button.setEnabled(true);
                    }else{
                        sign_in_button.setEnabled(false);
                    }
                    hasPass = true;
                } else {
                    hasPass = false;
                    sign_in_button.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.login;
    }
}


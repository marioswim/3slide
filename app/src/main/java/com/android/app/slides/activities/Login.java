package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.android.app.slides.R;
import com.android.app.slides.Utilities.Utilities;
import com.android.app.slides.model.User;
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

        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = new User();
                user.setName("Fran");
                Utilities.saveUser(Login.this, user);

                Intent i = new Intent(Login.this, Home.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        forgotten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Recover.class);
                startActivity(intent);
            }
        });

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


package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.android.app.slides.R;
import com.android.app.slides.model.DAOUser;
import com.android.app.slides.tools.Utilities;
import com.android.app.slides.model.User;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;

import butterknife.Bind;

public class Login extends BaseActivity {
    @Bind(R.id.email) AutoCompleteTextView email;
    @Bind(R.id.password) EditText password;
    @Bind(R.id.sign_in_button) ButtonRectangle sign_in_button;
    @Bind(R.id.forgotten) ButtonFlat forgotten;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFields();

        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Utilities.isEmailValid(email.getText().toString())){
                    User user = new User();
                    user.setName("Fran");
                    DAOUser daoUser = new DAOUser(Login.this);
                    daoUser.saveUser(user);

                    Intent i = new Intent(Login.this, Home.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }else{
                    email.setError("Email no válido");
                }

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

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (completedFields()) {
                    sign_in_button.setEnabled(true);
                } else {
                    sign_in_button.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.login;
    }

    private boolean completedFields(){
        if(email.getText().length()>0 && password.getText().length()>0){
            return true;
        }else{
            return false;
        }
    }
}


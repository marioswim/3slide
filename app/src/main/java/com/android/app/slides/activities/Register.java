package com.android.app.slides.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.android.app.slides.R;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;

import butterknife.Bind;

public class Register extends BaseActivity {
    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.email2)
    EditText email2;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.registerBtn)
    ButtonRectangle registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFields();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.register;
    }

    private void checkFields(){

        registerBtn.setEnabled(false);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (completedFields()){
                    registerBtn.setEnabled(true);
                }else{
                    registerBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        name.addTextChangedListener(watcher);
        email.addTextChangedListener(watcher);
        email2.addTextChangedListener(watcher);
        password.addTextChangedListener(watcher);
    }

    private boolean completedFields(){
        if(name.getText().length()>0 && email.getText().length()>0 && email2.getText().length()>0 && password.getText().length()>0){
            return true;
        }else{
            return false;
        }
    }
}


package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.app.slides.R;
import com.android.app.slides.tools.DialogManager;
import com.android.app.slides.tools.Utilities;
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
    @Bind(R.id.termsCheckBox)
    CheckBox termsCheckBox;
    @Bind(R.id.termsText)
    TextView termsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFields();

        termsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.showDialog(Register.this, "Términos de usuario", "1- Blabla");
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean go = true;
                if(!Utilities.isEmailValid(email.getText().toString())){
                    email.setError("Email no válido");
                    go = false;
                }
                if (!email.getText().toString().equals(email2.getText().toString())){
                    email2.setError("No conciden los emails");
                    go = false;
                }

                if(go){
                    Intent i = new Intent(Register.this, Home.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }
        });
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

        termsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (completedFields()){
                    registerBtn.setEnabled(true);
                }else{
                    registerBtn.setEnabled(false);
                }
            }
        });
    }

    private boolean completedFields(){
        if(name.getText().length()>0 && email.getText().length()>0 && email2.getText().length()>0 && password.getText().length()>0 && termsCheckBox.isChecked()){
            return true;
        }else{
            return false;
        }
    }
}


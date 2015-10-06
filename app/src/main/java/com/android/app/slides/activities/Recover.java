package com.android.app.slides.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.android.app.slides.R;
import com.gc.materialdesign.views.ButtonRectangle;

import butterknife.Bind;

/**
 * Created by francisco on 26/9/15.
 */
public class Recover extends BaseActivity {
    @Bind(R.id.email)
    AutoCompleteTextView email;
    @Bind(R.id.recoverBtn)
    ButtonRectangle recoverBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFields();
    }



    @Override
    protected int getLayoutResource() {
        return R.layout.recover;
    }

    private void checkFields(){
        recoverBtn.setEnabled(false);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>0){
                    recoverBtn.setEnabled(true);
                }else{
                    recoverBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}

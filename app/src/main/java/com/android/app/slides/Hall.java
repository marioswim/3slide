package com.android.app.slides;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.app.slides.Utilities.Utilities;

import butterknife.Bind;

/**
 * Created by francisco on 23/9/15.
 */
public class Hall extends BaseActivity {
    @Bind(R.id.loginBtn) Button loginBtn;
    @Bind(R.id.registerBtn) Button registerBtn;
    @Bind(R.id.enterLayout) View enterLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Utilities.loadUser(this)!=null){
            enterLayout.setVisibility(View.GONE);
            Intent i = new Intent(Hall.this, Home.class);
            startActivity(i);
        }else{
            enterLayout.setVisibility(View.VISIBLE);

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Hall.this, Login.class);
                    startActivity(i);
                }
            });

            registerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Hall.this, Register.class);
                    startActivity(i);
                }
            });
        }

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

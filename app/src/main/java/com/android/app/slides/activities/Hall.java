package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.app.slides.R;
import com.android.app.slides.model.DAOUser;
import com.gc.materialdesign.views.ButtonRectangle;

import butterknife.Bind;

/**
 * Created by francisco on 23/9/15.
 */
public class Hall extends BaseActivity {
    @Bind(R.id.loginBtn) ButtonRectangle loginBtn;
    @Bind(R.id.registerBtn) ButtonRectangle registerBtn;
    @Bind(R.id.enterLayout) View enterLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DAOUser daoUser = new DAOUser(this);

        if(daoUser.loadUser()!=null){
            enterLayout.setVisibility(View.GONE);
            Intent i = new Intent(Hall.this, Home.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

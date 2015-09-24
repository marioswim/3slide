package com.android.app.slides;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.slides.Utilities.Utilities;

/**
 * Created by francisco on 23/9/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        loadToolbar();
        checkInternetConnection();
    }


    protected abstract int getLayoutResource();

    protected void loadToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            //TextView toolbarText = Tools.getActionBarTextView(toolbar);
            //if(toolbarText!=null){
            //    toolbarText.setTypeface(ToolsTypeFace.getNotoRegular(this));
            //}
        }
    }

    protected void checkInternetConnection(){
        if (!Utilities.isNetworkAvailable(this)){
            Toast.makeText(this, "No tiene conexión a Internet", Toast.LENGTH_LONG).show();
            //Start hall with a bundle and a button to retry
            //Toast con button para reintentar
        }
    }
}

package com.android.app.slides.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.android.app.slides.R;
import com.gc.materialdesign.views.ButtonRectangle;

public class Search extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

       //5 Button search= (Button) findViewById(R.id.searchButton);
        com.gc.materialdesign.views.ButtonRectangle searcButton= (ButtonRectangle) findViewById(R.id.searchButton);


        searcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Search.this,SearchResult.class);

                startActivity(intent);
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.search;
    }
}

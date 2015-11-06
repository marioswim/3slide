package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.app.slides.R;
import com.gc.materialdesign.views.ButtonRectangle;

import butterknife.Bind;

/**
 * Created by david on 6/11/15.
 */
public class Presentation extends BaseActivity {
    @Bind(R.id.UploadPptBtn) ButtonRectangle UploadPptBtn;

    @Override
    protected int getLayoutResource() {
        return R.layout.presentation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UploadPptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("file/*"); // intent type to filter application based on your requirement
                startActivityForResult(fileIntent, RESULT_OK);
            }
        });
    }
}

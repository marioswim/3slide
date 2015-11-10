package com.android.app.slides.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;

import com.android.app.slides.R;
import com.joanzapata.pdfview.PDFView;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by francisco on 8/11/15.
 */
public class PDFViewer extends AppCompatActivity {
    @Bind(R.id.pdfView)
    PDFView pdfView;
    String pdfPath;
    URI uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pdf_viewer);
        ButterKnife.bind(this);

        if (getIntent().getExtras()!=null){
            pdfPath = getIntent().getExtras().getString("path", null);

            if(pdfPath!=null){
                try {
                    File file = new File(new URI(pdfPath));
                    if(file.exists()){
                        pdfView.fromFile(file)
                                .defaultPage(1)
                                .swipeVertical(true)
                                .showMinimap(false)
                                .enableSwipe(true)
                                .load();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }




            }else{
                finish();
            }
        }else{
            finish();
        }


    }

    public void setURI(URI uri){
        this.uri = uri;
    }


}

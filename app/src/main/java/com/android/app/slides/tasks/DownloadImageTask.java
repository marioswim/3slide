package com.android.app.slides.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.app.slides.R;
import com.android.app.slides.model.VolleySingleton;
import com.android.app.slides.tools.Configurations;
import com.android.app.slides.tools.SlidesApp;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

/**
 * Created by francisco on 13/11/15.
 */
public class DownloadImageTask {

    String url;
    ImageView imageView;
    Context context;

    public DownloadImageTask(String url, ImageView imageView, Context APPcontext){
        this.url = url;
        this.imageView = imageView;
        this.context = APPcontext;
    }

    public void execute(){
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        if(bitmap!=null){
                            imageView.setImageBitmap(bitmap);
                            SlidesApp app = new SlidesApp();
                            app.setUserBitmap(bitmap);
                        }
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        imageView.setImageResource(R.drawable.avatar);
                    }
                });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}

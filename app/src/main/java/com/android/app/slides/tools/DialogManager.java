package com.android.app.slides.tools;

import android.app.Activity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.app.slides.R;

/**
 * Created by francisco on 7/10/15.
 */
public class DialogManager{

    public static void showDialog(final Activity a, String titleText, String contentText){

        new MaterialDialog.Builder(a)
                .title(titleText)
                .content(contentText)
                .positiveText("Ok")
                .show();

    }
}

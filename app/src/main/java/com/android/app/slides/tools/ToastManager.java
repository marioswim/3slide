package com.android.app.slides.tools;

import android.app.Activity;

import com.android.app.slides.R;
import com.github.mrengineer13.snackbar.SnackBar;

/**
 * Created by francisco on 7/10/15.
 */
public class ToastManager {

    public static void showToast(Activity a, String text){
        new SnackBar.Builder(a)
                .withMessage(text)
                .withActionMessage("Ok")
                .withTextColorId(R.color.accent)
                .withStyle(SnackBar.Style.CONFIRM)
                .withDuration(SnackBar.MED_SNACK)
                .show();
    }
}
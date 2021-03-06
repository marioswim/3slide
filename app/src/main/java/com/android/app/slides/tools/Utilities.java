package com.android.app.slides.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.app.slides.services.LocationService;
import com.android.app.slides.tasks.DownloadImageTask;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by francisco on 24/9/15.
 */
public class Utilities {

    private static Typeface typeface;

    public static boolean isNetworkAvailable(Activity a) {
        Context context = a.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Toast.makeText(a.getBaseContext(), "No hay internet", Toast.LENGTH_LONG).show();
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void hideKeyboard(Activity a){
        View view = a.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String getErrorMsgById(int errorNo){
        switch (errorNo){
            case 1:
                return "Dirección de email o contraseña incorrectas";
            case 2:
                return "Dirección de email o contraseña incorrectas";
            case 1062:
                return "La dirección de email ya está registrada en el sistema";
            default:
                return "Ha ocurrido un error, por favor inténtelo de nuevo más tarde";
        }
    }

    public static boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String ofuscate(String pass){
        return Utilities.md5(pass);
    }

    private static Typeface getTypefaceAsset(Context context, String typefaceName) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), typefaceName);
        }
        return typeface;
    }

    public static Typeface getLightFont(Context context) {
        return getTypefaceAsset(context, "light.ttf");
    }

    public static Typeface getNormalFont(Context context) {
        return getTypefaceAsset(context, "normal.ttf");
    }

    public static void profileImageServer(String url, ImageView imageView, Context APPContext){
        if(SlidesApp.getUserBitmap()!= null){
            imageView.setImageBitmap(SlidesApp.getUserBitmap());
        }else{
            DownloadImageTask downloadImageTask = new DownloadImageTask(url, imageView, APPContext);
            downloadImageTask.execute();
        }
    }

    public static void makeCall(Context context, String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    public static void makeEmail(Context context, String email, String userName){
        Intent itSend = new Intent(Intent.ACTION_SEND);
        itSend.setType("text/plain");
        itSend.putExtra(android.content.Intent.EXTRA_EMAIL, email);
        itSend.putExtra(Intent.EXTRA_SUBJECT, "Mensaje de " + userName + " - 3Slides");
        context.startActivity(Intent.createChooser(itSend, "Selecciona una aplicación"));
    }
}

package com.android.app.slides.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.app.slides.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francisco on 5/10/15.
 */
public class Preferences extends PreferenceActivity {

    Preference location;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        location = getPreferenceManager().findPreference("pref_location");

        if (location != null) {
            location.setSummary("No");

            location.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    ArrayList<String> options = new ArrayList<String>();
                    options.add("Yes");
                    options.add("No");

                    new MaterialDialog.Builder(Preferences.this)
                            .title("Location")
                            .items(options.toArray(new String[options.size()]))
                            .alwaysCallSingleChoiceCallback()
                            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    /**
                                     * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                     * returning false here won't allow the newly selected radio button to actually be selected.
                                     **/
                                    return true;
                                }
                            })
                            .show();
                    return false;
                }
            });
        }
    }
}

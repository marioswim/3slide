package com.android.app.slides.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.app.slides.R;
import com.android.app.slides.tools.Configurations;
import com.android.app.slides.tools.Constants;

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

            switch (Configurations.getLocationModeId(Preferences.this)){
                case 0:
                    location.setSummary("Yes");
                    break;
                case 1:
                    location.setSummary("No");
                    break;
            }

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
                            .itemsCallbackSingleChoice(Configurations.getLocationModeId(Preferences.this), new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    /**
                                     * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                     * returning false here won't allow the newly selected radio button to actually be selected.
                                     **/

                                    switch (which) {
                                        case 0:
                                            Configurations.saveLocationMode(Constants.SERVICE_MODE_FOREVER, Preferences.this);
                                            reloadSummary("Yes", location);
                                            break;
                                        case 1:
                                            Configurations.saveLocationMode(Constants.SERVICE_MODE_ONCE, Preferences.this);
                                            reloadSummary("No", location);
                                            break;
                                    }
                                    return true;
                                }
                            })
                            .show();
                    return false;
                }
            });
        }
    }

    public void reloadSummary(String summary, Preference preference){
        if (preference != null) {
            preference.setSummary(summary);
        }
    }
}

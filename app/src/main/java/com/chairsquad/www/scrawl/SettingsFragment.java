package com.chairsquad.www.scrawl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.chairsquad.www.scrawl.activities.MainActivity;
import com.chairsquad.www.scrawl.utilities.PreferenceUtils;
import com.chairsquad.www.scrawl.utilities.ScrawlConnection;

/**
 * Created by henry on 18/04/17.
 */

public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName(PreferenceUtils.getEmail(getContext()));

        addPreferencesFromResource(R.xml.pref_general);

        PreferenceScreen prefScreen = getPreferenceScreen();
        Preference preference = prefScreen.getPreference(1);
        preference.setSummary("Signed in as " + PreferenceUtils.getEmail(getContext()));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ScrawlConnection.logout(getContext());
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            }
        });
    }

}

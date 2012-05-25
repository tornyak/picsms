package com.tornyak.picsms;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PicsmsPreferences extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.picsms_preferences);
	}

}

package com.tornyak.picsms;

import android.app.Application;
import android.util.Log;

public class PicsmsApplication extends Application {
	public static final String LOG_TAG = "PicsmsApp";
	private PicsmsDbAdapter picsmsDb;

	@Override
	public void onCreate() {
		Log.d(LOG_TAG, "onCreate()");
		super.onCreate();
		picsmsDb = new PicsmsDbAdapter(this);
		picsmsDb.open();
	}

	@Override
	public void onTerminate() {
		Log.d(LOG_TAG, "onCreate()");
		super.onTerminate();
		picsmsDb.close();
	}

	public PicsmsDbAdapter getPicsmsDb() {
		return picsmsDb;
	}
}

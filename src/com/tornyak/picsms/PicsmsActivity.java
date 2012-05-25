package com.tornyak.picsms;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.tornyak.picsms.pictures.BitmapUtils;
import com.tornyak.picsms.pictures.PictureBook;
import com.tornyak.picsms.pictures.PictureBookBuilderXml;

public class PicsmsActivity extends ActionBarActivity {
	public static final String LOG_TAG = "PicssmsActivity";

	private PictureBook pb;
	private PictureMessageBar pictureMessageBar;
	private PictureGridMenu pictureGridMenu;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			// Get memory size in MB of this device, exceeding this amount will throw an
			// OutOfMemory exception.
			final int memClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
			// Use 1/8th of the available memory for this memory cache.
			final int cacheSize = 1024 * 1024 * memClass / 8;
			BitmapUtils.initBmpMemoryCache(cacheSize);

			pb = PictureBookBuilderXml.buildPictureBookFromXML(getResources(), R.xml.picture_book);
			setContentView(R.layout.main);
			GridView gvMenu = (GridView) findViewById(R.id.menuGridView);
			GridView gvMessage = (GridView) findViewById(R.id.messageGridView);
			pictureMessageBar = new PictureMessageBar(this, gvMessage);
			pictureGridMenu = new PictureGridMenu(this, pb, gvMenu, pictureMessageBar);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);

		// Calling super after populating the menu is necessary here to ensure that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
			break;

			case R.id.menu_refresh:
				Toast.makeText(this, "Fake refreshing...", Toast.LENGTH_SHORT).show();
				getActionBarHelper().setRefreshActionItemState(true);
				getWindow().getDecorView().postDelayed(new Runnable() {
					@Override
					public void run() {
						getActionBarHelper().setRefreshActionItemState(false);
					}
				}, 1000);
			break;

			case R.id.menu_share:
				Toast.makeText(this, "Tapped share", Toast.LENGTH_SHORT).show();
			case R.id.menu_settings:
				Intent it = new Intent(this, PicsmsPreferences.class);
				startActivity(it);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void onBackPressed() {
		if (pb.getActiveCategory() != null)
			pictureGridMenu.back();
		else
			super.onBackPressed();
	}

	public void onDeletePressed(View v) {
		pictureMessageBar.delete();
	}
}
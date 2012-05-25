package com.tornyak.picsms;

import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class PhoneBookActivity extends Activity {

	private static final String LOG_TAG = "PhoneBookActivity";

	private PicsmsDbAdapter picsmsDbAdapter;
	private Cursor phoneCursor;
	private GridView contactView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phonebook);
		picsmsDbAdapter = new PicsmsDbAdapter(this);
		picsmsDbAdapter.open();
		phoneCursor = picsmsDbAdapter.getAllPhoneNumberItemsCursor();
		startManagingCursor(phoneCursor);
		bindPhoneNumbers();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void bindPhoneNumbers() {
		if (contactView == null) {
			contactView = (GridView) findViewById(R.id.phoneBookGridView);
		}
		contactView.setAdapter(new PhoneNumbersCursorAdapter(this, phoneCursor));
	}

	private class PhoneNumbersCursorAdapter extends CursorAdapter {
		private Rect mOldBounds = new Rect();
		private Cursor mCursor;
		private Context mContext;
		private final LayoutInflater mInflater;

		public PhoneNumbersCursorAdapter(Context context, Cursor c) {
			super(context, c, true);
			mContext = context;
			mInflater = LayoutInflater.from(context);
			mCursor = c;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			PhoneNumberInfo info = new PhoneNumberInfo();

			info.contactName = cursor.getString(cursor.getColumnIndex(PicsmsDbAdapter.PhoneNumberColumns.KEY_CONTACT_NAME));
			info.phoneNumber = cursor.getString(cursor.getColumnIndex(PicsmsDbAdapter.PhoneNumberColumns.KEY_PHONE_NUMBER));
			info.phoneType = cursor.getString(cursor.getColumnIndex(PicsmsDbAdapter.PhoneNumberColumns.KEY_PHONE_TYPE));
			info.contactId = cursor.getLong(cursor.getColumnIndex(PicsmsDbAdapter.PhoneNumberColumns.KEY_CONTACT_ID));
			Drawable icon = getContactPhoto(info.contactId);

			TextView textView = (TextView) view;

			textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
			textView.setTextSize(16);
			textView.setGravity(Gravity.CENTER);
			textView.setText(info.contactName);

			textView.setTag(info);

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {

			TextView textView = new TextView(context);
			return textView;

		}

		private BitmapDrawable getContactPhoto(long contactId) {
			String ID = String.valueOf(contactId);
			Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, ID);
			BitmapDrawable ret = null;

			InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), contactUri);

			if (is != null)
				ret = new BitmapDrawable(is);

			return ret;

		}

	}

}

package com.tornyak.picsms;

import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
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

import com.tornyak.picsms.pictures.BitmapUtils;

public class PhoneBookActivity extends Activity {

	private static final String LOG_TAG = "PhoneBookActivity";

	private PicsmsDbAdapter picsmsDbAdapter;
	private Cursor phoneCursor;
	private GridView contactView;

	private int textColor;
	private float textSize;
	private float pictureWidth;
	private Drawable defaultContactIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);

		Resources res = getResources();

		textColor = res.getColor(R.color.menu_icon_text);
		textSize = res.getDimension(R.dimen.grid_picture_text_size);
		pictureWidth = res.getDimension(R.dimen.grid_contact_picture_width);
		Bitmap b = BitmapUtils.decodeSampledBitmapFromResource(res, R.drawable.ic_contact, (int) pictureWidth, (int) pictureWidth);
		defaultContactIcon = new BitmapDrawable(b);

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
			if (icon == null)
				icon = defaultContactIcon;

			TextView textView = (TextView) view;

			textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
			textView.setTextColor(textColor);
			textView.setTextSize(textSize);
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
			else {
				// TODO add default picture
			}

			return ret;

		}

	}

}

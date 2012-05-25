package com.tornyak.picsms;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

public class PicsmsDbAdapter {

	private static final String DATABASE_NAME = "picsms.db";
	private static final String DATABASE_PHONE_NUMBER_TABLE = "phone_numbers";
	private static final int DATABASE_VERSION = 1;
	private static final String DATE_FORMAT_STRING = "MMM dd, yyyy HH:mm";

	public static class PhoneNumberColumns implements BaseColumns {
		public static final String KEY_ID = "_id";
		public static final String KEY_CONTACT_ID = "contact_id";
		public static final String KEY_CONTACT_NAME = "contact_name";
		public static final String KEY_PHONE_NUMBER = "phone_number";
		public static final String KEY_PHONE_TYPE = "phone_type";
	}

	private SQLiteDatabase db;
	private final Context context;
	private EmergencyDBOpenHelper dbHelper;
	private SimpleDateFormat dateFormat;;

	public PicsmsDbAdapter(Context context) {
		this.context = context;
		dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
		dbHelper = new EmergencyDBOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void close() {
		db.close();
	}

	public void open() throws SQLiteException {
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			db = dbHelper.getReadableDatabase();
		}
	}

	public long insertPhoneNumber(PhoneNumberInfo pni) {
		// Create a new row of values to insert.
		ContentValues newAppValues = new ContentValues();
		// Assign values for each row.
		newAppValues.put(PhoneNumberColumns.KEY_CONTACT_ID, pni.contactId);
		newAppValues.put(PhoneNumberColumns.KEY_CONTACT_NAME, pni.contactName);
		newAppValues.put(PhoneNumberColumns.KEY_PHONE_NUMBER, pni.phoneNumber);
		newAppValues.put(PhoneNumberColumns.KEY_PHONE_TYPE, pni.phoneType);

		return db.insert(DATABASE_PHONE_NUMBER_TABLE, null, newAppValues);
	}

	public boolean removePhoneNumber(long rowIndex) {
		return db.delete(DATABASE_PHONE_NUMBER_TABLE, PhoneNumberColumns.KEY_ID + "=" + rowIndex, null) > 0;
	}

	public Cursor getAllPhoneNumberItemsCursor() {
		return db.query(DATABASE_PHONE_NUMBER_TABLE, null, null, null, null, null, null);
	}

	public Cursor setCursorToPhoneNumberItem(long rowIndex) throws SQLException {
		Cursor result = db.query(true, DATABASE_PHONE_NUMBER_TABLE, null, PhoneNumberColumns.KEY_ID + "=" + rowIndex, null, null, null, null, null);

		if ((result.getCount() == 0) || !result.moveToFirst()) {
			throw new SQLException("No phone number items found for row: " + rowIndex);
		}
		return result;
	}

	public Cursor setCursorToPhoneNumberItem(String phoneNumber) throws SQLException {
		Cursor result = db.query(true, DATABASE_PHONE_NUMBER_TABLE, null, PhoneNumberColumns.KEY_PHONE_NUMBER + "=" + phoneNumber, null, null, null, null, null);
		if ((result.getCount() == 0) || !result.moveToFirst()) {
			throw new SQLException("No phone number items found for number: " + phoneNumber);
		}
		return result;
	}

	public PhoneNumberInfo getPhoneNumberItem(String phoneNumber) throws SQLException {
		Cursor cursor = db.query(true, DATABASE_PHONE_NUMBER_TABLE, null, PhoneNumberColumns.KEY_PHONE_NUMBER + "=" + phoneNumber, null, null, null, null, null);

		if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
			throw new SQLException("No phone item found for title: " + phoneNumber);
		}

		String contactName = cursor.getString(cursor.getColumnIndex(PhoneNumberColumns.KEY_CONTACT_NAME));
		String phoneType = cursor.getString(cursor.getColumnIndex(PhoneNumberColumns.KEY_PHONE_TYPE));
		long contactId = cursor.getLong(cursor.getColumnIndex(PhoneNumberColumns.KEY_CONTACT_ID));

		PhoneNumberInfo result = new PhoneNumberInfo();
		result.contactId = contactId;
		result.contactName = contactName;
		result.phoneNumber = phoneNumber;
		result.phoneType = phoneType;
		result.contactPhoto = getContactPhoto(contactId);

		cursor.close();

		return result;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	private static class EmergencyDBOpenHelper extends SQLiteOpenHelper {

		public EmergencyDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// SQL Statement to create a new database.

		private static final String DATABASE_CREATE_PHONE_NUMBER_TABLE = "create table " + DATABASE_PHONE_NUMBER_TABLE + " (" + PhoneNumberColumns.KEY_ID + " integer primary key autoincrement, "
				+ PhoneNumberColumns.KEY_CONTACT_ID + " integer, " + PhoneNumberColumns.KEY_CONTACT_NAME + " text, " + PhoneNumberColumns.KEY_PHONE_NUMBER + " text, "
				+ PhoneNumberColumns.KEY_PHONE_TYPE + " text);";

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_PHONE_NUMBER_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

			// Drop the old table.
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_PHONE_NUMBER_TABLE);

			// Create a new one.
			onCreate(db);
		}
	}

	public BitmapDrawable getContactPhoto(long contactId) {
		String ID = String.valueOf(contactId);
		Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, ID);
		BitmapDrawable ret = null;

		InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), contactUri);

		if (is != null)
			ret = new BitmapDrawable(is);

		return ret;

	}

}

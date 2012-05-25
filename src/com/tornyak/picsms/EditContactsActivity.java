package com.tornyak.picsms;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class EditContactsActivity extends Activity implements OnClickListener {
	public static final String LOG_TAG = "EditContactsActivity";

	private ListView allowedList;
	private Button addButton;
	private PicsmsDbAdapter dbAdapter;
	private Cursor phoneCursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_contacts);
		allowedList = (ListView) findViewById(R.id.callFilterListView);
		addButton = (Button) findViewById(R.id.callFilterAddButton);
		addButton.setOnClickListener(this);
		loadPhoneNumbers();
		registerForContextMenu(allowedList);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.callFilterAddButton:
				Intent intent = new Intent(this, PickContactActivity.class);
				startActivityForResult(intent, PickContactActivity.PICK_NUMBER_REQUEST);
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent in) {
		super.onActivityResult(requestCode, resultCode, in);
		Log.d(LOG_TAG, "requestCode " + requestCode + " resultCode " + resultCode);

		if (resultCode == Activity.RESULT_OK && requestCode == PickContactActivity.PICK_NUMBER_REQUEST) {
			long contactId = in.getLongExtra(PickNumberActivity.CONTACT_ID, 0);
			String contactName = in.getStringExtra(PickNumberActivity.CONTACT_NAME);
			String phoneNumber = in.getStringExtra(PickNumberActivity.PHONE_NUMBER);
			String numberType = in.getStringExtra(PickNumberActivity.NUMBER_TYPE);

			PhoneNumberInfo pni = new PhoneNumberInfo();
			pni.contactId = contactId;
			pni.contactName = contactName;
			pni.phoneNumber = phoneNumber;
			pni.phoneType = numberType;

			if (getIntent().getAction().equals("com.tornyak.picsms.ACTION_EDIT_CONTACT_LIST"))

				dbAdapter.insertPhoneNumber(pni);

			String toastText = "ID: " + contactId + "\nName: " + contactName + "\nPhone " + phoneNumber + "\nType " + numberType;
			Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG);
			toast.show();
		}
	}

	private void loadPhoneNumbers() {
		dbAdapter = new PicsmsDbAdapter(this);
		dbAdapter.open();

		phoneCursor = dbAdapter.getAllPhoneNumberItemsCursor();

		startManagingCursor(phoneCursor);
		String[] fields = new String[] { PicsmsDbAdapter.PhoneNumberColumns.KEY_CONTACT_NAME, PicsmsDbAdapter.PhoneNumberColumns.KEY_PHONE_NUMBER };

		allowedList.setAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, phoneCursor, fields, new int[] { android.R.id.text1, android.R.id.text2 }));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.phone_numbers_list_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.deletePhoneItem:
				deletePhoneNumber(info.id);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	private void deletePhoneNumber(long id) {
		dbAdapter.removePhoneNumber(id);
		phoneCursor.requery();
		((SimpleCursorAdapter) allowedList.getAdapter()).notifyDataSetChanged();
	}

}

package com.tornyak.picsms;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class PickContactActivity extends ListActivity implements TextWatcher, OnClickListener {

	public static final String LOG_TAG = "PickContactActivity";

	public static final int PICK_NUMBER_REQUEST = 1;

	private Cursor contactsCursor;
	private Button doneButton;
	private EditText numberEditText;
	private SimpleCursorAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View header = li.inflate(R.layout.pick_contact_list_header, null);
		numberEditText = (EditText) header.findViewById(R.id.enterNameOrNumberTextField);
		doneButton = (Button) header.findViewById(R.id.pickContactDoneButton);
		doneButton.setOnClickListener(this);
		numberEditText.addTextChangedListener(this);

		getListView().setTextFilterEnabled(true);
		getListView().addHeaderView(header);
		populateContactList();

		adapter.setStringConversionColumn(adapter.getCursor().getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		adapter.setFilterQueryProvider(new FilterQueryProvider() {

			@Override
			public Cursor runQuery(CharSequence constraint) {
				String partialItemName = null;
				if (constraint != null) {
					partialItemName = constraint.toString();
				}
				return getContacts(partialItemName);
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		numberEditText.removeTextChangedListener(this);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Start a new activity for picking a number for
		// selected contact

		Intent in = new Intent(this, PickNumberActivity.class);
		in.putExtra(PickNumberActivity.SELECTED_CONTACT_ID, id);
		startActivityForResult(in, PickNumberActivity.PICK_NUMBER_REQUEST);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent in) {
		super.onActivityResult(requestCode, resultCode, in);

		if (resultCode == RESULT_OK) {
			// return Intent with extra data to the calling activity
			setResult(RESULT_OK, in);
			finish();
		} else {
			Log.d(LOG_TAG, "onActivityResult requestCode " + requestCode + " resultCode " + resultCode);
		}

	}

	private void populateContactList() {
		// Build adapter with contact entries
		contactsCursor = getContacts(null);
		String[] fields = new String[] { ContactsContract.Contacts.DISPLAY_NAME };

		adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, contactsCursor, fields, new int[] { android.R.id.text1 });

		setListAdapter(adapter);
	}

	private Cursor getContacts(CharSequence filterValue) {
		// Run query
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };

		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

		if (filterValue == null || filterValue.length() == 0) {
			return managedQuery(uri, projection, null, null, sortOrder);
		} else {
			String[] selectionArgs = new String[] { "%" + filterValue.toString() + "%" };
			String selection = ContactsContract.Contacts.DISPLAY_NAME + " like ?";
			return managedQuery(uri, projection, selection.toString(), selectionArgs, sortOrder);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		adapter.getFilter().filter(s);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.pickContactDoneButton:
				String phoneNumber = numberEditText.getText().toString();
				Intent in = new Intent();
				in.putExtra(PickNumberActivity.PHONE_NUMBER, phoneNumber);
				in.putExtra(PickNumberActivity.CONTACT_NAME, getString(R.string.unknown));
				setResult(RESULT_OK, in);
				finish();
			break;
			default:
			break;
		}

	}
}

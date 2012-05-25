package com.tornyak.picsms;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class PickNumberActivity extends Activity implements OnClickListener
{
	public static final String LOG_TAG = "PickNumberActivity";
	public static final String SELECTED_CONTACT_ID = "SELECTED_CONTACT_URI";
	public static final String CONTACT_ID = "CONTACT_ID";
	public static final String CONTACT_NAME = "CONTACT_NAME";
	public static final String PHONE_NUMBER = "PHONE_NUMBER";
	public static final String NUMBER_TYPE = "NUMBER_TYPE";

	
	public static final int PICK_NUMBER_REQUEST = 1;
	
	private TextView contactNameView;
	private ImageView contactPhoto;
	private LayoutInflater li;
	private View contentView;
	private LinearLayout layout;
	
	private long contactId;
	private String contactName;
	private String phoneNumber;
	private String phoneType;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = li.inflate(R.layout.pick_number, null);
		layout = (LinearLayout)contentView.findViewById(R.id.layout);
		contactNameView = (TextView)contentView.findViewById(R.id.nameView);
		contactPhoto = (ImageView)contentView.findViewById(R.id.contactPicture);
		Intent in = getIntent();
		contactId = in.getLongExtra(SELECTED_CONTACT_ID, -1);
		getNumbersForContact(contactId);
		BitmapDrawable bd = getContactPhoto(contactId);		
		
		if(bd != null)
		{
			contactPhoto.setImageDrawable(bd);
		}
		 
		setContentView(contentView);
		
	}
	
	

	
	private void getNumbersForContact(long id)
	{		
		if(id < 0)
		{	
			Toast.makeText(this, "Selected contact does not contain phone numbers", Toast.LENGTH_SHORT);
			return;
		}
		
		String ID = String.valueOf(id);
		
		Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, ID);
		String[] contactProjection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
        };
		
		Cursor cContact = managedQuery(contactUri, contactProjection, null, null, null);
		
		if(cContact.moveToFirst())
		{
			contactName = cContact.getString(cContact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
    		contactNameView.setText(contactName);
    		int hasPhoneNumber = Integer.parseInt(cContact.getString(cContact.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))); 
    		if ( hasPhoneNumber > 0)
    		{
    			String[] phonesProjection = new String[] {
    	                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
    	                ContactsContract.CommonDataKinds.Phone.NUMBER,
    	                ContactsContract.CommonDataKinds.Phone.TYPE,
    	                ContactsContract.CommonDataKinds.Phone.LABEL
    	        };
    			
    			
    			//Query phone here.
    			Cursor cPhone = managedQuery(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
    										 phonesProjection,
    										 ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
    										 new String[]{ID},
    										 null);
    			
    			ArrayList<View> vList = new ArrayList<View>();

    			while (cPhone.moveToNext())
    			{
    		 		View v = li.inflate(R.layout.pick_number_button, (ViewGroup)contentView, false);
    		 		
    		 		
    		 		TextView phoneTypeView = (TextView)v.findViewById(R.id.phoneType);
    		 		TextView phoneNumberView = (TextView)v.findViewById(R.id.phoneNumber);
    		 		
    		 		
    		 		phoneNumber = cPhone.getString(cPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    		 		int type = cPhone.getInt(cPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
    		 		
    		 		if(type == ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
    		 		{
    		 			phoneType = cPhone.getString(cPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
    		 		}
    		 		else
    		 		{
    		 			phoneType = getPhoneType(type);
    		 		}
    		 		
    		 		phoneNumberView.setText(phoneNumber);
    		 		phoneTypeView.setText(phoneType);
    		 		
    		 		
    		 		layout.addView(v);
    		 		
    		 		v.setOnClickListener(this);
    		 		vList.add(v);
    		 		v.setId(vList.size());

    		 	}
    		}
		}
		else
		{
			Log.e(LOG_TAG, "No contact for Uri: " + contactUri.toString());
		}
	}
	
	public BitmapDrawable getContactPhoto(long contactId)
	{
		String ID = String.valueOf(contactId);		
		Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, ID);
		BitmapDrawable ret = null;
        
        InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), contactUri);
        
        if(is != null)
        	ret = new BitmapDrawable(is);
        
        return ret;
        
    }
	



	private String getPhoneType(int type)
	{
 		String ret;
 		
 		switch(type)
 		{
 		case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
 			ret = getString(R.string.phone_type_assistant);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
 			ret = getString(R.string.phone_type_callback);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
 			ret = getString(R.string.phone_type_car);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
 			ret = getString(R.string.phone_type_company_main);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
 			ret = getString(R.string.phone_type_fax_home);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
 			ret = getString(R.string.phone_type_fax_work);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
 			ret = getString(R.string.phone_type_home);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
 			ret = getString(R.string.phone_type_isdn);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
 			ret = getString(R.string.phone_type_main);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
 			ret = getString(R.string.phone_type_mms);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
 			ret = getString(R.string.phone_type_mobile);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
 			ret = getString(R.string.phone_type_other);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
 			ret = getString(R.string.phone_type_other_fax);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
 			ret = getString(R.string.phone_type_pager);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
 			ret = getString(R.string.phone_type_radio);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
 			ret = getString(R.string.phone_type_telex);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
 			ret = getString(R.string.phone_type_tty_tdd);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
 			ret = getString(R.string.phone_type_work);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
 			ret = getString(R.string.phone_type_mobile);
 			break;
 		case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
 			ret = getString(R.string.phone_type_work_pager);
 			break;
 		default:
 			ret = "";
 			break;
 		}		
 		
 		return ret;
	}




	
	@Override
	public void onClick(View v)
	{

		String phoneNumber = ((TextView)v.findViewById(R.id.phoneNumber)).getText().toString();
		String phoneType = ((TextView)v.findViewById(R.id.phoneType)).getText().toString();
		Log.w(LOG_TAG, "Cntact number clicked, Phone: " + phoneNumber + " Type: " + phoneType);
		
		// return data
		Intent in = new Intent();
		in.putExtra(CONTACT_ID, contactId);
		in.putExtra(CONTACT_NAME, contactName);
		in.putExtra(PHONE_NUMBER, phoneNumber);
		in.putExtra(NUMBER_TYPE, phoneType);
		setResult(RESULT_OK, in);
		finish();
		
	}




	
}

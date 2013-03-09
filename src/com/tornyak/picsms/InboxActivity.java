package com.tornyak.picsms;

import java.io.InputStream;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class InboxActivity extends Activity
{
 
    private Cursor smsCursor;
    private ListView smsView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.activity_inbox);
        Uri smsUri = Uri.parse("content://sms/inbox");
        smsCursor = getContentResolver().query(smsUri, null, null, null,null);
        bindSmses();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_inbox, menu);
        return true;
    }
    
    private void bindSmses() {
        if (smsView == null) {
            smsView = (ListView) findViewById(R.id.inboxPicsmsList);
        }
        smsView.setAdapter(new SmsCursorAdapter(this, smsCursor));
    }
    
    public void onNewPicsms(View v) {
        Intent it = new Intent(this, PicsmsActivity.class);
        startActivity(it);
    }
    
    private class SmsCursorAdapter extends CursorAdapter {

        private Cursor mCursor;
        private Context mContext;
        private final LayoutInflater mInflater;

        public SmsCursorAdapter(Context context, Cursor c) {
            super(context, c, true);
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mCursor = c;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView textView = (TextView) view; 
            String from = cursor.getString(2);
            String smsText = cursor.getString(11);
            textView.setText("From: " + from + "\n" + smsText);


        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            TextView textView = new TextView(context);
            return textView;

        }

    }
    

}

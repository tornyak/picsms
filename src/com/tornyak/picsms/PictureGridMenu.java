package com.tornyak.picsms;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.tornyak.picsms.pictures.BitmapUtils;
import com.tornyak.picsms.pictures.Category;
import com.tornyak.picsms.pictures.Picture;
import com.tornyak.picsms.pictures.PictureAction;
import com.tornyak.picsms.pictures.PictureBook;

public class PictureGridMenu {

	public static final String LOG_TAG = "PictureGridMenu";
	private Context context;
	private PictureGridMenuAdapter adapter;
	private PictureBook pb;
	private PictureMessageBar pmb;

	private int textColor;
	private float textSize;
	private float pictureWidth;

	public PictureGridMenu(Context context, PictureBook pb, GridView gridView, PictureMessageBar pmb) {
		this.context = context;
		this.pb = pb;
		this.pmb = pmb;
		adapter = new PictureGridMenuAdapter();
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new PictureGridMenuItemClickListener());

		Resources res = context.getResources();

		textColor = res.getColor(R.color.menu_icon_text);
		textSize = res.getDimension(R.dimen.grid_picture_text_size);
		pictureWidth = res.getDimension(R.dimen.grid_picture_width);
	}

	public class PictureGridMenuItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(LOG_TAG, "PictureGridMenuItemClickListener.onItemClick: position: " + position);
			Picture p = pb.setSelected(position);
			if (p != null) {
				switch (p.getType()) {
					case WORD:
						pmb.addPicture(p);
					break;
					case ACTION:
						doAction(p.getAction());
					break;
					default:
						Log.w(LOG_TAG, "Unknown picture type: " + p.getType());
					break;
				}
			}

			adapter.notifyDataSetInvalidated();
		}

	}

	private void doAction(PictureAction action) {
		if (action != null) {
			switch (action) {
				case INPUT_TEXT:
				// TODO
				break;
				case SEND_SMS:
					Intent it = new Intent(context, PhoneBookActivity.class);
					// TODO get message and put proper extra name
					it.putExtra("Message", "Hello");
					context.startActivity(it);
				break;
			}
		}

	}

	public class PictureGridMenuAdapter extends BaseAdapter {

		public int getCount() {
			return pb.getSize();
		}

		public Object getItem(int position) {
			Category activeCat = pb.getActiveCategory();
			if (activeCat != null)
				return activeCat.getPicture(position);
			else
				return pb.getCategory(position);
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			TextView textView;
			Drawable icon;

			if (convertView == null) {
				textView = new TextView(context);
			} else {
				textView = (TextView) convertView;
			}

			int resId = pb.getImageInfo(position).getResourceId();

			Bitmap b = BitmapUtils.decodeSampledBitmapFromResource(context.getResources(), resId, (int) pictureWidth, (int) pictureWidth);
			icon = new BitmapDrawable(b);

			textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
			textView.setTextColor(textColor);
			textView.setTextSize(textSize);
			textView.setGravity(Gravity.CENTER);
			textView.setText(pb.getText(position));

			return textView;
		}
	}

	public void back() {
		pb.back();
		adapter.notifyDataSetChanged();
	}
}

package com.tornyak.picsms;

import java.util.ArrayList;

import com.tornyak.picsms.pictures.BitmapUtils;
import com.tornyak.picsms.pictures.Picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PictureMessageBar {

	private final String LOG_TAG = "PictureMessageBar";
	private Context context;
	private PictureMessageBarAdapter adapter;
	private GridView gridView;
	private ArrayList<Picture> message;

	public PictureMessageBar(Context context, GridView gridView) {
		this.context = context;
		this.gridView = gridView;
		message = new ArrayList<Picture>();
		adapter = new PictureMessageBarAdapter();
		gridView.setAdapter(adapter);
	}

	public void addPicture(Picture p) {
		if (p != null) {
			message.add(p);
			adapter.notifyDataSetChanged();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
				gridView.smoothScrollToPosition(adapter.getCount());
		}
	}

	// Delete last symbol
	public void delete() {
		if (message != null && message.size() > 0) {
			message.remove(message.size() - 1);
			adapter.notifyDataSetChanged();
		}
	}

	public class PictureMessageBarAdapter extends BaseAdapter {

		public int getCount() {
			return message.size();
		}

		public Object getItem(int position) {
			return message.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(context);
				imageView.setLayoutParams(new GridView.LayoutParams(72, 72));
				// imageView.setAdjustViewBounds(false);
				// imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageView.setPadding(8, 8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}

			int resId = message.get(position).getImageInfo().getResourceId();
			Bitmap b = BitmapUtils.decodeSampledBitmapFromResource(context.getResources(), resId, 72, 72);
			imageView.setImageBitmap(b);

			return imageView;
		}
	}

}

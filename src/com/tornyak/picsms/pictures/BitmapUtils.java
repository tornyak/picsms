package com.tornyak.picsms.pictures;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

public class BitmapUtils {

	private static final String LOG_TAG = "BitmapUtils";

	private static LruCache<String, Bitmap> bitmapMemoryCache;

	public static Bitmap getBitmap(Resources res, int resId, int reqWidth, int reqHeight) {
		final String imageKey = String.valueOf(resId);

		Bitmap bitmap = getBitmapFromMemCache(imageKey);
		if (bitmap != null) {
			return bitmap;
		} else {
			return decodeSampledBitmapFromResource(res, resId, reqWidth, reqHeight);
		}
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

		Log.d(LOG_TAG, String.format("%s %d", "decodeSampledBitmapFromResource() resId =", resId));
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize1 = 1;
		int inSampleSize2 = 1;

		if (height > reqHeight || width > reqWidth) {
			// if (width > height) {
			// inSampleSize1 = Math.round((float) height / (float) reqHeight);
			// } else {
			// inSampleSize = Math.round((float) width / (float) reqWidth);
			// }
			inSampleSize1 = Math.round((float) height / (float) reqHeight);
			inSampleSize2 = Math.round((float) width / (float) reqWidth);
		}
		return (inSampleSize1 > inSampleSize2) ? inSampleSize1 : inSampleSize2;
	}

	public static void initBmpMemoryCache(int size) {
		if (bitmapMemoryCache == null) {
			bitmapMemoryCache = new LruCache<String, Bitmap>(size) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					// The cache size will be measured in bytes rather than number of items.
					return bitmap.getRowBytes() * bitmap.getHeight();
				}
			};
		}
	}

	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			bitmapMemoryCache.put(key, bitmap);
		}
	}

	public static Bitmap getBitmapFromMemCache(String key) {
		return bitmapMemoryCache.get(key);
	}

}

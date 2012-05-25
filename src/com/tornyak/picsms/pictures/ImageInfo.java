package com.tornyak.picsms.pictures;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ImageInfo {
	private final static String LOG_TAG = "ImageInfo";
	private final static String BASE_PACKAGE = "com.tornyak.picsms";

	private String imageName;
	private Drawable image;
	private int resourceId;

	public ImageInfo(String imageName, int resId) {
		setImageName(imageName);
		setResourceId(resId);
	}

	public ImageInfo(String imageName, Resources res) {
		setImageName(imageName);
		setResourceId(getImageId(imageName, res));
	}

	public static int getImageId(String imageName, Resources res) {
		int id = res.getIdentifier(imageName.substring(1), null, BASE_PACKAGE);
		Log.d(LOG_TAG, "getImageId: " + imageName + " id: " + id);
		return id;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public Drawable getDrawable() {
		return image;
	}

}

package com.tornyak.picsms.pictures;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

public class PictureBookBuilderXml {

	public static final String LOG_TAG = "PictureBookBuilderXml";

	private PictureBookBuilderXml() {
	}

	public static PictureBook buildPictureBookFromXML(Resources res, int xmlRes) throws XmlPullParserException, IOException {

		Log.d(LOG_TAG, "buildPictureBookFromXML");
		PictureBook pictureBook = null;
		Category category = null;
		Picture picture = null;
		int catId = 0;
		int picId = 0;

		boolean begBook = false;
		boolean begCategory = false;
		boolean begPicture = false;
		boolean begText = false;
		boolean begAction = false;

		XmlResourceParser xmlResParser = res.getXml(xmlRes);

		int eventType = xmlResParser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_DOCUMENT) {

				Log.d(LOG_TAG, "XmlPullParser: START_DOCUMENT");

			} else if (eventType == XmlPullParser.START_TAG) {

				String tagName = xmlResParser.getName();
				Log.d(LOG_TAG, "XmlPullParser: START_TAG " + tagName);
				if (tagName.equalsIgnoreCase("PICTUREBOOK")) {

					String name = xmlResParser.getAttributeValue(null, "name");
					pictureBook = new PictureBook(name);
					begBook = true;

				} else if (tagName.equalsIgnoreCase("CATEGORY")) {

					String name = xmlResParser.getAttributeValue(null, "name");
					String imageName = xmlResParser.getAttributeValue(null, "image");
					Log.d(LOG_TAG, "XmlPullParser: name = " + name + " image = " + imageName);
					category = new Category(catId++, name, new ImageInfo(imageName, res));
					picId = 0;
					begCategory = true;

				} else if (tagName.equalsIgnoreCase("PICTURE")) {
					String name = xmlResParser.getAttributeValue(null, "name");
					String imageName = xmlResParser.getAttributeValue(null, "image");
					Log.d(LOG_TAG, "XmlPullParser: name = " + name + " image = " + imageName);
					String type = xmlResParser.getAttributeValue(null, "type");

					PictureType picType = PictureType.WORD;
					if (type.equalsIgnoreCase("ACTION"))
						picType = PictureType.ACTION;

					int newId;
					if (begCategory)
						newId = picId++;
					else
						newId = catId++;

					picture = new Picture(newId, name, picType, new ImageInfo(imageName, res));
					begPicture = true;

				} else if (tagName.equalsIgnoreCase("TEXT")) {
					begText = true;
				} else if (tagName.equalsIgnoreCase("ACTION")) {
					begAction = true;
				}

			} else if (eventType == XmlPullParser.END_TAG) {

				String tagName = xmlResParser.getName();
				Log.d(LOG_TAG, "XmlPullParser: END_TAG " + tagName);

				if (tagName.equalsIgnoreCase("PICTURE")) {
					if (begCategory)
						category.addPicture(picture);
					else
						pictureBook.addPicture(picture, null);
					begPicture = false;

				} else if (tagName.equalsIgnoreCase("TEXT")) {
					begText = false;
				} else if (tagName.equalsIgnoreCase("ACTION")) {
					begAction = false;
				} else if (tagName.equalsIgnoreCase("CATEGORY")) {
					pictureBook.addCathegory(category);
					begCategory = false;
				} else if (tagName.equalsIgnoreCase("PICTUREBOOK")) {
					begBook = false;
				}
			} else if (eventType == XmlPullParser.TEXT) {

				String value = xmlResParser.getText();
				Log.d(LOG_TAG, "XmlPullParser: TEXT " + value);
				if (begText) {
					if (begPicture) {
						picture.setText(value);
					} else if (begCategory)
						category.setText(value);
				} else if (begAction) {
					if (value.equalsIgnoreCase("INPUT_TEXT"))
						picture.setAction(PictureAction.INPUT_TEXT);
					else if (value.equalsIgnoreCase("SEND_SMS"))
						picture.setAction(PictureAction.SEND_SMS);
				}
			}
			eventType = xmlResParser.next();
		}
		Log.d(LOG_TAG, "XmlPullParser: END_DOCUMENT");

		return pictureBook;
	}
}

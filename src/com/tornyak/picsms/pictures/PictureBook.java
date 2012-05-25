package com.tornyak.picsms.pictures;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;

import android.util.Log;

public class PictureBook {

	public static final String LOG_TAG = "PictureBook";

	private String name;
	private String language;

	private HashMap<String, Category> categories;
	private HashMap<Integer, Category> categoryIds;

	private HashMap<String, Picture> pictures;
	private HashMap<Integer, Picture> pictureIds;

	private Category activeCategory;

	public PictureBook(String name) {
		init(name);
	}

	public PictureBook() {
		init("");
	}

	private void init(String name) {
		this.name = name;
		categories = new HashMap<String, Category>();
		categoryIds = new HashMap<Integer, Category>();
		pictures = new HashMap<String, Picture>();
		pictureIds = new HashMap<Integer, Picture>();
	}

	public int getSize() {
		if (activeCategory == null)
			return categories.size() + pictures.size();
		else
			return activeCategory.getSize();

	}

	public Picture getPicture(int id) {
		if (activeCategory != null)
			return activeCategory.getPicture(id);
		else
			return pictureIds.get(id);
	}

	public ImageInfo getImageInfo(int id) {
		Log.d(LOG_TAG, String.format("%s %d", "getImageInfo() id =", id));

		ImageInfo ii = null;
		if (activeCategory != null)
			return activeCategory.getPicture(id).getImageInfo();
		else {
			Category cat = categoryIds.get(id);
			if (cat != null)
				ii = cat.getImageInfo();
			else {
				Picture pic = pictureIds.get(id);
				if (pic != null)
					ii = pictureIds.get(id).getImageInfo();
			}
		}
		return ii;
	}

	public Picture setSelected(int id) {
		Picture pic = null;
		if (activeCategory == null) {
			// category selected
			if (categoryIds.containsKey(id))
				setActiveCategory(id);
			else
				pic = pictureIds.get(id);
		} else {
			pic = activeCategory.getPicture(id);
			activeCategory = null;
		}
		return pic;
	}

	public void addCathegory(Category cat) {
		if (!categories.containsKey(cat.getName()) && !categoryIds.containsKey(cat.getId())) {
			categories.put(cat.getName(), cat);
			categoryIds.put(cat.getId(), cat);
		}
	}

	public void removeCathegory(String name) {
		Category cat = categories.get(name);
		if (cat != null) {
			categoryIds.remove(cat.getId());
		}
		categories.remove(name);
	}

	public void removeCathegory(int id) {
		Category cat = categoryIds.get(id);
		if (cat != null) {
			categories.remove(cat.getName());
		}
		categoryIds.remove(id);
	}

	public Category getCathegory(String name) {
		return categories.get(name);
	}

	public Category getCategory(int id) {
		return categoryIds.get(id);
	}

	public String getName() {
		return name;
	}

	public void addPicture(Picture p, String catName) {
		Category cat = categories.get(catName);
		if (cat != null)
			cat.addPicture(p);
		else {
			pictureIds.put(p.getId(), p);
			pictures.put(p.getName(), p);
		}
	}

	public void addPicture(Picture p, int catId) {
		Category cat = categoryIds.get(catId);
		if (cat != null)
			cat.addPicture(p);
		else {
			pictureIds.put(p.getId(), p);
			pictures.put(p.getName(), p);
		}
	}

	public Picture getPicture(String picName, String catName) {
		Category cat = categories.get(catName);
		if (cat != null)
			return cat.getPicture(picName);
		else
			return pictures.get(picName);
	}

	public Picture getPicture(int picId, int catId) {
		Category cat = categoryIds.get(catId);
		if (cat != null)
			return cat.getPicture(picId);
		else
			return pictureIds.get(picId);
	}

	public Category getActiveCategory() {
		return activeCategory;
	}

	public void setActiveCategory(Category cat) {
		if (categories.containsKey(cat.getName())) {
			activeCategory = cat;
			Log.d(LOG_TAG, "setActiveCategory: " + activeCategory.getName());
		}
	}

	public void setActiveCategory(int catId) {
		if (categoryIds.containsKey(catId)) {
			activeCategory = categoryIds.get(catId);
			Log.d(LOG_TAG, "setActiveCategory: " + activeCategory.getName());
		}
	}

	public void setActiveCategory(String cat) {
		if (categories.containsKey(cat)) {
			activeCategory = categories.get(cat);
			Log.d(LOG_TAG, "setActiveCategory: " + activeCategory.getName());
		}
	}

	public void back() {
		if (activeCategory != null)
			activeCategory = null;
	}

	public String getText(int id) {

		String text = "";

		if (activeCategory == null) {
			Category cat = categoryIds.get(id);
			if (cat != null)
				text = cat.getText();
			else {
				Picture pic = pictureIds.get(id);
				if (pic != null)
					text = pic.getText();
			}
		} else
			text = activeCategory.getPicture(id).getText();

		return text;
	}

	public void toXmlStream(BufferedWriter writer) throws IOException {

		writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		writer.write("<pictureBook name=\"");
		writer.write(name);
		writer.write("\"\nlanguage=\"");
		writer.write(language);
		writer.write("\"\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		writer.write("xsi:noNamespaceSchemaLocation=\"picsms.xsd\">\n");

		for (Category c : categories.values())
			c.toXmlStream(writer);

		writer.write("</pictureBook>/n");
		writer.flush();
		writer.close();

	}
}

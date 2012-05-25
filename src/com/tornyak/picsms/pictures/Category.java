package com.tornyak.picsms.pictures;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;


public class Category {
	private int id;
	private String name;
	private String text;
	private ImageInfo imageInfo;

	private HashMap<String, Picture> pictures;
	private HashMap<Integer, Picture> pictureIds;

	public Category(int id, String name, String text, ImageInfo imageInfo) {
		init(id, name, text, imageInfo);
	}

	public Category(int id, String name, ImageInfo imageInfo) {
		init(id, name, "", imageInfo);
	}

	private void init(int id, String name, String text, ImageInfo imageInfo) {
		this.id = id;
		this.name = name;
		this.text = text;
		this.imageInfo = imageInfo;
		pictures = new HashMap<String, Picture>();
		pictureIds = new HashMap<Integer, Picture>();
	}

	public void addPicture(Picture pic) {
		if (!pictures.containsKey(pic.getName()) && !pictureIds.containsKey(pic.getId())) {
			pictures.put(pic.getName(), pic);
			pictureIds.put(pic.getId(), pic);
		}
	}

	public void removePicture(String name) {
		Picture pic = pictures.get(name);
		if (pic != null) {
			pictureIds.remove(pic.getId());
		}
		pictures.remove(name);
	}

	public void removePicture(int id) {
		Picture pic = pictureIds.get(id);
		if (pic != null) {
			pictures.remove(pic.getName());
		}
		pictureIds.remove(id);
	}

	public Picture getPicture(String name) {
		return pictures.get(name);
	}

	public Picture getPicture(int id) {
		return pictureIds.get(id);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ImageInfo getImageInfo() {
		return imageInfo;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSize() {
		return pictures.size();
	}

	public void toXmlStream(BufferedWriter writer) throws IOException {
		writer.write("<cathegory name=\"");
		writer.write(name);
		writer.write("\"\nimage=\"");
		writer.write(imageInfo.getImageName());
		writer.write("\">\n");
		writer.write("<text>");
		writer.write(text);
		writer.write("</text>");

		for (Picture p : pictures.values())
			p.toXmlStream(writer);

		writer.write("</cathegory>\n");
		writer.flush();
	}
}

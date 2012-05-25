package com.tornyak.picsms.pictures;

import java.io.BufferedWriter;
import java.io.IOException;

public class Picture {

	private int id;
	private String name;
	private PictureType type;
	private String text;
	private PictureAction action;
	ImageInfo imageInfo;

	public Picture(int id, String name, String text, PictureType type, ImageInfo imageInfo) {
		init(id, name, text, type, imageInfo);
	}

	public Picture(int id, String name, PictureType type, ImageInfo imageInfo) {
		init(id, name, "", type, imageInfo);
	}

	private void init(int id, String name, String text, PictureType type, ImageInfo imageInfo) {
		this.name = name;
		this.text = text;
		this.type = type;
		this.id = id;
		this.imageInfo = imageInfo;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setAction(PictureAction action) {
		this.action = action;
	}

	public PictureAction getAction() {
		return action;
	}

	public String toString() {
		return text;
	}

	public PictureType getType() {
		return type;
	}

	public ImageInfo getImageInfo() {
		return imageInfo;
	}

	public String toXmlString() {
		StringBuilder sb = new StringBuilder(100);

		sb.append("<picture name=\"");
		sb.append(name);
		sb.append("\"\ntype=\"");
		sb.append(type);
		sb.append("\"\nimage=\"");
		sb.append(imageInfo.getImageName());
		sb.append("\">\n<text>");
		sb.append(text);
		sb.append("</text>");

		sb.append("</picture>\n");

		return sb.toString();
	}

	public void toXmlStream(BufferedWriter writer) throws IOException {
		writer.write(toXmlString());
		writer.flush();
	}
}

package com.tornyak.picsms;

import android.graphics.drawable.Drawable;

public class PhoneNumberInfo {
	String contactName;
	Drawable contactPhoto;
	long contactId;
	String phoneNumber;
	String phoneType;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof PhoneNumberInfo)) {
			return false;
		}

		PhoneNumberInfo that = (PhoneNumberInfo) o;
		return contactName.equals(that.contactName) && contactId == that.contactId && phoneNumber.equals(that.phoneNumber) && phoneType.equals(that.phoneType);
	}

	@Override
	public int hashCode() {
		int result;
		result = (contactName != null ? contactName.hashCode() : 0);
		final String name = PhoneNumberInfo.class.getName();
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Name: " + contactName.toString() + " Phone: " + phoneNumber;
	}

}

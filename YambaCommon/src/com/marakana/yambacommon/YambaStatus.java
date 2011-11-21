package com.marakana.yambacommon;

import android.os.Parcel;
import android.os.Parcelable;

public class YambaStatus implements Parcelable {
	public long createdAt;
	public long id;
	public String text;
	public String user;

	/** Creates YambaStatus from Parcel */
	public YambaStatus(Parcel parcel) {
		createdAt = parcel.readLong();
		id = parcel.readLong();
		text = parcel.readString();
		user = parcel.readString();
	}

	/** Writes state of YambaStatus to Parcel */
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(createdAt);
		parcel.writeLong(id);
		parcel.writeString(text);
		parcel.writeString(user);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<YambaStatus> CREATOR = new Parcelable.Creator<YambaStatus>() {

		@Override
		public YambaStatus createFromParcel(Parcel source) {
			return new YambaStatus(source);
		}

		@Override
		public YambaStatus[] newArray(int size) {
			return new YambaStatus[size];
		}

	};

}

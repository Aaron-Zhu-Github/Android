package com.picture.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Photo details
 */
public class Album implements Parcelable {
    /**
     * Picture path
     */
    private String path;
    /**
     * Date (e.g. November 21, 2021)
     */
    private long date;
    /**
     * Is it selected
     */
    private boolean checked;
    /**
     * Location
     */
    private int position;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * file name
     */
    private String location;

    public Album() {

    }

    public Album(long date) {
        this.date = date;
    }

    protected Album(Parcel in) {
        path = in.readString();
        date = in.readLong();
        checked = in.readByte() != 0;
        fileName = in.readString();
        location = in.readString();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeLong(date);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeString(fileName);
        dest.writeString(location);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Album) {
            Album album = (Album) obj;
            return album.path.equals(path);
        }
        return super.equals(obj);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

package com.picture.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Album date categories
 */
public class TimeAlbum implements Parcelable {
    /**
     * Date
     */
    private long date;
    /**
     * Path
     */
    private String path;
    /**
     * List of photos below the target date
     */
    private List<Album> albums = new ArrayList<>();
    /**
     * Whether selected
     */
    private boolean checked;
    /**
     * Child node is selected
     */
    private boolean existSelected;
    /**
     * Is it a character classification
     */
    private boolean figureTag;
    /**
     * Category Name
     */
    private String type;
    /**
     * quantity
     */
    private int count;

    public TimeAlbum() {

    }

    public TimeAlbum(long date) {
        this.date = date;
    }

    protected TimeAlbum(Parcel in) {
        date = in.readLong();
        albums = in.createTypedArrayList(Album.CREATOR);
        checked = in.readByte() != 0;
    }

    public static final Creator<TimeAlbum> CREATOR = new Creator<TimeAlbum>() {
        @Override
        public TimeAlbum createFromParcel(Parcel in) {
            return new TimeAlbum(in);
        }

        @Override
        public TimeAlbum[] newArray(int size) {
            return new TimeAlbum[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TimeAlbum) {
            TimeAlbum ta = (TimeAlbum) obj;
            return this.date == ta.date;
        }
        return super.equals(obj);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(date);
        dest.writeTypedList(albums);
        dest.writeByte((byte) (checked ? 1 : 0));
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isExistSelected() {
        return existSelected;
    }

    public void setExistSelected(boolean existSelected) {
        this.existSelected = existSelected;
    }

    public boolean isFigureTag() {
        return figureTag;
    }

    public void setFigureTag(boolean figureTag) {
        this.figureTag = figureTag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

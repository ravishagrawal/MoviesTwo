package com.example.android.moviestwo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hp on 10-09-2017.
 */

public class VideoStructure implements Parcelable {

    public String site, key, name;

    public VideoStructure() {}

    public VideoStructure(String site, String key, String name) {
        this.site = site;
        this.key = key;
        this.name = name;
    }

    public VideoStructure(Parcel in) {
        site = in.readString();
        key = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(site);
        parcel.writeString(key);
        parcel.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<VideoStructure> CREATOR = new Parcelable.Creator<VideoStructure>() {
        @Override
        public VideoStructure createFromParcel(Parcel in) {
            return new VideoStructure(in);
        }

        @Override
        public VideoStructure[] newArray(int size) {
            return new VideoStructure[size];
        }
    };
}


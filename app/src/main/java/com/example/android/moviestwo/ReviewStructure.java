package com.example.android.moviestwo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hp on 10-09-2017.
 */

public class ReviewStructure implements Parcelable {

    public String remoteId, author, content;

    public ReviewStructure() {}

    public ReviewStructure(String remoteId, String author, String content) {
        this.remoteId = remoteId;
        this.author = author;
        this.content = content;
    }

    public ReviewStructure(Parcel in) {
        remoteId = in.readString();
        author = in.readString();
        content = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(remoteId);
        parcel.writeString(author);
        parcel.writeString(content);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ReviewStructure> CREATOR = new Parcelable.Creator<ReviewStructure>() {
        @Override
        public ReviewStructure createFromParcel(Parcel in) {
            return new ReviewStructure(in);
        }

        @Override
        public ReviewStructure[] newArray(int size) {
            return new ReviewStructure[size];
        }
    };
}


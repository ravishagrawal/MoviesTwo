package com.example.android.moviestwo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hp on 10-09-2017.
 */

public class MovieStructure implements Parcelable {

    public String mId;
    public String mTitle;
    public String mPosterLink;
    public String mSynopsis;
    public double mRating;
    public String mReleaseDate;
    public int mRuntime;

    public MovieStructure() {}

    public MovieStructure(String remoteId, String title, String posterUrl, String synopsis, double userRating, String releaseDate, int runtime) {
        this.mId = remoteId;
        this.mTitle = title;
        this.mPosterLink = posterUrl;
        this.mSynopsis = synopsis;
        this.mRating = userRating;
        this.mReleaseDate = releaseDate;
        this.mRuntime = runtime;
    }

    public MovieStructure(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mPosterLink = in.readString();
        mSynopsis = in.readString();
        mRating = in.readDouble();
        mReleaseDate = in.readString();
        mRuntime = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mPosterLink);
        parcel.writeString(mSynopsis);
        parcel.writeDouble(mRating);
        parcel.writeString(mReleaseDate);
        parcel.writeInt(mRuntime);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieStructure> CREATOR = new Parcelable.Creator<MovieStructure>() {
        @Override
        public MovieStructure createFromParcel(Parcel in) {
            return new MovieStructure(in);
        }

        @Override
        public MovieStructure[] newArray(int size) {
            return new MovieStructure[size];
        }
    };
}


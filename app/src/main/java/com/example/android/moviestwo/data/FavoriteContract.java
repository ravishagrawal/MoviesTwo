package com.example.android.moviestwo.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by hp on 10-09-2017.
 */

public class FavoriteContract {

    public static final String AUTHORITY = "com.example.android.moviestwo";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoriteEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_URL = "posterLink";
        public static final String COLUMN_TIMESTAMP = "created_at";
    }

}

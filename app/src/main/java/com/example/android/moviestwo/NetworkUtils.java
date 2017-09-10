package com.example.android.moviestwo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by hp on 10-09-2017.
 */

public class NetworkUtils {

    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    public final static String POPULAR_URL = "/movie/popular";
    public final static String TOP_RATED_URL = "/movie/top_rated";
    public final static String MOVIE_DETAIL_URL = "/movie/{id}";
    public final static String TRAILERS_URL = "/movie/{id}/videos";
    public final static String REVIEWS_URL = "/movie/{id}/reviews";
    public final static String THUMBNAIL_BASE_URL = "http://image.tmdb.org/t/p";

    private final static String API_KEY_QUERY = "api_key";

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    protected static URL buildCommonApiUrl(Context context, String urlString) {
        Uri uri = Uri.parse(urlString).buildUpon()
                .appendQueryParameter(API_KEY_QUERY, context.getString(R.string.API_KEY))
                .build();
        URL url = null;

        try {
            url = new URL(uri.toString());
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL getPopularUrl(Context context) {
        return buildCommonApiUrl(context, API_BASE_URL + POPULAR_URL);
    }

    public static URL getTopRatedUrl(Context context) {
        return buildCommonApiUrl(context, API_BASE_URL + TOP_RATED_URL);
    }

    public static URL getMovieDetailUrl(Context context, String id) {
        String urlString = API_BASE_URL + MOVIE_DETAIL_URL;
        urlString = urlString.replace("{id}", id);
        return buildCommonApiUrl(context, urlString);
    }

    public static URL getTrailersUrl(Context context, String id) {
        String urlString = API_BASE_URL + TRAILERS_URL;
        urlString = urlString.replace("{id}", id);
        return buildCommonApiUrl(context, urlString);
    }

    public static URL getReviewsUrl(Context context, String id) {
        String urlString = API_BASE_URL + REVIEWS_URL;
        urlString = urlString.replace("{id}", id);
        return buildCommonApiUrl(context, urlString);
    }

}


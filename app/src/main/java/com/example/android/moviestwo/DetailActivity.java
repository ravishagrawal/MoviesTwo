package com.example.android.moviestwo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviestwo.data.FavoriteContract;
import com.example.android.moviestwo.data.FavoriteContract.FavoriteEntry;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.android.moviestwo.data.FavoriteContract.FavoriteEntry.COLUMN_POSTER_URL;
import static com.example.android.moviestwo.data.FavoriteContract.FavoriteEntry.COLUMN_ID;
import static com.example.android.moviestwo.data.FavoriteContract.FavoriteEntry.COLUMN_TITLE;

public class DetailActivity extends AppCompatActivity {

    public static final String MOVIE_KEY = "movie";
    public static final String TRAILERS_KEY = "trailers";
    public static final String REVIEWS_KEY = "reviews";

    private ScrollView mDetailContent;
    private TextView mErrorTextView, mTrailerHeadingTextView, mReviewHeadingTextView;
    private Button mErrorTryAgainButton;
    private ProgressBar mLoadingIndicator;
    private MovieStructure mMovieStructure;
    private VideoStructure[] mVideos;
    private ReviewStructure[] mReviews;
    private boolean mDetailFlag, mTrailerFlag, mReviewFlag;
    private String mRemoteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle(R.string.movie_detail);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mDetailContent = (ScrollView) findViewById(R.id.sv_detail_content);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_detail);
        mErrorTextView = (TextView) findViewById(R.id.tv_detail_error);
        mErrorTryAgainButton = (Button) findViewById(R.id.btn_try_again);
        mTrailerHeadingTextView = (TextView) findViewById(R.id.trailer_heading);
        mReviewHeadingTextView = (TextView) findViewById(R.id.tv_review_heading);

        mErrorTryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDetail(mRemoteId);
            }
        });

        mDetailFlag = false;
        mTrailerFlag = false;
        mReviewFlag = false;

        showLoadingIndicator();

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if(intent.hasExtra(Intent.EXTRA_TEXT)) {
                mRemoteId = intent.getStringExtra(Intent.EXTRA_TEXT);
                loadDetail(mRemoteId);
            }
            else {
                Toast.makeText(this, R.string.please_supply_id, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MOVIE_KEY, mMovieStructure);
        outState.putParcelableArray(TRAILERS_KEY, mVideos);
        outState.putParcelableArray(REVIEWS_KEY, mReviews);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        MovieStructure data = (MovieStructure) savedInstanceState.getParcelable(MOVIE_KEY);
        VideoStructure[] videos = (VideoStructure[]) savedInstanceState.getParcelableArray(TRAILERS_KEY);
        ReviewStructure[] reviews = (ReviewStructure []) savedInstanceState.getParcelableArray(REVIEWS_KEY);

        mDetailFlag = true;
        mTrailerFlag = true;
        mReviewFlag = true;
        mRemoteId = data.mId;

        setMovieStructure(data);
        setTrailersData(videos);
        setReviewsData(reviews);

        showDetailContentView(true);

        super.onRestoreInstanceState(savedInstanceState);
    }

    private boolean isFavorited() {
        Cursor result = getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                null, FavoriteEntry.COLUMN_ID + " = ?", new String[] {mMovieStructure.mId}, null);

        return result.getCount() > 0;
    }

    private void populateViews() {
        TextView title = (TextView) findViewById(R.id.textview_movie_title);
        TextView synopsis = (TextView) findViewById(R.id.textView_synopsis);
        TextView userRating = (TextView) findViewById(R.id.textview_ratings);
        TextView year = (TextView) findViewById(R.id.textview_release_date);
        TextView runtime = (TextView) findViewById(R.id.textview_duration);
        ImageView poster = (ImageView) findViewById(R.id.imageview_poster);

        Picasso.with(this).load(mMovieStructure.mPosterLink).error(R.drawable.ic_image_black_24dp).into(poster);
        title.setText(mMovieStructure.mTitle);
        synopsis.setText(mMovieStructure.mSynopsis);
        userRating.setText(String.format(getString(R.string.rating_formatted), String.valueOf(mMovieStructure.mRating)));
        year.setText(mMovieStructure.mReleaseDate.substring(0, mMovieStructure.mReleaseDate.indexOf('-')));
        runtime.setText(String.format(getString(R.string.runtime_formatted), String.valueOf(mMovieStructure.mRuntime)));
    }

    public void markAsFavorite(View v) {
        ImageButton favoriteButton = (ImageButton) findViewById(R.id.btn_mark_favorite);

        if(isFavorited()) {
            String remoteId = mMovieStructure.mId;
            Uri uri = FavoriteContract.FavoriteEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(remoteId).build();

            getContentResolver().delete(uri, COLUMN_ID + " = ?", new String[] {mMovieStructure.mId});
            favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);

            Toast.makeText(this, R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
        }
        else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, mMovieStructure.mId);
            values.put(COLUMN_TITLE, mMovieStructure.mTitle);
            values.put(COLUMN_POSTER_URL, mMovieStructure.mPosterLink);

            Uri uri = getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, values);

            if(uri != null)
            {
                favoriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                Toast.makeText(this, R.string.marked_as_favorite, Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDetail(String id) {
        mDetailFlag = false;
        mTrailerFlag = false;
        mReviewFlag = false;

        showLoadingIndicator();
        new FetchDetailsTask().execute(id);
        new FetchTrailersTask().execute(id);
        new FetchReviewsTask().execute(id);
    }

    private void setMovieStructure(MovieStructure data) {
        mMovieStructure = data;
    }

    private void setTrailersData(VideoStructure[] data) {
        mVideos = data;



        if(data == null || data.length == 0) {
            mTrailerHeadingTextView.setVisibility(View.GONE);

        }
        else {
            mTrailerHeadingTextView.setVisibility(View.VISIBLE);


            for(int i = 0; i < data.length; ++i) {


                final VideoStructure structure = data[i];
                TextView trailerName = (TextView) findViewById(R.id.tv_trailer_name_1);

                trailerName.setText(structure.name);
                trailerName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(structure.site != null && structure.site.toLowerCase().equals("youtube")) {
                            String youtubeUrl = "https://youtube.com/watch?v=" + structure.key;
                            Uri uri = Uri.parse(youtubeUrl);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                            if(intent.resolveActivity(getPackageManager()) != null)
                                startActivity(intent);
                        }
                        else Toast.makeText(DetailActivity.this, R.string.type_not_supported, Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }
    }

    private void setReviewsData(ReviewStructure[] data) {
        mReviews = data;



        if(data == null || data.length == 0) {
            mReviewHeadingTextView.setVisibility(View.GONE);

        }
        else {
            mReviewHeadingTextView.setVisibility(View.VISIBLE);


            for(int i = 0; i < data.length; ++i) {

                final ReviewStructure structure = data[i];
                TextView tvAuthor = (TextView) findViewById(R.id.tv_review_author_1);
                TextView tvContent = (TextView)findViewById(R.id.tv_review_content_1);

                tvAuthor.setText(structure.author);
                tvContent.setText(structure.content);

            }
        }
    }

    private void showDetailContentView() {
        ImageButton favoriteButton = (ImageButton) findViewById(R.id.btn_mark_favorite);

        if(isFavorited())
            favoriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        mErrorTryAgainButton.setVisibility(View.INVISIBLE);
        mDetailContent.setVisibility(View.VISIBLE);
    }

    private void showDetailContentView(boolean onlyWhenReady) {
        if(onlyWhenReady && mTrailerFlag && mDetailFlag && mReviewFlag)
        {
            populateViews();
            showDetailContentView();
        }
        else if(!onlyWhenReady)
            showDetailContentView();
    }

    private void showErrorMessage() {
        mErrorTextView.setText(R.string.error_message);

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mDetailContent.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
        mErrorTryAgainButton.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String message, boolean withButton) {
        mErrorTextView.setText(message);

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mDetailContent.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
        mErrorTryAgainButton.setVisibility(withButton ? View.VISIBLE : View.INVISIBLE);
    }

    private void showLoadingIndicator() {
        mDetailContent.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchDetailsTask extends AsyncTask<String, Void, MovieStructure> {

        private boolean mNoConnection;

        @Override
        protected MovieStructure doInBackground(String... strings) {
            if(!NetworkUtils.isOnline(DetailActivity.this) || strings.length == 0)
            {
                mNoConnection = true;
                return null;
            }

            String id = strings[0];
            URL url = NetworkUtils.getMovieDetailUrl(DetailActivity.this, id);
            MovieStructure movie = new MovieStructure();

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String jsonString = GeneralUtils.readInputStream(urlConnection.getInputStream());

                JSONObject data = new JSONObject(jsonString);
                movie.mTitle = data.getString("original_title");
                movie.mSynopsis = data.getString("overview");
                movie.mId = String.valueOf(data.getInt("id"));
                movie.mRating = data.getDouble("vote_average");
                movie.mReleaseDate = data.getString("release_date");
                movie.mPosterLink = NetworkUtils.THUMBNAIL_BASE_URL + "/w185" + data.getString("poster_path");
                movie.mRuntime = data.getInt("runtime");
            }
            catch(IOException | JSONException e) {
                e.printStackTrace();
            }

            return movie;
        }

        @Override
        protected void onPostExecute(MovieStructure data) {
            if(data != null)
            {
                mDetailFlag = true;
                showDetailContentView(true);
                setMovieStructure(data);
            }
            else if(mNoConnection)
                showErrorMessage(getString(R.string.no_internet_connection), true);
            else showErrorMessage();
        }
    }

    public class FetchTrailersTask extends AsyncTask<String, Void, VideoStructure[]> {

        private boolean mNoConnection;

        @Override
        protected VideoStructure[] doInBackground(String... strings) {
            if(!NetworkUtils.isOnline(DetailActivity.this) || strings.length == 0)
            {
                mNoConnection = true;
                return null;
            }

            String id = strings[0];
            URL url = NetworkUtils.getTrailersUrl(DetailActivity.this, id);
            VideoStructure[] trailers = null;

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String jsonString = GeneralUtils.readInputStream(urlConnection.getInputStream());

                JSONObject root = new JSONObject(jsonString);
                JSONArray results = root.getJSONArray("results");

                trailers = new VideoStructure[results.length()];

                for(int i = 0; i < results.length(); ++i) {
                    JSONObject data = results.getJSONObject(i);
                    trailers[i] = new VideoStructure();
                    trailers[i].site = data.getString("site");
                    trailers[i].key = data.getString("key");
                    trailers[i].name = data.getString("name");
                }
            }
            catch(IOException | JSONException e) {
                e.printStackTrace();
            }

            return trailers;
        }

        @Override
        protected void onPostExecute(VideoStructure[] data) {
            if(mNoConnection)
                showErrorMessage(getString(R.string.no_internet_connection), true);
            else {
                mTrailerFlag = true;
                showDetailContentView(true);
                setTrailersData(data);
            }
        }
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, ReviewStructure[]> {

        private boolean mNoConnection;

        @Override
        protected ReviewStructure[] doInBackground(String... strings) {
            if(!NetworkUtils.isOnline(DetailActivity.this) || strings.length == 0)
            {
                mNoConnection = true;
                return null;
            }

            String id = strings[0];
            URL url = NetworkUtils.getReviewsUrl(DetailActivity.this, id);
            ReviewStructure[] reviews = null;

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String jsonString = GeneralUtils.readInputStream(urlConnection.getInputStream());

                JSONObject root = new JSONObject(jsonString);
                JSONArray results = root.getJSONArray("results");
                int count = root.getInt("total_results");

                if(count > 0) {
                    reviews = new ReviewStructure[results.length()];

                    for(int i = 0; i < results.length(); ++i) {
                        JSONObject data = results.getJSONObject(i);
                        reviews[i] = new ReviewStructure();
                        reviews[i].remoteId = data.getString("id");
                        reviews[i].author = data.getString("author");
                        reviews[i].content = data.getString("content");
                    }
                }
            }
            catch(IOException | JSONException e) {
                e.printStackTrace();
            }

            return reviews;
        }

        @Override
        protected void onPostExecute(ReviewStructure[] data) {
            if(mNoConnection)
                showErrorMessage(getString(R.string.no_internet_connection), true);
            else {
                mReviewFlag = true;
                showDetailContentView(true);
                setReviewsData(data);
            }
        }
    }
}

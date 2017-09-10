package com.example.android.moviestwo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by hp on 10-09-2017.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MovieViewHolder> {

    private MovieStructure[] mMovieData;
    private Context mContext;
    final private ImageAdapterOnClickHandler mOnClickHandler;

    public ImageAdapter(Context context, ImageAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mOnClickHandler = onClickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_list_item, parent, false);
        return new MovieViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(mMovieData[position]);
    }

    @Override
    public int getItemCount() {
        return mMovieData == null ? 0 : mMovieData.length;
    }

    public void setData(MovieStructure[] data) {
        mMovieData = data;
        notifyDataSetChanged();
    }

    public MovieStructure[] getData() {
        return mMovieData;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Context mParentContext;
        private ImageView mThumbnail;

        public MovieViewHolder(Context context, View itemView) {
            super(itemView);
            mParentContext = context;
            mThumbnail = (ImageView) itemView.findViewById(R.id.iv_movie_thumbnail);
            itemView.setOnClickListener(this);
        }

        public void bind(MovieStructure movie) {
            Picasso.with(mParentContext).load(movie.mPosterLink).error(R.drawable.ic_image_black_24dp).into(mThumbnail);
        }

        @Override
        public void onClick(View view) {
            mOnClickHandler.onClick(mMovieData[getAdapterPosition()]);
        }
    }

    interface ImageAdapterOnClickHandler {
        void onClick(MovieStructure structure);
    }
}


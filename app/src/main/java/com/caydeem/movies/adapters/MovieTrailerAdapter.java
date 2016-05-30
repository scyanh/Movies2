package com.caydeem.movies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caydeem.movies.R;
import com.caydeem.movies.holders.MovieTrailerHolder;
import com.caydeem.movies.models.Trailer;

import java.util.List;

/**
 * Created by kunaljaggi on 4/29/16.
 */
public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerHolder> {

    private Context mContext;
    private List<Trailer> mMovieTrailerList;

    public MovieTrailerAdapter(List<Trailer> trailers, Context context) {
        mMovieTrailerList = trailers;
        this.mContext = context;
    }

    @Override
    public MovieTrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_item, parent, false);

        return new MovieTrailerHolder(itemView, mContext);
    }

    @Override
    public void onBindViewHolder(MovieTrailerHolder holder, int position) {
        Trailer movieTrailer = mMovieTrailerList.get(position);
        holder.getmVideoTitle().setText(movieTrailer.getmName());
        holder.getmVideoKey().setText(movieTrailer.getmKey());
    }

    @Override
    public int getItemCount() {
        if (mMovieTrailerList == null) {
            return 0;
        } else
            return mMovieTrailerList.size();
    }

    public List<Trailer> getmMovieTrailerList() {
        return mMovieTrailerList;
    }

    public void setmMovieTrailerList(List<Trailer> mMovieTrailerList) {
        this.mMovieTrailerList = mMovieTrailerList;
    }
}
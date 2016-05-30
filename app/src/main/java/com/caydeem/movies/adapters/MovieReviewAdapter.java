package com.caydeem.movies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caydeem.movies.R;
import com.caydeem.movies.holders.MovieReviewHolder;
import com.caydeem.movies.models.MovieReview;

import java.util.List;

/**
 * Created by kunaljaggi on 4/29/16.
 */
public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewHolder> {

    private Context mContext;
    private List<MovieReview> mMovieReviewList;

    public MovieReviewAdapter(List<MovieReview> reviews) {
        mMovieReviewList = reviews;
    }


    @Override
    public MovieReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);

        return new MovieReviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieReviewHolder holder, int position) {
        MovieReview movieReview = mMovieReviewList.get(position);
        holder.getmAuthor().setText(movieReview.getmAuthor());
        holder.getmContent().setText(movieReview.getmContent());
    }

    @Override
    public int getItemCount() {

        if (mMovieReviewList == null)
            return 0;
        else {
            return mMovieReviewList.size();
        }
    }

    public List<MovieReview> getmMovieReviewList() {
        return mMovieReviewList;
    }

    public void setmMovieReviewList(List<MovieReview> mMovieReviewList) {
        this.mMovieReviewList = mMovieReviewList;
    }
}
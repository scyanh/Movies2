package com.caydeem.movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.caydeem.movies.R;
import com.caydeem.movies.models.Movie;
import com.caydeem.movies.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {

    private Context mContext;
    private List<Movie> mMovieList;

    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
        mContext = context;
        mMovieList = movies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.gallery_item, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
        Picasso.with(mContext)
                //.load(Constants.MOVIE_DB_POSTER_URL + Constants.POSTER_PHONE_SIZE + mMovieList.get(position).getPosterPath())
                .load(Constants.MOVIE_DB_POSTER_URL + Constants.POSTER_PHONE_SIZE + mMovieList.get(position).getmPosterPath())
                .placeholder(R.drawable.poster_placeholder) // support download placeholder
                .error(R.drawable.poster_placeholder_error) //support error placeholder, if back-end returns empty string or null
                .into(imageView);
        return convertView;
    }
}
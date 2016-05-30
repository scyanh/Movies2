package com.caydeem.movies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.caydeem.movies.R;
import com.caydeem.movies.event.DiscoverMovieEvent;
import com.caydeem.movies.event.MovieEvent;
import com.caydeem.movies.event.MovieReviewsEvent;
import com.caydeem.movies.event.MovieTrailersEvent;
import com.caydeem.movies.event.ReviewEvent;
import com.caydeem.movies.event.TrailerEvent;
import com.caydeem.movies.models.MovieReview;
import com.caydeem.movies.models.Trailer;

import java.util.List;

/**
 * Created by kunaljaggi on 4/15/16.
 */
public class Utility {
    public static String getPreferredSortingCriteria(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_order_default));

    }

    public static DiscoverMovieEvent produceDiscoverMovieEvent(String queryParam) {
        return new DiscoverMovieEvent(queryParam);
    }

    public static MovieEvent produceMovieEvent() {
        return new MovieEvent();
    }

    public static MovieReviewsEvent produceMovieReviewsEvent(final int movieId) {
        return new MovieReviewsEvent(movieId);
    }

    public static ReviewEvent produceReviewEvent(final List<MovieReview> mReviewList) {
        return new ReviewEvent(mReviewList);
    }

    public static MovieTrailersEvent produceMovieTrailersEvent(final int movieId) {
        return new MovieTrailersEvent(movieId);
    }

    public static TrailerEvent produceTrailerEvent(final List<Trailer> mTrailerList) {
        return new TrailerEvent(mTrailerList);
    }

}

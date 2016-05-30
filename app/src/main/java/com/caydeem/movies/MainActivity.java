package com.caydeem.movies;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.caydeem.movies.fragments.MovieDetailFragment;
import com.caydeem.movies.models.Movie;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private  static String TAG="MainActivity";
    public static String MOVIEFRAGMENT_TAG = "MFTAG";

    private static Bus mEventBus;
    public static List<Movie> mMovieList;
    public static boolean movies_loaded;
    public static boolean mTwoPane;

    public static Movie selectedMovie;

    public static Bus getEventBus() {

        if (mEventBus == null) {
            mEventBus = new com.squareup.otto.Bus();
        }

        return mEventBus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "zz in onCreate");

        setContentView(R.layout.activity_main);

        movies_loaded=false;
        if(savedInstanceState != null) {
            Log.d(TAG, "zz on savedInstanceState");
            movies_loaded=true;
            MainActivity.mMovieList = savedInstanceState.getParcelableArrayList("movies");
        }

        if (findViewById(R.id.movie_details_container) != null) {
            Log.d(TAG, "zz movie_details_container finded");
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new MovieDetailFragment(), MOVIEFRAGMENT_TAG)
                        .commit();
        } else {
            mTwoPane = false;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "zz in onSaveInstanceState");
        outState.putParcelableArrayList("movies", (ArrayList<? extends Parcelable>) mMovieList);
    }


    /*}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        movies_loaded=false;
        int id = item.getItemId();
        if (id == R.id.action_order_by_popular) {
            discoverMovies("popularity.desc", movies_loaded);
            return true;
        }
        if (id == R.id.action_order_by_rate) {
            discoverMovies("vote_average.desc", movies_loaded);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }*/


}

package com.caydeem.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.caydeem.movies.fragments.MovieDetailFragment;

public class MovieDetailActivity extends AppCompatActivity {

    private static String TAG="MDetailActivity";

    private TextView movieTitle;
    private ImageView moviePoster;
    private  TextView movieReleaseYear;
    private  TextView movieRating;
    private TextView movieOverview;

    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private MovieDetailFragment mDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        /*
        movieTitle=(TextView)findViewById(R.id.movieTitle);
        moviePoster=(ImageView)findViewById(R.id.moviePoster);
        movieReleaseYear=(TextView)findViewById(R.id.movieReleaseYear);
        movieRating=(TextView)findViewById(R.id.movieRating);
        movieOverview=(TextView)findViewById(R.id.movieOverview);
        */

        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();

        /*if (intent != null && intent.hasExtra("selectedMovie")) {
            Log.i(TAG, "zz intent with Extra selectedMovie");
            MainActivity.selectedMovie = intent.getParcelableExtra("selectedMovie");
            */

            if (MainActivity.selectedMovie != null) {
                Log.i(TAG, "zz with movie");
                Log.i(TAG, "zz selectedMovie.getmTitle()=" + MainActivity.selectedMovie.getmTitle());
            }else{
                Log.i(TAG, "zz NO movie");
            }

        /*
        }else{
            Log.i(TAG, "zz no intent with Extra selectedMovie");
        }*/

        FragmentManager fm = getSupportFragmentManager();
        mDetailsFragment = (MovieDetailFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        if (mDetailsFragment == null) {
            mDetailsFragment = new MovieDetailFragment();
            fm.beginTransaction().add(R.id.movie_details_container, mDetailsFragment, TAG_TASK_FRAGMENT).commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

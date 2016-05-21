package com.caydeem.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.caydeem.movies.models.Movie;
import com.caydeem.movies.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovieDetailActivity extends AppCompatActivity {

    private static String TAG="MDetailActivity";

    private TextView movieTitle;
    private ImageView moviePoster;
    private  TextView movieReleaseYear;
    private  TextView movieRating;
    private TextView movieOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieTitle=(TextView)findViewById(R.id.movieTitle);
        moviePoster=(ImageView)findViewById(R.id.moviePoster);
        movieReleaseYear=(TextView)findViewById(R.id.movieReleaseYear);
        movieRating=(TextView)findViewById(R.id.movieRating);
        movieOverview=(TextView)findViewById(R.id.movieOverview);

        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();

        if (intent != null && intent.hasExtra("selectedMovie")) {
            Movie selectedMovie = intent.getParcelableExtra("selectedMovie");
            if (selectedMovie != null) {
                Log.i(TAG, "zz with movie");
                fillDetailScreen(selectedMovie);
            }else{
                Log.i(TAG, "zz NO movie");
            }
        }

    }

    private void fillDetailScreen(Movie selectedMovie) {
        Log.i(TAG, "zz in fillDetailScreen");
        Log.i(TAG, "zz selectedMovie.getmTitle()=" + selectedMovie.getmTitle());

        movieTitle.setText(selectedMovie.getmTitle());
        Picasso.with(this)
                .load(Constants.MOVIE_DB_POSTER_URL + Constants.POSTER_PHONE_SIZE + selectedMovie.getmPosterPath())
                .placeholder(R.drawable.poster_placeholder) // support download placeholder
                .error(R.drawable.poster_placeholder_error) //support error placeholder, if back-end returns empty string or null
                .into(moviePoster);
        movieRating.setText(selectedMovie.getmVoteAverage() + "/10");
        movieOverview.setText(selectedMovie.getmOverview());

        // Movie DB API returns release date in yyyy--mm-dd format
        // Extract the year through regex
        Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        String year = selectedMovie.getmReleaseDate();
        Matcher dateMatcher = datePattern.matcher(year);
        if (dateMatcher.find()) {
            year = dateMatcher.group(1);
        }
        movieReleaseYear.setText(year);
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

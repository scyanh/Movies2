package com.caydeem.movies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.caydeem.movies.adapters.MovieAdapter;
import com.caydeem.movies.models.Movie;
import com.caydeem.movies.models.MovieInfo;
import com.caydeem.movies.services.DiscoverMovieService;
import com.caydeem.movies.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    private  static String TAG="MainActivity";
    private List<Movie> mMovieList;
    GridView mMovieGrid;
    Activity mContext;

    private boolean movies_loaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;

        setContentView(R.layout.activity_main);

        mMovieGrid=(GridView)findViewById(R.id.movies_grid);

        //discoverMovies("vote_average.desc");
        movies_loaded=false;
        if(savedInstanceState != null) {
            Log.d(TAG, "zz on savedInstanceState");
            movies_loaded=true;
            mMovieList = savedInstanceState.getParcelableArrayList("movies");
        }

        discoverMovies("popularity.desc", movies_loaded);


    }


    private void discoverMovies(String sort, boolean movies){
        if (movies){
            Log.d(TAG, "zz with movies");
            if (mMovieList != null) {
                mMovieGrid.setAdapter(new MovieAdapter(mContext, mMovieList));
            } else {
                mMovieGrid.setAdapter(null);
            }

            mMovieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Movie selectedMovie = mMovieList.get(position);
                    Intent intent = new Intent(mContext, MovieDetailActivity.class);
                    intent.putExtra("selectedMovie", selectedMovie);
                    startActivity(intent);
                }
            });

        }else{
            Log.d(TAG, "zz no movies");
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(Constants.MOVIE_DB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            DiscoverMovieService api = client.create(DiscoverMovieService.class);

            Call<MovieInfo> restCall = api.getPopularMovies(sort, Constants.MOVIE_DB_API_KEY);

            restCall.enqueue(new Callback<MovieInfo>() {
                @Override
                public void onResponse(Response<MovieInfo> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        MovieInfo movieInfo = response.body();
                        mMovieList = movieInfo.getmMovieList();

                        if (mMovieList != null) {
                            mMovieGrid.setAdapter(new MovieAdapter(mContext, mMovieList));
                        } else {
                            mMovieGrid.setAdapter(null);
                        }

                        mMovieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Movie selectedMovie = mMovieList.get(position);
                                Intent intent = new Intent(mContext, MovieDetailActivity.class);
                                intent.putExtra("selectedMovie", selectedMovie);
                                startActivity(intent);
                            }
                        });

                    } else {
                        Log.d(TAG, "zz call error");
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "zz in onSaveInstanceState");
        outState.putParcelableArrayList("movies", (ArrayList<? extends Parcelable>) mMovieList);
    }

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

    }

}

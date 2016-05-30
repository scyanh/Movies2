package com.caydeem.movies.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.caydeem.movies.MainActivity;
import com.caydeem.movies.MovieDetailActivity;
import com.caydeem.movies.R;
import com.caydeem.movies.adapters.MovieAdapter;
import com.caydeem.movies.data.MovieContract;
import com.caydeem.movies.models.Movie;
import com.caydeem.movies.models.MovieInfo;
import com.caydeem.movies.services.DiscoverMovieService;
import com.caydeem.movies.utils.Constants;
import com.caydeem.movies.utils.Utility;

import java.util.List;
import java.util.Vector;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * A placeholder fragment containing a simple grid view
 * Sequence for callbacks (upon launch): -> onCreate() -> onCreateView() -> onActivityCreated() ->  onCreateLoader () -> onStart () -> onLoadFinished()-> onCreateOptionsMenu()
 * Sequence for callbacks (upon rotation): -> onPause()  -> onCreateView() -> onActivityCreated() ->  onStart() ->onLoadFinished -> onCreateOptionsMenu()
 */
public class MovieGalleryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = MovieGalleryFragment.class.getSimpleName();

    GridView mMovieGrid;

    private View mRootView;
    private static final int MOVIE_LOADER_ID = 0;

    // For the main Grid layout view, we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_MOVIE_POSTER_PATH = 2;
    private static final int COL_MOVIE_RELEASE_DATE = 3;
    private static final int COL_MOVIE_VOTE_AVERAGE = 4;

    public MovieGalleryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate() called");
        setRetainInstance(true);
        setHasOptionsMenu(true); // fragment should handle menu events
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "zz onCreateView() called");

        mRootView = inflater.inflate(R.layout.fragment_moviegallery, container, false);
        mMovieGrid=(GridView)mRootView.findViewById(R.id.movies_grid);

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "zz onActivityCreated called");
        super.onActivityCreated(savedInstanceState);

        discoverMovies("popularity.desc", MainActivity.movies_loaded);
    }

    private void queryFavorites(){
        //String read = getActivity().getApplicationContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI);
        Log.i(TAG, "zz read query=");

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "zz onCreateLoader called");
        // Defines a string to contain the selection clause
        String selectionClause = null;
        // An array to contain selection arguments
        String[] selectionArgs = null;
        // Gets a word from the UI

        selectionClause = MovieContract.MovieEntry.COLUMN_IS_FAVORITE + " = ?";

        // Use the user's input string as the (only) selection argument.
        selectionArgs = new String[]{"" + 1};

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,   //projection
                selectionClause,  //selection
                selectionArgs,  //selection args
                null); //sort order
    }

    /**
     * Called when loader is complete and data is ready. Used for making UI updates.
     *
     * @param loader
     * @param cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(TAG, "zz onLoadFinished called cursor count is " + cursor.getCount());

        if (cursor.moveToFirst()) {
            MainActivity.mMovieList.clear();
            do {
                Movie movie = new Movie();
                //Log.i(TAG, "zz name="+cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_TITLE)));
                Log.i(TAG, "zz COL_MOVIE_ID="+cursor.getInt(COL_MOVIE_ID) + " movie COL_MOVIE_TITLE: " + cursor.getString(COL_MOVIE_TITLE));

                movie.setmId(cursor.getInt(COL_MOVIE_ID));
                movie.setmTitle(cursor.getString(COL_MOVIE_TITLE));
                movie.setmPosterPath(cursor.getString(COL_MOVIE_POSTER_PATH));
                movie.setmReleaseDate(cursor.getString(COL_MOVIE_RELEASE_DATE));
                movie.setmVoteAverage(cursor.getInt(COL_MOVIE_VOTE_AVERAGE));


                MainActivity.mMovieList.add(movie);
            } while (cursor.moveToNext());

            if (MainActivity.mMovieList != null) {
                mMovieGrid.setAdapter(new MovieAdapter(getActivity().getApplicationContext(), MainActivity.mMovieList));
                //insertMovieRecords(MainActivity.mMovieList);
            } else {
                mMovieGrid.setAdapter(null);
            }

            mMovieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MainActivity.selectedMovie = MainActivity.mMovieList.get(position);

                    if (MainActivity.mTwoPane) {
                        Log.i(TAG, "zz mTwoPane");

                        MovieDetailFragment fragment = new MovieDetailFragment();

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_details_container, fragment, MainActivity.MOVIEFRAGMENT_TAG)
                                .commit();
                    } else {
                        Log.i(TAG, "zz mOnePane");
                        Intent intent = new Intent(getActivity().getApplicationContext(), MovieDetailActivity.class);
                        //intent.putExtra("selectedMovie", selectedMovie);
                        startActivity(intent);
                    }
                }
            });

        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(TAG, "zz onLoaderReset");
    }

    private void discoverMovies(String sort, boolean movies){
        if (movies){
            Log.i(TAG, "zz with movies");
            if (MainActivity.mMovieList != null) {
                Log.i(TAG, "zz mMovieList not null");
                mMovieGrid.setAdapter(new MovieAdapter(getActivity().getApplicationContext(), MainActivity.mMovieList));
            } else {
                Log.i(TAG, "zz mMovieList null");
                mMovieGrid.setAdapter(null);
            }

            mMovieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MainActivity.selectedMovie = MainActivity.mMovieList.get(position);


                    if (MainActivity.mTwoPane) {
                        Log.i(TAG, "zz mTwoPane");

                        MovieDetailFragment fragment = new MovieDetailFragment();

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_details_container, fragment, MainActivity.MOVIEFRAGMENT_TAG)
                                .commit();
                    } else {
                        Log.i(TAG, "zz mOnePane");
                        Intent intent = new Intent(getActivity().getApplicationContext(), MovieDetailActivity.class);
                        //intent.putExtra("selectedMovie", selectedMovie);
                        startActivity(intent);
                    }

                }
            });

        }else{
            Log.i(TAG, "zz no movies");
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(Constants.MOVIE_DB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            DiscoverMovieService api = client.create(DiscoverMovieService.class);

            Call<MovieInfo> restCall = api.getPopularMovies(sort, Constants.MOVIE_DB_API_KEY);

            restCall.enqueue(new retrofit.Callback<MovieInfo>() {
                @Override
                public void onResponse(Response<MovieInfo> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        MovieInfo movieInfo = response.body();
                        MainActivity.mMovieList = movieInfo.getmMovieList();

                        if (MainActivity.mMovieList != null) {
                            mMovieGrid.setAdapter(new MovieAdapter(getActivity().getApplicationContext(), MainActivity.mMovieList));
                            insertMovieRecords(MainActivity.mMovieList);
                        } else {
                            mMovieGrid.setAdapter(null);
                        }

                        mMovieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                MainActivity.selectedMovie = MainActivity.mMovieList.get(position);

                                /*
                                Intent intent = new Intent(getActivity().getApplicationContext(), MovieDetailActivity.class);
                                intent.putExtra("selectedMovie", selectedMovie);
                                startActivity(intent);
                                */

                                if (MainActivity.mTwoPane) {
                                    Log.i(TAG, "zz mTwoPane");

                                    MovieDetailFragment fragment = new MovieDetailFragment();

                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.movie_details_container, fragment, MainActivity.MOVIEFRAGMENT_TAG)
                                            .commit();
                                } else {
                                    Log.i(TAG, "zz mOnePane");
                                    Intent intent = new Intent(getActivity().getApplicationContext(), MovieDetailActivity.class);
                                    //intent.putExtra("selectedMovie", selectedMovie);
                                    startActivity(intent);
                                }


                            }
                        });

                    } else {
                        Log.i(TAG, "zz call error");
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }

    }

    /**
     * Inserts movie JSON result into movie.db DB.
     * This method is executed in a background worker thread.
     */
    private void insertMovieRecords(final List<Movie> movieList) {
        // Insert the new movie information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieList.size());
        String sortCriteria = Utility.getPreferredSortingCriteria(getActivity().getApplicationContext());

        for (Movie movie : movieList) {
            /*Log.i(TAG, "zz movie="+movie);
            Log.i(TAG, "zz movie.getmTitle()="+movie.getmTitle());
            Log.i(TAG, "zz movie.getmReleaseDate()="+movie.getmReleaseDate());
            */

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry._ID, movie.getmId());
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getmTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getmVoteAverage());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getmReleaseDate());
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getmOverview());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getmPosterPath());

            if (sortCriteria.equalsIgnoreCase(getActivity().getApplicationContext().getString(R.string.pref_sort_by_popular))) {
                movieValues.put(MovieContract.MovieEntry.COLUMN_IS_POPULAR, 1);   // SQLite does not have a separate Boolean storage class.
            } else if (sortCriteria.equalsIgnoreCase(getActivity().getApplicationContext().getString(R.string.pref_sort_by_rating))) {
                movieValues.put(MovieContract.MovieEntry.COLUMN_IS_RATED, 1);     // Instead, Boolean values are stored as integers 0 (false) and 1 (true).
            }
            cVVector.add(movieValues);

        }

        int inserted = 0;
        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = getActivity().getApplicationContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }
        Log.i(TAG, "zz saved Complete. " + inserted + " Inserted");
    }

    /**
     * This callback makes the fragment visible to the user when the containing activity is started.
     * We want to make a network request before user can  begin interacting with the user (onResume callback)
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart called");
        MainActivity.getEventBus().register(this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.i(TAG, "onCreateOptionsMenu() called");
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MainActivity.movies_loaded=false;
        int id = item.getItemId();
        if (id == R.id.action_most_popular) {
            discoverMovies("popularity.desc", MainActivity.movies_loaded);
            return true;
        }
        if (id == R.id.action_most_rated) {
            discoverMovies("vote_average.desc", MainActivity.movies_loaded);
            return true;
        }
        if (id == R.id.action_favorites) {
            if (getLoaderManager().getLoader(MOVIE_LOADER_ID) == null) {
                getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
            } else {
                getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);


    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop called");
        MainActivity.getEventBus().unregister(this);
    }

}


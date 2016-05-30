package com.caydeem.movies.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caydeem.movies.MainActivity;
import com.caydeem.movies.R;
import com.caydeem.movies.adapters.MovieReviewAdapter;
import com.caydeem.movies.adapters.MovieTrailerAdapter;
import com.caydeem.movies.data.MovieContract;
import com.caydeem.movies.data.MovieContract.MovieEntry;
import com.caydeem.movies.models.Movie;
import com.caydeem.movies.models.MovieReview;
import com.caydeem.movies.models.ReviewInfo;
import com.caydeem.movies.models.Trailer;
import com.caydeem.movies.models.TrailerInfo;
import com.caydeem.movies.services.DiscoverMovieService;
import com.caydeem.movies.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;
import su.j2e.rvjoiner.RvJoiner;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    private static final String MOVIE_DETAILS_SHARE_HASHTAG = " #PopularMoviesApp";
    static final String MOVIE_ID = "ID";
    static final String DETAIL_URI = "URI";
    private Uri mUri;

    private TextView mMovieTileTxt;
    private ImageView mMoviePoster;
    private TextView mMovieReleaseYearTxt;
    private TextView mMovieRatingTxt;
    private TextView mMovieOverviewTxt;
    private ImageView mMovieFavorite;

    private RecyclerView rv;
    private RvJoiner rvJoiner = new RvJoiner();
    private MovieTrailerAdapter trailerAdapter;
    private MovieReviewAdapter reviewAdapter;

    private int mMovieID;
    private String mMovieTitle;
    private String mMovieOverview;
    private String mMovieVotes;
    private String mMovieReleaseDate;
    private String mMoviePosterPath;
    private boolean mIsFavorite;

    private static final int DETAIL_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_IS_FAVORITE
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_MOVIE_OVERVIEW = 2;
    private static final int COL_MOVIE_VOTE_AVERAGE = 3;
    private static final int COL_MOVIE_RELEASE_DATE = 4;
    private static final int COL_MOVIE_POSTER_PATH = 5;
    private static final int COL_MOVIE_IS_FAVORITE = 6;

    private View viewRecycler;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);// fragment should handle menu events.
        Log.d(TAG, "zz in onCreate");
        // Retain this fragment across configuration changes.
        //setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "zz onStart called");
        MainActivity.getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onPause();
        Log.d(TAG, "zz onStop called");
        MainActivity.getEventBus().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "zz in onCreateView");



        viewRecycler = inflater.inflate(R.layout.fragment_details, container, false);
        rv = (RecyclerView) viewRecycler.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        trailerAdapter = new MovieTrailerAdapter(null, getContext());
        reviewAdapter = new MovieReviewAdapter(null);

        final Movie selectedMovie= MainActivity.selectedMovie;



        if (selectedMovie == null) {
            Log.i(TAG, "zz no movie");
            rvJoiner.add(new JoinableLayout(R.layout.placeholder));
        } else {

            mMovieID=selectedMovie.getmId();
            rvJoiner.add(new JoinableLayout(R.layout.movie_details, new JoinableLayout.Callback() {
                @Override
                public void onInflateComplete(View view, ViewGroup parent) {
                    Log.i(TAG, "zz onInflateComplete");
                    mMovieTileTxt = (TextView) view.findViewById(R.id.movieTitle);
                    mMoviePoster = (ImageView) view.findViewById(R.id.moviePoster);
                    mMovieReleaseYearTxt = (TextView) view.findViewById(R.id.movieReleaseYear);
                    mMovieRatingTxt = (TextView) view.findViewById(R.id.movieRating);
                    mMovieOverviewTxt = (TextView) view.findViewById(R.id.movieOverview);
                    mMovieFavorite = (ImageView) view.findViewById(R.id.favoriteIcon);

                    //fillDetailsScreen(selectedMovie);
                }
            }));

            rvJoiner.add(new JoinableLayout(R.layout.trailers));
            rvJoiner.add(new JoinableAdapter(trailerAdapter));
            rvJoiner.add(new JoinableLayout(R.layout.reviews));
            rvJoiner.add(new JoinableAdapter(reviewAdapter));
        }
        rv.setAdapter(rvJoiner.getAdapter());

        //viewDetail = inflater.inflate(R.layout.movie_details, container, false);

        setupMovieTrailerdapter(null);
        setupMovieReviewAdapter(null);

        return viewRecycler;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "zz in onActivityCreated");
        if (MainActivity.selectedMovie != null) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);

            discoverReviews();
            discoverTraillers();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "zz in onCreateLoader id="+id+" args="+args);
        mUri=MovieEntry.CONTENT_URI;
        if (mUri != null) {
            String selectionClause = MovieContract.MovieEntry._ID + " = ?";
            String[] selectionArgs = new String[]{"" + mMovieID};

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_COLUMNS,      //projection
                    selectionClause,    //selection
                    selectionArgs,      //selection args
                    null                //sort order
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(TAG, "zz In onLoadFinished");
        if (!cursor.moveToFirst()) {
            return;
        }

        mMovieID = cursor.getInt(COL_MOVIE_ID);
        mMovieTitle = cursor.getString(COL_MOVIE_TITLE);
        mMovieOverview = cursor.getString(COL_MOVIE_OVERVIEW);
        mMovieVotes = cursor.getString(COL_MOVIE_VOTE_AVERAGE);
        mMovieReleaseDate = cursor.getString(COL_MOVIE_RELEASE_DATE);
        mMoviePosterPath = cursor.getString(COL_MOVIE_POSTER_PATH);
        mIsFavorite = cursor.getInt(COL_MOVIE_IS_FAVORITE) > 0;

        Log.i(TAG, "zz cursor title="+cursor.getString(COL_MOVIE_TITLE));
        Log.i(TAG, "zz cursor mIsFavorite="+mIsFavorite);

        /*
        if (mIsFavorite) {
            showFavoriteIcon(mMovieFavorite, R.drawable.ic_favorite_black_24dp);
        } else {
            showFavoriteIcon(mMovieFavorite, R.drawable.ic_favorite_border_black_24dp);
        }
        */

        fillDetailsScreen();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    /**
     * Used to render original title, poster image, overview (plot), user rating and release date.
     */
    private void fillDetailsScreen() {
        Log.i(TAG, "zz in fillDetailScreen");
        Log.i(TAG, "zz mMovieTitle=" + mMovieTitle);

        if (mMovieTileTxt != null) {
            mMovieTileTxt.setText(mMovieTitle);
        }


        if(mMoviePoster!=null){
            Picasso.with(getContext())
                    .load(Constants.MOVIE_DB_POSTER_URL + Constants.POSTER_PHONE_SIZE + mMoviePosterPath)
                    .placeholder(R.drawable.poster_placeholder) // support download placeholder
                    .error(R.drawable.poster_placeholder_error) //support error placeholder, if back-end returns empty string or null
                    .into(mMoviePoster);
        }



        //we only want to display ratings rounded up to 3 chars max (e.g. 6.3)
        if (mMovieVotes != null && mMovieVotes.length() >= 3) {
            mMovieVotes = mMovieVotes.substring(0, 3);
        }

        if(mMovieRatingTxt!=null){
            mMovieRatingTxt.setText("" + mMovieVotes + "/10");
        }

        if(mMovieOverviewTxt!=null){
            mMovieOverviewTxt.setText(mMovieOverview);
        }



        Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        try{
            String year = mMovieReleaseDate;

            Log.i(TAG, "zz mMovieReleaseDate="+mMovieReleaseDate);

            Matcher dateMatcher = datePattern.matcher(year);
            if (dateMatcher.find()) {
                year = dateMatcher.group(1);
            }
            mMovieReleaseYearTxt.setText(year);
        }catch (Exception e){
            e.printStackTrace();
        }



        if (mMovieFavorite != null) {
            if (mIsFavorite) {
                showFavoriteIcon(mMovieFavorite, R.drawable.ic_favorite_black_24dp);
            } else {
                showFavoriteIcon(mMovieFavorite, R.drawable.ic_favorite_border_black_24dp);
            }


            mMovieFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create and execute the background task.
                    Log.i(TAG, "zz mMovieID="+mMovieID);
                    DBUpdateTask task = new DBUpdateTask(mIsFavorite, mMovieID);
                    task.execute();
                }
            });
        }




    }

    private void discoverReviews(){
            Log.d(TAG, "zz in discoverReviews");
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(Constants.MOVIE_DB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            DiscoverMovieService api = client.create(DiscoverMovieService.class);

            Call<ReviewInfo> restCall = api.getReviews(MainActivity.selectedMovie.getmId(), Constants.MOVIE_DB_API_KEY);

            restCall.enqueue(new retrofit.Callback<ReviewInfo>() {
                @Override
                public void onResponse(Response<ReviewInfo> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        ReviewInfo movieReviews = response.body();
                        //PopularMoviesApplication.getEventBus().post(Utility.produceReviewEvent(movieReviews.getmReviewList()));
                        Log.d(TAG, "zz Reviews Result count : " + movieReviews.getmReviewList().size());
                        setupMovieReviewAdapter(movieReviews.getmReviewList());

                    } else {
                        Log.d(TAG, "zz call error");
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d(TAG, "zz reviews onFailure");
                }
            });


    }

    private void discoverTraillers(){
        Log.d(TAG, "zz in discoverTraillers");
        Retrofit client = new Retrofit.Builder()
                .baseUrl(Constants.MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DiscoverMovieService api = client.create(DiscoverMovieService.class);

        Call<TrailerInfo> restCall = api.getTrailers(MainActivity.selectedMovie.getmId(), Constants.MOVIE_DB_API_KEY);

        restCall.enqueue(new retrofit.Callback<TrailerInfo>() {
            @Override
            public void onResponse(Response<TrailerInfo> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    TrailerInfo movieTrailers = response.body();
                    Log.d(TAG, "zz Traillers Result count : " + movieTrailers.getmResults().size());
                    setupMovieTrailerdapter(movieTrailers.getmResults());
                } else {
                    Log.d(TAG, "zz call error");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "zz reviews onFailure");
            }
        });


    }

    private void showFavoriteIcon(ImageView image, int resoureId) {
        image.setImageResource(resoureId);
        image.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        } else {
            Log.d(TAG, "Share Action Provider is null?");
        }
    }

    /**
     * Returns an implicit intent to launch another app. Movie title is added as intent extra.
     *
     * @return intent
     */
    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND); //generic action
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET); //required to return to Popular Movies app
        shareIntent.setType("text/plain");

        if (mMovieTileTxt != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    mMovieTileTxt.getText() + MOVIE_DETAILS_SHARE_HASHTAG);
        }

        return shareIntent;
    }

    private void setupMovieReviewAdapter(final List<MovieReview> movieReviews) {
        if (getActivity() == null) return;

        if (movieReviews != null) {
            reviewAdapter.setmMovieReviewList(movieReviews);
            reviewAdapter.notifyDataSetChanged();
            Log.d(TAG, "# of reviews in setupAdapet is: " + movieReviews.size());
        }
    }

    private void setupMovieTrailerdapter(final List<Trailer> movieTrailers) {
        if (getActivity() == null) return;

        if (movieTrailers != null) {
            trailerAdapter.setmMovieTrailerList(movieTrailers);
            trailerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Used to insert a record into SQLite DB in a non-UI worker thread.
     */
    private class DBUpdateTask extends AsyncTask<Void, Integer, Void> {

        boolean mIsFavorite;
        int movieID;

        DBUpdateTask(boolean mIsFavorite, int movieID) {
            this.mIsFavorite = mIsFavorite;
            this.movieID = movieID;
        }


        @Override
        protected void onPreExecute() {
        }

        /**
         * Note that we do NOT call the callback object's methods
         * directly from the background thread, as this could result
         * in a race condition.
         */
        @Override
        protected Void doInBackground(Void... ignore) {
            ContentValues updateValues = new ContentValues();
            if (mIsFavorite) {
                Log.i(TAG, "zz is favorite");
                updateValues.put(MovieEntry.COLUMN_IS_FAVORITE, 0);
                //mIsFavorite=false;
            } else {
                Log.i(TAG, "zz is NOT favorite");
                updateValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 1);
                //mIsFavorite=true;
            }

            // Defines selection criteria for the rows you want to update
            String selectionClause = MovieEntry._ID + " = ?";
            String[] selectionArgs = new String[]{"" + movieID};

            // Defines a variable to contain the number of updated rows
            int rowsUpdated = 0;


            rowsUpdated = getContext().getContentResolver().update(
                    MovieEntry.CONTENT_URI,  // the user dictionary content URI
                    updateValues,                       // the columns to update
                    selectionClause,                    // the column to select on
                    selectionArgs);                      // the value to compare to

            Log.i(TAG, "zz rowsUpdated="+rowsUpdated);

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
        }

        @Override
        protected void onCancelled() {
        }

        @Override
        protected void onPostExecute(Void ignore) {
            //readMovieID();
        }
    }
}

package com.caydeem.movies.services;


import com.caydeem.movies.models.MovieInfo;
import com.caydeem.movies.models.ReviewInfo;
import com.caydeem.movies.models.TrailerInfo;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface DiscoverMovieService {

    @GET("/3/discover/movie")
    public Call<MovieInfo> getPopularMovies(@Query("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/reviews")
    public Call<ReviewInfo> getReviews(@Path("id") int movieId, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/videos")
    public Call<TrailerInfo> getTrailers(@Path("id") int movieId, @Query("api_key") String apiKey);
}
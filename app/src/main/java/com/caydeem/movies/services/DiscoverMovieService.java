package com.caydeem.movies.services;


import com.caydeem.movies.models.MovieInfo;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface DiscoverMovieService {

    @GET("/3/discover/movie")
    public Call<MovieInfo> getPopularMovies(@Query("sort_by") String sortBy, @Query("api_key") String apiKey);

}
package com.hafidrf.movieapp.movies.networking

import com.hafidrf.movieapp.movies.model.MovieDetailsModel
import com.hafidrf.movieapp.movies.model.MovieSearchModel
import com.hafidrf.movieapp.networking.RemoteResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    @GET("movie/now_playing")
    fun getMoviesNowPlaying(@Query("api_key") apiKey: String, @Query("page") page: Int): Single<RemoteResponse<List<MovieSearchModel>>>

    @GET("search/movie")
    fun searchMovie(@Query("api_key") apiKey: String, @Query("page") page: Int, @Query("query") query: String): Single<RemoteResponse<List<MovieSearchModel>>>

    @GET("movie/{movieId}")
    fun getMovieDetails(@Path("movieId") movieId: Int, @Query("api_key") apiKey: String): Single<MovieDetailsModel>

}

package com.hafidrf.movieapp.movies.networking

import com.hafidrf.movieapp.movies.model.MovieSearchModel
import com.hafidrf.movieapp.networking.RemoteResponse
import io.reactivex.Single

interface MovieSearchContract {
    interface Repo {

        fun moviesNowPlaying(apiKey: String, page: Int): Single<RemoteResponse<List<MovieSearchModel>>>

        fun searchMovie(apiKey: String, page: Int, query: String): Single<RemoteResponse<List<MovieSearchModel>>>

    }
}
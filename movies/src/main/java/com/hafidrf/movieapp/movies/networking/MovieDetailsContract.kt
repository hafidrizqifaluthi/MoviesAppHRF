package com.hafidrf.movieapp.movies.networking

import com.hafidrf.movieapp.movies.model.MovieDetailsModel
import io.reactivex.Single

interface MovieDetailsContract {

    interface Repo {

        fun getMovieDetails(movieId: Int, apiKey: String): Single<MovieDetailsModel>

    }

}
package com.hafidrf.movieapp.movies.networking

import com.hafidrf.movieapp.movies.model.MovieDetailsModel
import io.reactivex.Single

class MovieDetailsRepo(private val service:  MovieService) :
     MovieDetailsContract.Repo {

    override fun getMovieDetails(movieId: Int, apiKey: String): Single<MovieDetailsModel> = service.getMovieDetails(movieId, apiKey)
}
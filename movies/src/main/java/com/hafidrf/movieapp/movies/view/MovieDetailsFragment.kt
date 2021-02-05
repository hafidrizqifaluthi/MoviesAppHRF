package com.hafidrf.movieapp.movies.view


import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.hafidrf.movieapp.BuildConfig.BACKDROP_BASE_URL
import com.hafidrf.movieapp.movies.R
import com.hafidrf.movieapp.movies.model.MovieDetailsModel
import com.hafidrf.movieapp.movies.model.MovieSearchModel
import com.hafidrf.movieapp.movies.viewmodel.MovieDetailsVM
import com.hafidrf.movieapp.view.BaseFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.actionbar_toolbar.*
import kotlinx.android.synthetic.main.fragment_movie_details.*
import org.koin.android.ext.android.inject

class MovieDetailsFragment : BaseFragment() {

    override val layout = R.layout.fragment_movie_details

    override val viewModel: MovieDetailsVM by inject()

    private var selectedMovie: MovieSearchModel? = null

    companion object {
        const val TAG = "MovieDetailsFragment"
        const val MOVIE = "movie"
        fun newInstance(movie: MovieSearchModel) =
            MovieDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(MOVIE, movie)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(MOVIE))
                selectedMovie = it.getParcelable(MOVIE)
        }

        if (selectedMovie == null) {
            showToast(R.string.something_went_wrong)
            popBack()
            return
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar(toolbar, "Movie Details")

        srlDetails.isEnabled = false

        tvTitle.text = selectedMovie?.title
        tvImdb.text = "IMDB " + selectedMovie?.vote_average
        tvReleaseDate.text = selectedMovie?.release_date
        tvRated.text = when (selectedMovie?.adult) {
            true -> "PG-18"
            false -> "PG-13"
            null -> "PG-13"
        }

        viewModel.getMovieDetails(selectedMovie!!.id)
            .observe(this@MovieDetailsFragment, Observer { details ->
                handleMovieDetails(details)
            })


    }

    private fun handleMovieDetails(details: MovieDetailsModel) {

        val picasso = Picasso.get()
        picasso.load(BACKDROP_BASE_URL + details.coverImagePath)
            .placeholder(R.drawable.cover_placeholder)
            .into(imCover)

        tvDetails.text = details.overview
        tvRuntime.text = details.runTime
        tvGenre.text = details.genresList
        tvReleaseDate.text = details.releaseDate
    }

    override fun hideLoading() {
        srlDetails.isRefreshing = false
    }

    override fun showLoading() {
        srlDetails.isRefreshing = true
    }
}

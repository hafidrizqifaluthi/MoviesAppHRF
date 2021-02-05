package com.hafidrf.movieapp.movies.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hafidrf.movieapp.movies.R
import com.hafidrf.movieapp.movies.dh.MovieDH
import com.hafidrf.movieapp.movies.model.MovieSearchModel

private val loadMovieDependencies by lazy {  MovieDH.init() }

private fun injectMovieDependencies() =
    loadMovieDependencies

class MovieActivity : AppCompatActivity(),
    MovieSearchFragment.MovieNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        injectMovieDependencies()

        displayFragment(
            MovieSearchFragment.newInstance(),
            MovieSearchFragment.TAG
        )
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1)
            super.onBackPressed()
        else finish()
    }

    override fun showMovieDetails(movie: MovieSearchModel) {
        displayFragment(
            MovieDetailsFragment.newInstance(
                movie
            ), MovieDetailsFragment.TAG
        )
    }

    private fun displayFragment(fragment: Fragment, tag: String) {
        if (supportFragmentManager.findFragmentByTag(tag) != null) return

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.flContainer, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }
}

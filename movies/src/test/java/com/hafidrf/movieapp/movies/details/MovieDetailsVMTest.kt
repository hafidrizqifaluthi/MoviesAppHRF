package com.hafidrf.movieapp.movies.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.hafidrf.movieapp.BuildConfig
import com.hafidrf.movieapp.movies.model.GenresModel
import com.hafidrf.movieapp.movies.model.MovieDetailsModel
import com.hafidrf.movieapp.movies.networking.MovieDetailsContract
import com.hafidrf.movieapp.movies.utils.TrampolineSchedulerRule
import com.hafidrf.movieapp.movies.viewmodel.MovieDetailsVM
import com.hafidrf.movieapp.utils.TestingUtils
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MovieDetailsVMTest {

    @get:Rule
    var ruleInstant = InstantTaskExecutorRule()

    @get:Rule
    var ruleRx = TrampolineSchedulerRule()

    private lateinit var viewModel: MovieDetailsVM
    private val repo = mock<MovieDetailsContract.Repo>()

    private val loadingObs = mock<Observer<Boolean>>()
    private val errorObs = mock<Observer<Throwable>>()
    private val gson = Gson()

    private val baseUrl = BuildConfig.API_BASE //"https://swapi.co/api/"

    private val movieId = 475557

    @Before
    fun setUp() {
        viewModel = MovieDetailsVM(repo = repo)

        viewModel.loading.observeForever(loadingObs)
        viewModel.error.observeForever(errorObs)
    }

    @Test
    fun testSuccessfulRemoteLoad() {
        val detailsObs = mock<Observer<MovieDetailsModel>>()

        var genres = mutableListOf<GenresModel>()
        genres.add(GenresModel(80, "Crime"))
        genres.add(GenresModel(53, "Thriller"))
        genres.add(GenresModel(18, "Drama"))

        val expectedDetails = MovieDetailsModel(
            id = 475557,
            title = "Joker",
            adult = false,
            overview = "During the 1980s, a failed stand-up comedian is driven insane and turns to a life of crime and chaos in Gotham City while becoming an infamous psychopathic crime figure.",
            poster_path = "/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg",
            coverImagePath = "/n6bUvigpRFqSwmPp1m2YADdbRBc.jpg",
            vote_average = 8.6,
            runtime = 122,
            runTime = "2h 2m",
            release_date = "2019-10-02",
            releaseDate = "02 Oct 2019",
            genres = genres,
            genresList = "Crime, Thriller, Drama"
        )

        viewModel.getMovieDetails(movieId).observeForever(detailsObs)

        verify(loadingObs).onChanged(true)

        verify(repo).getMovieDetails(movieId, BuildConfig.API_KEY)

        verify(loadingObs).onChanged(false)

        verify(detailsObs).onChanged(expectedDetails)

        verify(errorObs, never()).onChanged(any())

        pm("We fire API calls properly based on the movie details and pushed the result correctly to the UI.")
    }

    @Test
    fun testPartialRemoteLoad() {
        whenever(repo.getMovieDetails(movieId, BuildConfig.API_KEY)).doReturn(Single.just(mockMovieDetails()))

        val detailsObs = mock<Observer< MovieDetailsModel>>()

        var genres = mutableListOf<GenresModel>()
        genres.add(GenresModel(80, "Crime"))
        genres.add(GenresModel(53, "Thriller"))
        genres.add(GenresModel(18, "Drama"))


        val expectedDetails =  MovieDetailsModel(
            id = 475557,
            title = "Joker",
            adult = false,
            overview = "During the 1980s, a failed stand-up comedian is driven insane and turns to a life of crime and chaos in Gotham City while becoming an infamous psychopathic crime figure.",
            poster_path = "/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg",
            coverImagePath = "/n6bUvigpRFqSwmPp1m2YADdbRBc.jpg",
            vote_average = 8.6,
            runtime = 122,
            runTime = "2h 2m",
            release_date = "2019-10-02",
            releaseDate = "02 Oct 2019",
            genres = genres,
            genresList = "Crime, Thriller, Drama"
        )

        viewModel.getMovieDetails(movieId).observeForever(detailsObs)

        verify(loadingObs).onChanged(true)

        verify(repo).getMovieDetails(movieId, BuildConfig.API_KEY)

        verify(loadingObs).onChanged(false)

        verify(detailsObs).onChanged(expectedDetails)

        verify(errorObs, never()).onChanged(any())

        pm("We fire API calls properly based on the movie details and pushed the result correctly to the UI.")
    }

    @Test
    fun testDateConversionValid() {
        val expected = "27 Sep 2019"
        assertEquals(expected, viewModel.formatDate("2019-09-27"))
        pm("Date is correct converted")
    }

    @Test
    fun testDateConversionInvalid() {
        assertEquals("", viewModel.formatDate("unknown"))
        pm("Date is correct handled for invalid values")
    }

    @Test
    fun testDateConversionEmpty() {
        assertEquals("", viewModel.formatDate(""))
        pm("Date is correct handled for empty values")
    }


    private fun pm(message: String) {
        println("\nMovie details verified: $message")
    }

    private fun mockMovieDetails(): MovieDetailsModel {
        return gson.fromJson(
            TestingUtils.getResponseFromJson("/details/movie_details"),
            MovieDetailsModel::class.java
        )
    }

}
package com.hafidrf.movieapp.movies.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hafidrf.movieapp.BuildConfig
import com.hafidrf.movieapp.movies.model.MovieSearchModel
import com.hafidrf.movieapp.movies.utils.TrampolineSchedulerRule
import com.hafidrf.movieapp.movies.viewmodel.MovieSearchVM
import com.hafidrf.movieapp.networking.RemoteResponse
import com.hafidrf.movieapp.utils.TestingUtils
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.reflect.Type

@RunWith(RobolectricTestRunner::class)
class MovieSearchVMTest {

    @get:Rule
    var ruleInstant = InstantTaskExecutorRule()

    @get:Rule
    var ruleRx = TrampolineSchedulerRule()

    private lateinit var viewModel: MovieSearchVM
    private val repo = mock<com.hafidrf.movieapp.movies.networking.MovieSearchContract.Repo>()

    private val loadingObs = mock<Observer<Boolean>>()
    private val errorObs = mock<Observer<Throwable>>()

    private val paginationObs = mock<Observer<Boolean>>()
    private val MoviesObs = mock<Observer<List<MovieSearchModel>>>()

    private val gson = Gson()

    private val initialSuccessData = mockInitialData()

    @Before
    fun setUp() {
        viewModel = MovieSearchVM(repo = repo)

        viewModel.loading.observeForever(loadingObs)
        viewModel.error.observeForever(errorObs)

        viewModel.paginationLoading.observeForever(paginationObs)
        viewModel.movies.observeForever(MoviesObs)

    }

    @Test
    fun testInitialLoad() {
        whenever(repo.moviesNowPlaying(BuildConfig.API_KEY, 1)).doReturn(Single.just(initialSuccessData))

        viewModel.initialLoad()

        verify(loadingObs).onChanged(true)

        verify(repo).moviesNowPlaying(BuildConfig.API_KEY, 1)

        verify(loadingObs).onChanged(false)

        verify(MoviesObs).onChanged(any())

        verify(errorObs, never()).onChanged(any())

        assertEquals(20, viewModel.movies.value?.size)

        pm("Initial load calls the correct functions in the repo and sets success data correctly")
    }

    @Test
    fun testLoadNextValid() {
        whenever(repo.moviesNowPlaying(BuildConfig.API_KEY, 1)).doReturn(Single.just(initialSuccessData))

        viewModel.setMovie(initialSuccessData.results)
        val nextUrl = initialSuccessData.page
        viewModel.setNextPageUrl(nextUrl - 1)

        viewModel.loadNextPage()

        verify(paginationObs).onChanged(true)

        verify(repo).moviesNowPlaying(BuildConfig.API_KEY, 1)

        verify(paginationObs).onChanged(false)

        verify(MoviesObs, atLeastOnce()).onChanged(any())

        verify(errorObs, never()).onChanged(any())

        assertEquals(40, viewModel.movies.value?.size)

        pm("Load page with a valid page page url calls the correct functions in the repo and appends success data correctly")
    }

    @Test
    fun testSearchMovie() {
        val queriedMovie = "it"
        whenever(repo.searchMovie(BuildConfig.API_KEY, 1, queriedMovie)).doReturn(Single.just(mockItSearch()))

        viewModel.setMovie(initialSuccessData.results)

        viewModel.searchMovie(queriedMovie)

        verify(loadingObs).onChanged(true)

        verify(repo).searchMovie(BuildConfig.API_KEY, 1, queriedMovie)

        verify(loadingObs).onChanged(false)

        verify(MoviesObs, atLeastOnce()).onChanged(any())

        verify(errorObs, never()).onChanged(any())

        assertEquals(1, viewModel.movies.value?.size)

        pm("Search for a movie triggers correct functions in the repo and resets the results to the UI")
    }

    @Test
    fun testRefresh() {
        whenever(repo.moviesNowPlaying(BuildConfig.API_KEY, 1)).doReturn(Single.just(initialSuccessData))

        viewModel.refreshMovies()

        verify(loadingObs).onChanged(true)

        verify(repo).moviesNowPlaying(BuildConfig.API_KEY, 1)

        verify(loadingObs).onChanged(false)

        verify(MoviesObs).onChanged(any())

        verify(errorObs, never()).onChanged(any())

        assertEquals(20, viewModel.movies.value?.size)

        pm("Refresh calls the correct functions in the repo and sets success data correctly")
    }


    //region region: Utils
    private fun pm(message: String) {
        println("\nMovie search verified: $message")
    }

    private fun mockInitialData(): RemoteResponse<List<MovieSearchModel>> {
        val responseModelToken: Type = object : TypeToken<RemoteResponse<List<MovieSearchModel>>>() {}.type
        return gson.fromJson(
            TestingUtils.getResponseFromJson("/search/initial_load"),
            responseModelToken
        )
    }

    private fun mockItSearch(): RemoteResponse<List<MovieSearchModel>> {
        val responseModelToken: Type = object : TypeToken<RemoteResponse<List<MovieSearchModel>>>() {}.type
        return gson.fromJson(
            TestingUtils.getResponseFromJson("/search/it_search"),
            responseModelToken
        )
    }

    //endregion

}
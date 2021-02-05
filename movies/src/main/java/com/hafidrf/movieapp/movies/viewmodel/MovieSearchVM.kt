package com.hafidrf.movieapp.movies.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hafidrf.movieapp.BuildConfig.API_KEY
import com.hafidrf.movieapp.movies.model.MovieSearchModel
import com.hafidrf.movieapp.networking.RemoteResponse
import com.hafidrf.movieapp.view.extensions.hide
import com.hafidrf.movieapp.view.extensions.show
import com.hafidrf.movieapp.viewmodel.BaseVM
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class MovieSearchVM(private val repo: com.hafidrf.movieapp.movies.networking.MovieSearchContract.Repo) : BaseVM() {

    private val _movies = MutableLiveData<List< MovieSearchModel>>()
    val movies: LiveData<List< MovieSearchModel>> by lazy { _movies }

    private var page: Int = 0
    private var totalPages: Int = 0
    private var processing: Boolean = false
    private val FIRST_PAGE = 1

    private val _paginationLoading = MutableLiveData<Boolean>()
    val paginationLoading: LiveData<Boolean> by lazy { _paginationLoading }

    fun initialLoad() {
        if (_movies.value != null && !_movies.value.isNullOrEmpty()) return

        _loading.show()
        getMovies(apiKey = API_KEY, page = FIRST_PAGE, resetItems = true)

    }

    private fun getMovies(apiKey: String, page: Int, resetItems: Boolean) {
        if (processing) return

        processing = true
        handleMoviesObs(repo.moviesNowPlaying(apiKey, page), resetItems)
    }

    private fun handleMoviesObs(
        moviesObs: Single<RemoteResponse<List<MovieSearchModel>>>,
        resetItems: Boolean
    ) {
        moviesObs
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { response ->
                page = response.page
                totalPages = response.total_pages
                return@map response.results
            }
            .map { searchModels ->
                appendOrSetResults(resetItems, _movies.value, searchModels)
            }
            .subscribe({
                _loading.hide()
                _paginationLoading.hide()
                _movies.postValue(it)

                println("List size: ${it.size}")

                processing = false
            }, {
                handleError(it)
                processing = false
            })
            .addTo(disposable)
    }

    private fun appendOrSetResults(
        resetItems: Boolean,
        existingData: List< MovieSearchModel>?,
        newData: List< MovieSearchModel>
    ): List< MovieSearchModel> {
        val finalData = mutableListOf< MovieSearchModel>()
        if (resetItems || existingData.isNullOrEmpty())
            finalData.addAll(newData)
        else {
            finalData.addAll(existingData)
            finalData.addAll(newData)
        }
        return finalData
    }

    fun loadNextPage() {
        _paginationLoading.show()
        getMovies(API_KEY, ++page, false)
    }

    fun searchMovie(query: String?) {
        if (query.isNullOrEmpty()) return

        _loading.show()
        handleMoviesObs(repo.searchMovie(API_KEY, FIRST_PAGE,  query), true)
    }

    fun refreshMovies() {
        _loading.show()
        getMovies(apiKey = API_KEY, page = FIRST_PAGE, resetItems = true)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun setMovie(movies: List<MovieSearchModel>?) {
        _movies.postValue(movies)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun setNextPageUrl(page: Int) {
        this.page = page
    }
}


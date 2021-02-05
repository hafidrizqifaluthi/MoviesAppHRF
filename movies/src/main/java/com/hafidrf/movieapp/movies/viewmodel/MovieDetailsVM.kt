package com.hafidrf.movieapp.movies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hafidrf.movieapp.BuildConfig
import com.hafidrf.movieapp.movies.model.GenresModel
import com.hafidrf.movieapp.movies.model.MovieDetailsModel
import com.hafidrf.movieapp.movies.networking.MovieDetailsContract
import com.hafidrf.movieapp.view.extensions.hide
import com.hafidrf.movieapp.view.extensions.show
import com.hafidrf.movieapp.viewmodel.BaseVM
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import kotlin.math.floor

class MovieDetailsVM(private val repo:  MovieDetailsContract.Repo) : BaseVM() {

    private val movieDetails = MutableLiveData<MovieDetailsModel>()

    fun getMovieDetails(movieId: Int): LiveData< MovieDetailsModel> {
        if (movieDetails.value == null) {

            _loading.show()


            repo.getMovieDetails(movieId, BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map {
                    val runTime = convertTime(it.runtime)
                    val releaseDate = formatDate(it.release_date)
                    val genresList = getGenresList(it.genres)

                    return@map it.copy(runTime = runTime, releaseDate = releaseDate, genresList = genresList)
                }
                .subscribe({
                    _loading.hide()

                    movieDetails.postValue(it)
                }, { handleError(it) })
                .addTo(disposable)
        }

        return movieDetails
    }

    private fun getGenresList(genres : List<GenresModel>?) : String{

        var genresList = StringBuilder("")

        genres?.forEachIndexed { i, genresModel ->
            genresList.append(genresModel.name)

            if (i+1 < genres.size)
                genresList.append(", ")

        }

        return genresList.toString()
    }

    fun formatDate( date: String? ) : String{

        return date.let {
            val parser = SimpleDateFormat("yyyy-MM-dd")
            val formatter = SimpleDateFormat("dd MMM yyyy")

            try {
                formatter.format(parser.parse(date))
            }catch ( exaction: Exception){
                return ""
            }
        }

    }

    private fun convertTime(time: Int?): String {

        return time?.let {
            var hours : Int = 0
            var minutes = time

            if (time >= 60 ) {
                hours = floor((it / 60).toDouble()).toInt()
                minutes = it - (hours * 60)
            }

            if (time == 0)
                ""
            else
                "${hours}h ${minutes}m"

        } ?: kotlin.run {
            return ""
        }
    }

}

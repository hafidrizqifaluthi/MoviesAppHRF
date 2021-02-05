package com.hafidrf.movieapp.movies.dh

import com.hafidrf.movieapp.movies.networking.*
import com.hafidrf.movieapp.movies.viewmodel.MovieDetailsVM
import com.hafidrf.movieapp.movies.viewmodel.MovieSearchVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.create

object MovieDH {

    fun init() {
        loadKoinModules(
             movieDetailsModule(),
             movieSearchModule(),
             movieModule()
        )
    }

    private fun movieDetailsModule(): Module = module {
        viewModel { MovieDetailsVM(get()) }
        single {
             movieDetailsContract(
                get()
            )
        }
    }

    private fun movieDetailsContract(service: MovieService)
            :  MovieDetailsContract.Repo =
         MovieDetailsRepo(service)

    private fun movieSearchModule(): Module = module {
        viewModel { MovieSearchVM(get()) }
        single {
             movieSearchContract(
                get()
            )
        }
    }

    private fun movieSearchContract(service:  MovieService)
            :  MovieSearchContract.Repo =
         MovieSearchRepo(service)


    private fun movieModule(): Module = module {
        single {  movieService(get()) }
    }

    private fun movieService(retrofit: Retrofit):  MovieService = retrofit.create()

}
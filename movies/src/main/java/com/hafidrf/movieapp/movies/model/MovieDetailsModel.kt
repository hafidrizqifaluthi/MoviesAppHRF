package com.hafidrf.movieapp.movies.model

import com.google.gson.annotations.SerializedName
import com.hafidrf.movieapp.networking.Exclude

data class MovieDetailsModel(
    @SerializedName("id") val id: Int?,
    @SerializedName("adult") val adult: Boolean?,
    @SerializedName("title") val title: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val poster_path: String?,
    @SerializedName("backdrop_path") val coverImagePath: String?,
    @SerializedName("vote_average") val vote_average: Double?,

    @SerializedName("runtime") val runtime: Int?,
    @Exclude var runTime : String = "",

    @SerializedName("release_date") val release_date: String?,
    @Exclude var releaseDate : String = "",

    @SerializedName("genres")
    val genres: List<GenresModel>?,
    @Exclude var genresList : String = ""


//    @SerializedName("production_countries")
//    val productionCountries: List<ProductionCountriesModel>?

)

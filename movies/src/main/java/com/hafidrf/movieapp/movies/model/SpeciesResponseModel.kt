package com.hafidrf.movieapp.movies.model

import com.google.gson.annotations.SerializedName

data class SpeciesResponseModel(
        @SerializedName("name") val name: String?,
        @SerializedName("language") val language: String?,
        @SerializedName("homeworld") val homeworldUrl: String?)

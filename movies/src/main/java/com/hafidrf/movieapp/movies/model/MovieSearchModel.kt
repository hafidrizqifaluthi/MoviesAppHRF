package com.hafidrf.movieapp.movies.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieSearchModel(
        @SerializedName("poster_path") val poster_path: String?,
        @SerializedName("id") val id: Int,
        @SerializedName("adult") val adult: Boolean?,
        @SerializedName("title") val title: String?,
        @SerializedName("vote_average") val vote_average: Double?,
        @SerializedName("release_date") val release_date: String?
) : Parcelable

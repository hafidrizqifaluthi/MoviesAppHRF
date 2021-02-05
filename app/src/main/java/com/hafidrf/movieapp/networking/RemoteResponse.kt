package com.hafidrf.movieapp.networking

import com.google.gson.annotations.SerializedName

data class RemoteResponse<T>(
        @SerializedName("page") val page: Int,
        @SerializedName("total_results") val total_results: Int,
        @SerializedName("total_pages") val total_pages: Int,
        @SerializedName("results") val results: T?
)
package com.hafidrf.movieapp.utils

import android.annotation.SuppressLint
import androidx.annotation.VisibleForTesting
import okio.Okio
import java.nio.charset.StandardCharsets

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
object TestingUtils {

    @SuppressLint("NewApi")
    fun getResponseFromJson(fileName: String): String {
        val inputStream = javaClass.classLoader?.getResourceAsStream("api-response/$fileName.json")
        return if (inputStream != null) {
            val source = Okio.buffer(Okio.source(inputStream))
            source.readString(StandardCharsets.UTF_8)
        } else ""
    }
}
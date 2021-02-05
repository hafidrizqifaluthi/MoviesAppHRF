package com.hafidrf.movieapp.view.extensions

import android.content.Intent

private const val PACKAGE_NAME = "com.hafidrf.movieapp"

fun intentTo(navigatableActivity: NavigatableActivity): Intent {
    return Intent(Intent.ACTION_VIEW).setClassName(
        PACKAGE_NAME,
        navigatableActivity.className)
}

interface NavigatableActivity {
    val className: String
}

object Activities {

    object Movies : NavigatableActivity {
        override val className = "$PACKAGE_NAME.movies.view.MovieActivity"
    }

}
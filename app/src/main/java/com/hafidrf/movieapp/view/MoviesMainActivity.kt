package com.hafidrf.movieapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hafidrf.movieapp.R
import com.hafidrf.movieapp.view.extensions.Activities
import com.hafidrf.movieapp.view.extensions.intentTo

class MoviesMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_main)

        startActivity(intentTo(Activities.Movies))
        finish()
    }
}

package com.hafidrf.movieapp.movies.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hafidrf.movieapp.BuildConfig.IMAGE_BASE_URL
import com.hafidrf.movieapp.movies.R
import com.hafidrf.movieapp.movies.model.MovieSearchModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_movie_search.view.*

class MovieSearchAdapter(private val interaction:  Interaction? = null) :
    ListAdapter< MovieSearchModel, MovieSearchAdapter.MovieSearchVH>(
         MovieSearchModelDC()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MovieSearchVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_movie_search,
                parent,
                false
            ),
            interaction
        )

    override fun onBindViewHolder(holder:  MovieSearchVH, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List< MovieSearchModel>) {
        submitList(data.toMutableList())
    }

    inner class MovieSearchVH(itemView: View, private val interaction:  Interaction?) :
        RecyclerView.ViewHolder(itemView), OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition == RecyclerView.NO_POSITION) return
            interaction?.movieClicked(getItem(adapterPosition))
        }

        fun bind(item: MovieSearchModel) = with(itemView) {
            tvTitle.text = item.title
            tvYear.text = item.release_date
            tvImdbRating.text = "IMDB " + item.vote_average

            val picasso = Picasso.get()

            picasso.load(IMAGE_BASE_URL + item.poster_path)
                .placeholder(R.drawable.poster_placeholder)
//                .resize(width, height)
                .into(tvPoster)
        }
    }

    interface Interaction {
        fun movieClicked(movie:  MovieSearchModel)
    }

    private class MovieSearchModelDC : DiffUtil.ItemCallback< MovieSearchModel>() {
        override fun areItemsTheSame(
            oldItem:  MovieSearchModel,
            newItem:  MovieSearchModel
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem:  MovieSearchModel,
            newItem:  MovieSearchModel
        ) = oldItem == newItem
    }
}
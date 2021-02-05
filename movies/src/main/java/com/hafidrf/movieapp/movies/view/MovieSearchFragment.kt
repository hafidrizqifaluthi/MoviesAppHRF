package com.hafidrf.movieapp.movies.view

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hafidrf.movieapp.movies.R
import com.hafidrf.movieapp.movies.adapter.MovieSearchAdapter
import com.hafidrf.movieapp.movies.viewmodel.MovieSearchVM
import com.hafidrf.movieapp.view.BaseFragment
import com.hafidrf.movieapp.view.extensions.EndlessScrollListener
import com.hafidrf.movieapp.view.extensions.gone
import com.hafidrf.movieapp.view.extensions.visible
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.actionbar_toolbar.*
import kotlinx.android.synthetic.main.fragment_search_movie.*
import org.koin.android.ext.android.inject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule


class MovieSearchFragment : BaseFragment(),
     MovieSearchAdapter.Interaction {

    override val layout = R.layout.fragment_search_movie

    override val viewModel: MovieSearchVM by inject()

    private val adapter:  MovieSearchAdapter by lazy {
         MovieSearchAdapter(
            this
        )
    }

    private var searchView: SearchView? = null
    private val searchListener = PublishSubject.create<String>()

    private lateinit var endlessScrollListener: EndlessScrollListener

    private var navigator: MovieNavigator? = null

    companion object {
        const val TAG = "MovieSearchFragment"
        fun newInstance() = MovieSearchFragment()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is MovieNavigator)
            navigator = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolbar(toolbar)
        parentActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
        }

        rvMovies.setHasFixedSize(true)
        rvMovies.adapter = adapter

        srlMovies.setOnRefreshListener {
            refreshMovies()
        }

        btnShowAllMovies.setOnClickListener {
            refreshMovies()
        }

        startListeningToPaginationLoadingState()

        startListeningToMovies()

        initSearchBehaviour()

        viewModel.initialLoad()

        endlessScrollListener = initEndlessScroll()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_search, menu)
        val myActionMenuItem = menu.findItem(R.id.item_search)
        searchView = myActionMenuItem.actionView as SearchView
        searchView?.queryHint = getString(R.string.enter_movie_name)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchListener.onNext(newText)
                return false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        rvMovies.addOnScrollListener(endlessScrollListener)
    }

    private fun refreshMovies() {
        llNoData.gone()
        searchView?.setQuery("", false)
        searchView?.clearFocus()
        toolbar.collapseActionView()

        viewModel.refreshMovies()
    }

    private fun startListeningToPaginationLoadingState() {
        viewModel.paginationLoading.observe(this, Observer {
            if (it) {
                pbLoading.visible()
            } else pbLoading.gone()
        })
    }

    private fun initSearchBehaviour() {
        searchListener
                .debounce(800, TimeUnit.MILLISECONDS)
                .subscribe {
                    endlessScrollListener.resetState()
                    viewModel.searchMovie(it)
                }
                .addTo(viewModel.disposable)
    }

    private fun startListeningToMovies() {
        viewModel.movies.observe(this, Observer { movies ->
            if (movies.isEmpty()) {
                llNoData?.visible()
                rvMovies?.gone()
            } else {
                adapter.swapData(movies)

                Timer().schedule(1000){
                    parentActivity.runOnUiThread {
                        llNoData?.gone()
                        rvMovies?.visible()
                    }
                }
            }
        })
    }

    private fun initEndlessScroll() = object : EndlessScrollListener(
            layoutManager = rvMovies.layoutManager as LinearLayoutManager,
            visibleThreshold = 2) {
        override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
            viewModel.loadNextPage()
        }
    }

    override fun hideLoading() {
        srlMovies.isRefreshing = false
    }

    override fun showLoading() {
        srlMovies.isRefreshing = true
    }

    interface MovieNavigator {
        fun showMovieDetails(movie: com.hafidrf.movieapp.movies.model.MovieSearchModel)
    }

    override fun movieClicked(movie: com.hafidrf.movieapp.movies.model.MovieSearchModel) {
        searchView?.clearFocus()
        navigator?.showMovieDetails(movie)
    }

}

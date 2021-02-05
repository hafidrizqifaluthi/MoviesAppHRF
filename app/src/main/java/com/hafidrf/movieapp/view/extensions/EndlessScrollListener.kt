package com.hafidrf.movieapp.view.extensions

abstract class EndlessScrollListener : androidx.recyclerview.widget.RecyclerView.OnScrollListener {
  private val visibleThreshold: Int
  private var currentPage = 0
  private var previousTotalItemCount = 0
  private var loading = true
  private val startingPageIndex = 0

  private var mLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager

  constructor(
    layoutManager: androidx.recyclerview.widget.LinearLayoutManager,
    visibleThreshold: Int = 5
  ) {
    this.mLayoutManager = layoutManager
    this.visibleThreshold = visibleThreshold
  }

  constructor(
    layoutManager: androidx.recyclerview.widget.GridLayoutManager,
    visibleThreshold: Int = 5
  ) {
    this.mLayoutManager = layoutManager
    this.visibleThreshold = visibleThreshold * layoutManager.spanCount
  }

  constructor(
    layoutManager: androidx.recyclerview.widget.StaggeredGridLayoutManager,
    visibleThreshold: Int = 5
  ) {
    this.mLayoutManager = layoutManager
    this.visibleThreshold = visibleThreshold * layoutManager.spanCount
  }

  private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
    var maxSize = 0
    for (i in lastVisibleItemPositions.indices) {
      if (i == 0) {
        maxSize = lastVisibleItemPositions[i]
      } else if (lastVisibleItemPositions[i] > maxSize) {
        maxSize = lastVisibleItemPositions[i]
      }
    }
    return maxSize
  }

  override fun onScrolled(
    view: androidx.recyclerview.widget.RecyclerView,
    dx: Int,
    dy: Int
  ) {

    val totalItemCount = mLayoutManager.itemCount

    val lastVisibleItemPosition = when (mLayoutManager) {
      is androidx.recyclerview.widget.StaggeredGridLayoutManager -> {
        getLastVisibleItem(
            (mLayoutManager as androidx.recyclerview.widget.StaggeredGridLayoutManager).findLastVisibleItemPositions(
                null
            )
        )
      }
      is androidx.recyclerview.widget.GridLayoutManager ->
        (mLayoutManager as androidx.recyclerview.widget.GridLayoutManager).findLastVisibleItemPosition()
      is androidx.recyclerview.widget.LinearLayoutManager ->
        (mLayoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastVisibleItemPosition()
      else -> 0
    }

    if (totalItemCount < previousTotalItemCount) {
      this.currentPage = this.startingPageIndex
      this.previousTotalItemCount = totalItemCount
      if (totalItemCount == 0) {
        this.loading = true
      }
    }
    if (loading && totalItemCount > previousTotalItemCount) {
      loading = false
      previousTotalItemCount = totalItemCount
    }

    if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
      currentPage++
      onLoadMore(currentPage, totalItemCount, view)
      loading = true
    }
  }

  fun resetState() {
    this.currentPage = this.startingPageIndex
    this.previousTotalItemCount = 0
    this.loading = true
  }

  protected abstract fun onLoadMore(
    page: Int,
    totalItemsCount: Int,
    view: androidx.recyclerview.widget.RecyclerView?
  )

}
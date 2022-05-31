package com.codebrew.clikat.app_utils

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Pagination class to add more items to the list when reach the last item.
 */
abstract class PaginationScrollListener
/**
 * Supporting only LinearLayoutManager for now.
 *
 * @param layoutManager
 */
    (var layoutManager: LinearLayoutManager?) : RecyclerView.OnScrollListener() {

    abstract fun isLastPage(): Boolean

    abstract fun isLoading(): Boolean

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val firstVisibleItemPosition = layoutManager?.findFirstCompletelyVisibleItemPosition()?:0
        if(dy<0)
        {

            if(!isLoading() && firstVisibleItemPosition==0)
            {
                loadMoreItems()
            }
        }
    }
    abstract fun loadMoreItems()
}
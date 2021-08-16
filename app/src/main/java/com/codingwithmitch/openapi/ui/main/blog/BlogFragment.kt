package com.codingwithmitch.openapi.ui.main.blog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_blog.*
import javax.inject.Inject

class BlogFragment : BaseBlogFragment(), BlogListAdapter.Interaction{

    @Inject
    lateinit var requestManager: RequestManager

    private lateinit var recyclerAdapter: BlogListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()
        executeSearch()
        initRecyclerView()
    }

    private fun executeSearch() {
        viewModel.setQuery("")
        viewModel.setStateEvent(
            BlogStateEvent.BlogSearchEvent()
        )
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, { dataState ->
            dataState?.let {
                stateChangeListener.onDataStateChange(it)
                it.data?.let { data ->
                    data.data?.let { event ->
                        event.getContentIfNotHandled()?.let { viewState ->
                            Log.d(TAG, "BlogFragment, DataState: $viewState")
                            viewModel.setBlogListData(viewState.blogFields.blogList)
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
            Log.d(TAG, "BlogFragment, ViewState: $viewState")
            if(viewState != null) {
                recyclerAdapter.submitList(
                    viewState.blogFields.blogList,
                    true
                )
            }
        })
    }

    private fun initRecyclerView() {
        blog_post_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@BlogFragment.context)
            val topSpacingItemDecoration = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingItemDecoration)
            addItemDecoration(topSpacingItemDecoration)
            recyclerAdapter = BlogListAdapter(
                this@BlogFragment,
                requestManager,
            )

            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if(lastPosition == recyclerAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "BlogFragment: attempting to load next page...")
                        // TODO("load next page using ViewModel")
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        blog_post_recyclerview.adapter = null
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        Log.d(TAG, "onItemSelected: $position, $item")
    }
}

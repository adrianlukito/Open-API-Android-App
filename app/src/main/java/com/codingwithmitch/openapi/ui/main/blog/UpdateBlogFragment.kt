package com.codingwithmitch.openapi.ui.main.blog

import android.os.Bundle
import android.view.*
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.UpdatedBlogPostEvent
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import kotlinx.android.synthetic.main.fragment_update_blog.*
import okhttp3.MultipartBody

class UpdateBlogFragment : BaseBlogFragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState.data?.let { data ->
                data.data?.getContentIfNotHandled()?.let { viewState ->
                    // if this is not null, the blogpost was updated...
                    viewState.viewBlogFields.blogPost?.let { blogPost ->
                        // TODO("onBlogpostUpdateSuccess")
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
            viewState.updateBlogFields.let { updateBlogFields ->
                setBlogProperties(updateBlogFields)
            }
        })
    }

    private fun setBlogProperties(updateBlogFields: BlogViewState.UpdateBlogFields) {
        updateBlogFields.run {
            requestManager.load(updatedImageUri).into(blog_image)
            blog_title.setText(updatedBlogTitle)
            blog_body.setText(updatedBlogBody)
        }
    }

    private fun saveChanges() {
        var multipartBody: MultipartBody.Part? = null
        viewModel.setStateEvent(
            UpdatedBlogPostEvent(
                blog_title.text.toString(),
                blog_body.text.toString(),
                multipartBody
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.save -> {
                saveChanges()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
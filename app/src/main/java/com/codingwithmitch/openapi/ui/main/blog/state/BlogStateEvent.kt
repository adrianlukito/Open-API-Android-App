package com.codingwithmitch.openapi.ui.main.blog.state

import okhttp3.MultipartBody

sealed class BlogStateEvent {

    class BlogSearchEvent: BlogStateEvent()

    class CheckAuthorOfBlogPost: BlogStateEvent()

    class DeleteBlogPostEvent: BlogStateEvent()

    data class UpdatedBlogPostEvent(
        var title: String,
        var body: String,
        var image: MultipartBody.Part?
    ): BlogStateEvent()

    class None: BlogStateEvent()
}
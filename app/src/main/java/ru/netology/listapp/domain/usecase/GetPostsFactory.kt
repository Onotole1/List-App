package ru.netology.listapp.domain.usecase

import arrow.core.right

object GetPostsFactory {
    operator fun invoke(
        savePostsLocal: SavePostsLocal,
        getPostsLocal: GetPostsLocal,
        getPostsRemote: GetPostsRemote,
    ): GetPosts = { page ->
        val localPosts = getPostsLocal(page)
        if (localPosts.isEmpty()) {
            getPostsRemote(page).map { remotePosts ->
                savePostsLocal(remotePosts)
                getPostsLocal(page)
            }
        } else {
            localPosts.right()
        }
    }
}

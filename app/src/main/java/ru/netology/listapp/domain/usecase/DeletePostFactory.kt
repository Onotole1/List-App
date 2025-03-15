package ru.netology.listapp.domain.usecase

import ru.netology.listapp.domain.model.PostId

object DeletePostFactory {
    operator fun invoke(deleteFromLocal: suspend (PostId) -> Unit): DeletePost =
        { postId, currentPosts ->
            deleteFromLocal(postId)
            currentPosts.filterNot { it.id == postId }
        }
}

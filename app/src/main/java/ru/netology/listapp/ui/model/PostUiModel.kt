package ru.netology.listapp.ui.model

import ru.netology.listapp.domain.model.Post
import ru.netology.listapp.domain.model.PostContent
import ru.netology.listapp.domain.model.PostId
import java.time.Instant

data class PostUiModel(
    val id: PostId,
    val content: PostContent,
    val dateFormatted: String,
) {
    companion object {
        fun fromDomain(
            post: Post,
            formatDate: (Instant) -> String,
        ): PostUiModel = with(post) {
            PostUiModel(
                id = id,
                content = content,
                dateFormatted = formatDate(createdAt)
            )
        }
    }
}

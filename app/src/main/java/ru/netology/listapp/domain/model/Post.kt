package ru.netology.listapp.domain.model

import java.time.Instant

@JvmInline
value class PostId(val value: String)

data class Post(
    val id: PostId,
    val content: PostContent,
    val createdAt: Instant,
)

package ru.netology.listapp.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.netology.listapp.domain.model.Post
import ru.netology.listapp.domain.model.PostId
import ru.netology.listapp.domain.usecase.DeterminePostContent
import java.time.Instant

@Serializable
data class PostDtoItem(
    @SerialName("id")
    val id: String,
    @SerialName("text")
    val text: String,
    @SerialName("time")
    val time: Long
) {
    fun toDomain() = Post(
        id = PostId(id),
        content = DeterminePostContent(text),
        createdAt = Instant.ofEpochMilli(time)
    )
}

package ru.netology.listapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.listapp.domain.model.Post
import ru.netology.listapp.domain.model.PostId
import ru.netology.listapp.domain.usecase.DeterminePostContent
import java.time.Instant

@Entity("Post")
data class PostEntity(
    @PrimaryKey
    @ColumnInfo("id")
    val id: String,
    @ColumnInfo("text")
    val text: String,
    @ColumnInfo("time")
    val time: Long,
    // Чтобы пагинация не ломалась, будем удалять таким способом
    @ColumnInfo("deleted")
    val deleted: Boolean = false,
) {
    fun toPost() = Post(
        PostId(id),
        DeterminePostContent(text),
        Instant.ofEpochMilli(time)
    )

    companion object {
        fun fromPost(post: Post) =
            with(post) {
                PostEntity(id = id.value, text = content.value, time = createdAt.toEpochMilli())
            }
    }
}

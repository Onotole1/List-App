package ru.netology.listapp.db.dao

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.listapp.db.ListAppDb
import ru.netology.listapp.db.entity.PostEntity
import ru.netology.listapp.domain.model.Post
import ru.netology.listapp.domain.model.PostId

@Dao
interface PostDao {
    companion object {
        private const val DEFAULT_PAGE_SIZE = 50

        @Composable
        fun getPostDao(): PostDao {
            val context = LocalContext.current
            return ListAppDb.getInstance { context }.postDao
        }
    }

    @Query("SELECT * FROM Post LIMIT :limit OFFSET :offset")
    suspend fun getPaginatedInternal(limit: Int, offset: Int): List<PostEntity>

    suspend fun getPaginated(page: Int): List<Post> {
        val offset = page * DEFAULT_PAGE_SIZE
        return getPaginatedInternal(DEFAULT_PAGE_SIZE, offset)
            .filterNot { it.deleted }
            .map { it.toPost() }
    }

    @Insert
    suspend fun insertPostsInternal(posts: List<PostEntity>)

    suspend fun insertPosts(posts: List<Post>) {
        insertPostsInternal(posts.map(PostEntity::fromPost))
    }

    @Query("UPDATE Post SET deleted = 1 WHERE id = :id")
    suspend fun deletePostByIdInternal(id: String)

    suspend fun deletePostById(id: PostId) {
        deletePostByIdInternal(id.value)
    }
}

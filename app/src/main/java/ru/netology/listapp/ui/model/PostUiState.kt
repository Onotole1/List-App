package ru.netology.listapp.ui.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import ru.netology.listapp.api.ApiError
import ru.netology.listapp.domain.model.Post
import ru.netology.listapp.domain.usecase.DeletePost
import ru.netology.listapp.domain.usecase.GetPosts

data class PostUiState(
    val posts: List<Post> = emptyList(),
    val status: PostLoadState = PostLoadState.Idle,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
) {
    private val isLoading: Boolean
        get() = status is PostLoadState.Loading
    val isLoadingMore: Boolean
        get() = isLoading && posts.isNotEmpty()
    val showFullLoading: Boolean
        get() = isLoading && posts.isEmpty()
    val loadingAvailable: Boolean
        get() = status !is PostLoadState.Loading && hasMore

    sealed interface PostLoadState {
        data class Error(val apiError: ApiError) : PostLoadState
        data object Loading : PostLoadState
        data object Idle : PostLoadState
    }

    companion object {
        val EMPTY = PostUiState()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun Flow<PostAction>.load(
    getCurrentState: () -> PostUiState,
    getPosts: GetPosts,
    deletePost: DeletePost,
): Flow<Mutation<PostUiState>> =
    filter { action ->
        if (action is PostAction.Load) {
            getCurrentState().loadingAvailable
        } else {
            true
        }
    }
        .flatMapLatest { action ->
            flow {
                when (action) {
                    is PostAction.Load -> loadNextPage(getCurrentState, getPosts)
                    is PostAction.DeletePost -> deletePost(getCurrentState, deletePost, action)
                }
            }
        }

private suspend fun FlowCollector<Mutation<PostUiState>>.deletePost(
    getCurrentState: () -> PostUiState,
    deletePost: DeletePost,
    action: PostAction.DeletePost
) {
    val updatedPosts = deletePost(action.id, getCurrentState().posts)
    emit {
        copy(posts = updatedPosts)
    }
}

private suspend fun FlowCollector<Mutation<PostUiState>>.loadNextPage(
    getCurrentState: () -> PostUiState,
    getPosts: GetPosts
) {
    emit {
        copy(status = PostUiState.PostLoadState.Loading)
    }

    val currentState = getCurrentState()
    val currentPage = currentState.currentPage

    getPosts(currentPage).fold(
        ifLeft = { error ->
            emit {
                copy(status = PostUiState.PostLoadState.Error(error))
            }
        },
        ifRight = { newPosts ->
            val updatedPosts = currentState.posts + newPosts
            emit {
                copy(
                    posts = updatedPosts,
                    status = PostUiState.PostLoadState.Idle,
                    currentPage = currentPage + 1,
                    hasMore = newPosts.isNotEmpty(),
                )
            }
        },
    )
}

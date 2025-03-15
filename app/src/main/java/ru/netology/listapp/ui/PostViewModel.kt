package ru.netology.listapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.netology.listapp.api.getPosts
import ru.netology.listapp.db.dao.PostDao
import ru.netology.listapp.domain.model.PostId
import ru.netology.listapp.domain.usecase.DeletePostFactory
import ru.netology.listapp.domain.usecase.GetPostsFactory
import ru.netology.listapp.ui.model.PostAction
import ru.netology.listapp.ui.model.PostUiState
import ru.netology.listapp.ui.model.load

class PostViewModel(
    private val httpClient: HttpClient,
    private val dao: PostDao,
) : ViewModel() {
    // Поток действий от UI
    private val _actions = MutableSharedFlow<PostAction>()
    val actions = _actions.asSharedFlow()

    // StateFlow для подписки UI
    val state: StateFlow<PostUiState> = actions
        .load(
            getCurrentState = { state.value },
            getPosts = GetPostsFactory(
                savePostsLocal = dao::insertPosts,
                getPostsLocal = dao::getPaginated,
                getPostsRemote = httpClient::getPosts
            ),
            deletePost = DeletePostFactory(
                deleteFromLocal = dao::deletePostById,
            ),
        )
        .scan(
            initial = PostUiState.EMPTY,
            operation = { currentState, mutation -> mutation(currentState) },
        )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PostUiState.EMPTY,
        )

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        viewModelScope.launch {
            _actions.emit(PostAction.Load)
        }
    }

    fun deletePost(id: PostId) {
        viewModelScope.launch {
            _actions.emit(PostAction.DeletePost(id = id))
        }
    }
}

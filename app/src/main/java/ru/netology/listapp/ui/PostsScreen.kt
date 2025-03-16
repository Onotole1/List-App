package ru.netology.listapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.filter
import ru.netology.listapp.api.ApiClient
import ru.netology.listapp.api.getApiErrorTexts
import ru.netology.listapp.db.dao.PostDao
import ru.netology.listapp.domain.model.PostId
import ru.netology.listapp.ui.model.PostUiModel
import ru.netology.listapp.ui.model.PostUiState
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PostsScreen(
    showMessage: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
) {
    val dao = PostDao.getPostDao()
    val viewModel = viewModel {
        PostViewModel(ApiClient, dao)
    }

    val state by viewModel.state.collectAsState()

    val apiErrorTexts = getApiErrorTexts()
    LaunchedEffect(state.status) {
        (state.status as? PostUiState.PostLoadState.Error)?.let { error ->
            showMessage(apiErrorTexts(error.apiError))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (state.showFullLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            val formatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy") }
            PostList(
                contentPadding = contentPadding,
                posts = state.posts.map {
                    PostUiModel.fromDomain(it) { createdAt ->
                        formatter.format(
                            createdAt.atZone(ZoneId.systemDefault())
                        )
                    }
                },
                onDelete = viewModel::deletePost,
                onLoadMore = viewModel::loadNextPage,
                isLoadingMore = state.isLoadingMore,
            )
        }
    }
}

@Composable
private fun PostList(
    posts: List<PostUiModel>,
    onDelete: (PostId) -> Unit,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean,
    contentPadding: PaddingValues,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1
        }
            .filter { it }
            .collect {
                onLoadMore()
            }
    }

    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        modifier = Modifier.fillMaxSize(),
    ) {
        items(
            items = posts,
            key = {
                it.id.value
            },
        ) { post ->
            // Обертка для свайпа
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) {
                        onDelete(post.id)
                        true
                    } else {
                        false
                    }
                }
            )

            SwipeToDismissBox(
                dismissState,
                backgroundContent = {
                    val direction = dismissState.dismissDirection
                    if (direction == SwipeToDismissBoxValue.EndToStart) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(end = 16.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete"
                                )
                            }
                        }
                    }
                },
                content = {
                    PostCard(
                        postUiModel = post,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                    )
                }
            )
        }

        // Индикатор загрузки следующей страницы
        if (isLoadingMore) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
            }
        }
    }
}
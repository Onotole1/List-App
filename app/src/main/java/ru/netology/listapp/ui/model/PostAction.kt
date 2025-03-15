package ru.netology.listapp.ui.model

import ru.netology.listapp.domain.model.PostId

sealed interface PostAction {
    data object Load : PostAction
    data class DeletePost(val id: PostId) : PostAction
}

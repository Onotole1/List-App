package ru.netology.listapp.domain.usecase

import ru.netology.listapp.domain.model.Post
import ru.netology.listapp.domain.model.PostId

typealias DeletePost = suspend (id: PostId, currentPosts: List<Post>) -> List<Post>
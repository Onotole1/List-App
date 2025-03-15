package ru.netology.listapp.domain.usecase

import ru.netology.listapp.domain.model.Post

typealias SavePostsLocal = suspend (List<Post>) -> Unit

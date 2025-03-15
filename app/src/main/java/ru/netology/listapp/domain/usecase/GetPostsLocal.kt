package ru.netology.listapp.domain.usecase

import ru.netology.listapp.domain.model.Post

typealias GetPostsLocal = suspend (Int) -> List<Post>

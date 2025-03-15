package ru.netology.listapp.domain.usecase

import arrow.core.Either
import ru.netology.listapp.api.ApiError
import ru.netology.listapp.domain.model.Post

typealias GetPostsRemote = suspend (Int) -> Either<ApiError, List<Post>>

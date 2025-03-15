package ru.netology.listapp.api

import arrow.core.Either
import arrow.core.recover
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import ru.netology.listapp.api.dto.PostDtoItem
import ru.netology.listapp.domain.model.Post
import java.net.UnknownHostException

@OptIn(ExperimentalSerializationApi::class)
suspend fun HttpClient.getPosts(page: Int): Either<ApiError, List<Post>> =
    Either.catch {
        get(
            "https://raw.githubusercontent.com/Onotole1/Messages-Mocks/refs/heads/main/page_$page"
        )
            .bodyAsChannel()
            .toInputStream()
            .let { Json.decodeFromStream<List<PostDtoItem>>(it) }
            .map { it.toDomain() }
    }
        .recover {
            when (it) {
                is ConnectTimeoutException, is UnknownHostException -> raise(ApiError.NetworkError)
                is ClientRequestException -> {
                    when (it.response.status) {
                        it.response.status -> emptyList()
                        else -> raise(ApiError.UnknownError)
                    }
                }

                else -> raise(ApiError.UnknownError)
            }
        }

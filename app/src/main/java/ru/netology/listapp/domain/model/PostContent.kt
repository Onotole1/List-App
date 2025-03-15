package ru.netology.listapp.domain.model

sealed interface PostContent {
    val value: String

    @JvmInline
    value class Text(override val value: String) : PostContent

    @JvmInline
    value class Image(override val value: String) : PostContent
}

package ru.netology.listapp.domain.usecase

import ru.netology.listapp.domain.model.PostContent
import java.net.URI

object DeterminePostContent {
    operator fun invoke(content: String): PostContent {
        val isValidUrl = runCatching {
            URI(content).toURL()
            true
        }
            .getOrDefault(false)

        return if (isValidUrl) {
            PostContent.Image(content)
        } else {
            PostContent.Text(content)
        }
    }
}

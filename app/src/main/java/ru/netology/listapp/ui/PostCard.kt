package ru.netology.listapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.netology.listapp.domain.model.PostContent
import ru.netology.listapp.domain.model.PostId
import ru.netology.listapp.ui.model.PostUiModel

@Composable
fun PostCard(postUiModel: PostUiModel, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Дата поста
            Text(
                text = postUiModel.dateFormatted,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Контент поста
            when (val content = postUiModel.content) {
                is PostContent.Text -> {
                    Text(
                        text = content.value,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                is PostContent.Image -> {
                    AsyncImage(
                        model = content.value,
                        contentDescription = "Post image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(bottom = 8.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // ID поста
            Text(
                text = "ID: ${postUiModel.id.value}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview
@Composable
fun PostCardPreview() {
    Column {
        // Текстовый пост
        PostCard(
            postUiModel = PostUiModel(
                id = PostId("123"),
                content = PostContent.Text(
                    "Это пример текстового поста с каким-то интересным содержимым"
                ),
                dateFormatted = "11 марта 2025"
            )
        )

        // Пост с изображением
        PostCard(
            postUiModel = PostUiModel(
                id = PostId("124"),
                content = PostContent.Image(
                    "https://github.com/Onotole1/Messages-Mocks/blob/main/pic/62c4307cfeb411ef974cca4dfd70a37b-1.jpeg"
                ),
                dateFormatted = "11 марта 2025"
            )
        )
    }
}

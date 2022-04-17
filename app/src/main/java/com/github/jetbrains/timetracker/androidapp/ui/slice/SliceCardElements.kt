package com.github.jetbrains.timetracker.androidapp.ui.slice

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.jetbrains.timetracker.androidapp.model.WorkActivity
import java.util.*

@Composable
fun DescriptionText(workActivity: WorkActivity) {
    Text(
        style = MaterialTheme.typography.body1,
        overflow = TextOverflow.Ellipsis,
        softWrap = false,
        text = workActivity.description.value,
    )
    if (workActivity.project != null) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                style = MaterialTheme.typography.caption,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                text = workActivity.project.value + ": " + (workActivity.task?.value ?: "")
            )
        }
    }
}

@Composable
fun Subtitle(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier.padding(start = 12.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
        style = MaterialTheme.typography.overline,
        text = text.uppercase(Locale.getDefault())
    )
}

@Composable
fun ActionButton(
    modifier: Modifier,
    contentDescription: String,
    imageVector: ImageVector,
    color: Color,
    size: Dp,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(size),
            tint = color,
        )
    }
}

package dev.sayed.mehrabalmomen.presentation.utils

import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePicker(onImagePicked: (String) -> Unit): () -> Unit {
    return {}
}

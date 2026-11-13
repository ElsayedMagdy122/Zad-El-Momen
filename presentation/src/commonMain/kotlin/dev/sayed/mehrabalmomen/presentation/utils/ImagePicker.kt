package dev.sayed.mehrabalmomen.presentation.utils

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePicker(onImagePicked: (String) -> Unit): () -> Unit

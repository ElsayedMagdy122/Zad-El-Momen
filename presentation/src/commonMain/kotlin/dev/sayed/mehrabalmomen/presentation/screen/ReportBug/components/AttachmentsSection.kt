package dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.ReportBugViewModel

@Composable
expect fun AttachmentsSection(
    viewModel: ReportBugViewModel,
    selectedImageUri: String? = null,
    modifier: Modifier = Modifier
)

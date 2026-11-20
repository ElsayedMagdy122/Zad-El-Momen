package dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.ReportBugViewModel
import org.jetbrains.compose.resources.painterResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.attachments
import zadelmomen.presentation.generated.resources.click_to_upload_image
import zadelmomen.presentation.generated.resources.ic_add_photo
import zadelmomen.presentation.generated.resources.ic_pencil_edit

@Composable
actual fun AttachmentsSection(
    viewModel: ReportBugViewModel,
    selectedImageUri: String?,
    modifier: Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.onImageSelected(it.toString())
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionTitle(text = localizedString(Res.string.attachments))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .dashedBorder(
                    width = 2.dp,
                    color = Theme.color.secondary.shadeSecondary,
                    shape = RoundedCornerShape(12.dp),
                    dashOn = 8.dp,
                    dashOff = 6.dp
                )
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        launcher.launch("image/*")
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(selectedImageUri)
                        .build(),
                    contentDescription = "Selected image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Theme.color.primary.primary)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_pencil_edit),
                        contentDescription = "Change image",
                        tint = Theme.color.primary.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Theme.color.surfaces.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_add_photo),
                            contentDescription = null,
                            tint = Theme.color.primary.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text(
                        text = localizedString(Res.string.click_to_upload_image),
                        color = Theme.color.primary.shadePrimary,
                        style = Theme.textStyle.body.small
                    )
                }
            }
        }
    }
}

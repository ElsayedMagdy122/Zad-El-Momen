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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.ReportBugViewModel

@Composable
fun AttachmentsSection(
    viewModel: ReportBugViewModel,
    selectedImageUri: String? = null,
    modifier: Modifier = Modifier
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
        SectionTitle(localizedString(R.string.attachments))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .dashedBorder(
                    width = 3.dp,
                    color = Theme.color.surfaces.surfaceHigh,
                    shape = RoundedCornerShape(12.dp),
                    dashOn = 8.dp,
                    dashOff = 6.dp
                )
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(
                        bounded = true,
                        radius = Dp.Unspecified,
                        color = Color.Unspecified
                    ),
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
                        .crossfade(true)
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
                        painter = painterResource(R.drawable.ic_pencil_edit),
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
                            .background(Theme.color.surfaces.surfaceHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add_photo),
                            contentDescription = null,
                            tint = Theme.color.primary.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text(
                        text = localizedString(R.string.click_to_upload_image),
                        color = Theme.color.secondary.shadeSecondary,
                        style = Theme.textStyle.body.small
                    )

                }
            }
        }
    }
}

fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    shape: Shape,
    dashOn: Dp = 8.dp,
    dashOff: Dp = 6.dp
) = composed {

    this
        .clip(shape)
        .drawWithContent {
            drawContent()

            val strokeWidthPx = width.toPx()

            val stroke = Stroke(
                width = strokeWidthPx,
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(dashOn.toPx(), dashOff.toPx()),
                    phase = 0f
                )
            )

            val cornerRadiusPx = when (shape) {
                is RoundedCornerShape -> {
                    val density = this
                    val size = this.size

                    maxOf(
                        shape.topStart.toPx(size, density),
                        shape.topEnd.toPx(size, density),
                        shape.bottomStart.toPx(size, density),
                        shape.bottomEnd.toPx(size, density)
                    )
                }

                else -> 0f
            }

            drawRoundRect(
                color = color,
                topLeft = Offset.Zero,
                size = size,
                cornerRadius = CornerRadius(cornerRadiusPx),
                style = stroke
            )
        }
}

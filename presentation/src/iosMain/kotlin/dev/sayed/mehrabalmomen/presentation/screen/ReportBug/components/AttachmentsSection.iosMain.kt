package dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components

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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.ReportBugViewModel
import org.jetbrains.compose.resources.painterResource
import platform.Foundation.NSDate
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.writeToFile
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.darwin.NSObject
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.attachments
import zadelmomen.presentation.generated.resources.click_to_upload_image
import zadelmomen.presentation.generated.resources.ic_add_photo
import zadelmomen.presentation.generated.resources.ic_pencil_edit

private class ImagePickerDelegate(
    private val onImagePicked: (String) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        if (image != null) {
            val jpegData = UIImageJPEGRepresentation(image, 0.8)
            if (jpegData != null) {
                val tempDir = NSTemporaryDirectory()
                val tempPath = "$tempDir/bug_report_${NSDate().timeIntervalSince1970}.jpg"
                jpegData.writeToFile(tempPath, atomically = true)
                onImagePicked(tempPath)
            }
        }
        picker.dismissViewControllerAnimated(true, completion = null)
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
    }
}

private fun getRootViewController(): UIViewController? {
    val keyWindow = UIApplication.sharedApplication.connectedScenes
        .mapNotNull { it as? UIWindowScene }
        .flatMap { it.windows }
        .firstOrNull { (it as? UIWindow)?.isKeyWindow() == true } as? UIWindow
        ?: UIApplication.sharedApplication.keyWindow

    var topController = keyWindow?.rootViewController
    while (topController?.presentedViewController != null) {
        topController = topController.presentedViewController
    }
    return topController
}

@Composable
actual fun AttachmentsSection(
    viewModel: ReportBugViewModel,
    selectedImageUri: String?,
    modifier: Modifier
) {
    val delegate = remember(viewModel) {
        ImagePickerDelegate { path ->
            viewModel.onImageSelected(path)
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
                    width = 3.dp,
                    color = Theme.color.surfaces.surfaceHigh,
                    shape = RoundedCornerShape(12.dp),
                    dashOn = 8.dp,
                    dashOff = 6.dp
                )
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        val picker = UIImagePickerController().apply {
                            sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
                            this.delegate = delegate
                        }
                        getRootViewController()?.presentViewController(picker, animated = true, completion = null)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
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
                        .background(Theme.color.primary.primary),
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
                            .background(Theme.color.surfaces.surfaceHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_add_photo),
                            contentDescription = null,
                            tint = Theme.color.primary.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text(
                        text = localizedString(Res.string.click_to_upload_image),
                        color = Theme.color.secondary.shadeSecondary,
                        style = Theme.textStyle.body.small
                    )
                }
            }
        }
    }
}

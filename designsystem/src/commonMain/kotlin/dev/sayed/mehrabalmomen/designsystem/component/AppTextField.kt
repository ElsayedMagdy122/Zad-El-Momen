package dev.sayed.mehrabalmomen.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField as FoundationBasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.designsystem.theme.Theme

@Composable
fun AppBasicTextField(
    value: String,
    hint: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    title: String? = null,
    containerColor: Color = Theme.color.surfaces.surfaceHigh,
    leadingIconTint: Color = Theme.color.primary.shadePrimary,
    trailingIconTint: Color = Theme.color.secondary.shadeSecondary,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    showTrailingDivider: Boolean = false,
    errorMessage: String? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = FocusRequester(),
    onFocusChanged: (Boolean) -> Unit = {},
    onTrailingIconClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    maxCharacters: Int = Int.MAX_VALUE,
) {
    Column(modifier) {
        title?.let {
            Text(
                text = title,
                style = Theme.textStyle.label.medium,
                modifier = Modifier.padding(bottom = 6.dp),
                color = Theme.color.primary.shadePrimary
            )
        }

        Row(Modifier.fillMaxWidth()) {
            leadingContent?.let {
                leadingContent()
                Spacer(Modifier.width(4.dp))
            }

            FoundationBasicTextField(
                value = value,
                onValueChange = {
                    if (it.length <= maxCharacters) {
                        onValueChanged(it)
                    }
                },
                enabled = enabled,
                readOnly = readOnly,
                minLines = minLines,
                maxLines = if (singleLine) 1 else maxLines,
                textStyle = Theme.textStyle.body.medium.copy(
                    color = Theme.color.primary.shadePrimary
                ),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                cursorBrush = SolidColor(Theme.color.primary.primary),
                decorationBox = { innerTextField ->
                    TextFieldContent(
                        innerTextField = innerTextField,
                        text = value,
                        isError = isError,
                        singleLine = singleLine,
                        hint = hint,
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon,
                        onTrailingIconClick = onTrailingIconClick,
                        showTrailingDivider = showTrailingDivider,
                        leadingIconTint = leadingIconTint,
                        trailingIconTint = trailingIconTint
                    )
                },
                visualTransformation = visualTransformation,
                modifier = Modifier
                    .weight(1f)
                    .clip(shape)
                    .background(color = containerColor)
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        onFocusChanged(it.isFocused)
                    }
            )
        }

        errorMessage?.let {
            Text(
                text = errorMessage,
                style = Theme.textStyle.label.small,
                modifier = Modifier.padding(
                    start = 12.dp,
                    top = 4.dp
                ),
                color = Theme.color.semantic.error
            )
        }
    }
}

@Composable
fun AppTextField(
    value: String,
    hint: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    title: String? = null,
    containerColor: Color = Theme.color.surfaces.surfaceHigh,
    leadingIconTint: Color = Theme.color.primary.shadePrimary,
    trailingIconTint: Color = Theme.color.semantic.shadeTertiary,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    showTrailingDivider: Boolean = false,
    errorMessage: String? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = FocusRequester(),
    onFocusChanged: (Boolean) -> Unit = {},
    onTrailingIconClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    maxCharacters: Int = Int.MAX_VALUE,
) {
    AppBasicTextField(
        value = value,
        hint = hint,
        onValueChanged = onValueChanged,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        title = title,
        containerColor = containerColor,
        leadingIconTint = leadingIconTint,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        showTrailingDivider = showTrailingDivider,
        errorMessage = errorMessage,
        shape = shape,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        focusRequester = focusRequester,
        onFocusChanged = onFocusChanged,
        onTrailingIconClick = onTrailingIconClick,
        visualTransformation = visualTransformation,
        trailingIconTint = trailingIconTint,
        maxCharacters = maxCharacters
    )
}

@Composable
fun MultiLineTextField(
    value: String,
    hint: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    minLines: Int = 3,
    maxLines: Int = 5,
    title: String? = null,
    containerColor: Color = Theme.color.surfaces.surfaceHigh,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = FocusRequester(),
    onFocusChanged: (Boolean) -> Unit = {},
    maxCharacters: Int = Int.MAX_VALUE,
) {
    AppBasicTextField(
        value = value,
        hint = hint,
        onValueChanged = onValueChanged,
        modifier = modifier,
        title = title,
        containerColor = containerColor,
        singleLine = false,
        minLines = minLines,
        maxLines = maxLines,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        errorMessage = errorMessage,
        shape = shape,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        focusRequester = focusRequester,
        onFocusChanged = onFocusChanged,
        maxCharacters = maxCharacters
    )
}

@Composable
private fun TextFieldContent(
    innerTextField: @Composable () -> Unit,
    text: String,
    hint: String,
    leadingIcon: Painter?,
    trailingIcon: Painter?,
    leadingIconTint: Color,
    trailingIconTint: Color,
    isError: Boolean,
    singleLine: Boolean,
    showTrailingDivider: Boolean = true,
    onTrailingIconClick: (() -> Unit)? = null,
) {
    val animatedIconErrorColor by animateColorAsState(
        targetValue = if (isError) Theme.color.semantic.error else leadingIconTint,
        label = "IconErrorColor"
    )

    Row(
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 14.dp)
            .fillMaxWidth(),
        verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top
    ) {
        leadingIcon?.let {
            Icon(
                painter = leadingIcon,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(20.dp),
                tint = animatedIconErrorColor
            )
        }

        InnerTextFieldWithHint(
            innerTextField = innerTextField,
            text = text,
            hint = hint,
            singleLine = singleLine,
            modifier = Modifier.weight(1f)
        )

        trailingIcon?.let {
            if (showTrailingDivider) {
                VerticalDivider()
            }
            Icon(
                painter = trailingIcon,
                tint = trailingIconTint,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(
                        enabled = onTrailingIconClick != null,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onTrailingIconClick?.invoke()
                    }
            )
        }
    }
}

@Composable
private fun InnerTextFieldWithHint(
    innerTextField: @Composable () -> Unit,
    text: String,
    hint: String,
    singleLine: Boolean,
    modifier: Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
    ) {
        innerTextField()
        if (text.isEmpty()) {
            Text(
                text = hint,
                style = Theme.textStyle.body.small,
                color = Theme.color.semantic.shadeTertiary
            )
        }
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        Modifier
            .padding(horizontal = 8.dp)
            .size(1.dp, 20.dp)
            .background(Theme.color.semantic.disabled.copy(alpha = 0.5f))
    )
}

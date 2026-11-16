package dev.sayed.mehrabalmomen.presentation.base

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun localizedString(res: StringResource): String = stringResource(res)

@Composable
fun localizedString(res: StringResource, vararg formatArgs: Any): String = stringResource(res, *formatArgs)

package dev.sayed.mehrabalmomen.presentation.utils

import dev.sayed.mehrabalmomen.domain.model.AppSettings

expect fun getHijriDateString(language: AppSettings.Language): String

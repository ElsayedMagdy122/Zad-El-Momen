package dev.sayed.mehrabalmomen.presentation.screen.radio

import dev.sayed.mehrabalmomen.domain.entity.radio.Category

fun Category.toUi(): RadioUiState.CategoryUi{
    return RadioUiState.CategoryUi(
        id = this.id,
        nameAr = nameAr,
        nameEn = nameEn
    )
}
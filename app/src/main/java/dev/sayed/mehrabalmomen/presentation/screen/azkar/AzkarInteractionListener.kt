package dev.sayed.mehrabalmomen.presentation.screen.azkar

interface AzkarInteractionListener {
    fun onClickCategory(title: String)
    fun onClickBack()
    fun onOpenAddCategoryBottomSheet()
    fun onDismissAddCategoryBottomSheet()
    fun onNewCategoryTitleChanged(title: String)
    fun onSaveNewCategory()
    fun onSelectTimeFilter(filter: AzkarTimeFilter)
}
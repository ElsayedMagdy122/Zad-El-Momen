package dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails

interface AzkarDetailInteractionListener {
    fun onClickBack()
    fun onDecrementCount(index: Int)
    fun onOpenAddDhikrBottomSheet()
    fun onDismissAddDhikrBottomSheet()
    fun onNewContentChanged(v: String)
    fun onNewCountChanged(v: String)
    fun onNewDescriptionChanged(v: String)
    fun onNewReferenceChanged(v: String)
    fun onSaveNewDhikr()
    fun onPlayAll()
    fun onCopyDhikr(content: String)
}
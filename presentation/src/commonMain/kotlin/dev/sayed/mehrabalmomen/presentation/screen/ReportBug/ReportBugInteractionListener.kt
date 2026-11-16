package dev.sayed.mehrabalmomen.presentation.screen.ReportBug

interface ReportBugInteractionListener {
    fun onTitleChange(value: String)
    fun onDescriptionChange(value: String)
    fun onFeatureSelected(value: FeatureArea)
    fun onImageSelected(url: String)
    fun onSubmitClick()
}
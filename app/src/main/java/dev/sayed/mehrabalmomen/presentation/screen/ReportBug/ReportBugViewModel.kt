package dev.sayed.mehrabalmomen.presentation.screen.ReportBug

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.data.bugReport.remote.DailyLimitExceededException
import dev.sayed.mehrabalmomen.domain.model.BugReportRequest
import dev.sayed.mehrabalmomen.domain.repository.bugReport.BugReportRepository
import dev.sayed.mehrabalmomen.domain.utils.Logger
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.base.UiText

class ReportBugViewModel(
    private val bugReportRepository: BugReportRepository,
    private val logger: Logger
) : BaseViewModel<ReportBugUiState, ReportBugEffect>(ReportBugUiState()),
    ReportBugInteractionListener {

    override fun onTitleChange(value: String) {
        updateState { it.copy(title = value) }
    }

    override fun onDescriptionChange(value: String) {
        updateState { it.copy(description = value) }
    }

    override fun onFeatureSelected(value: FeatureArea) {
        updateState { it.copy(feature = value) }
    }

    override fun onImageSelected(url: String) {
        updateState { it.copy(imageUrl = url) }
    }

    override fun onSubmitClick() {
        val state = screenState.value

        if (state.title.isBlank() || state.description.isBlank()) {
            sendEffect(ReportBugEffect.InvalidInput)
            return
        }

        tryToCall(
            block = {
                bugReportRepository
                    .submitBugReport(
                        BugReportRequest(
                            title = state.title,
                            description = state.description,
                            imageUrl = state.imageUrl,
                            featureArea = state.feature.name
                        )
                    )

            },
            onStart = {
                updateState { it.copy(isLoading = true) }
            },
            onSuccess = {
                updateState { it.copy(isLoading = false) }
                sendEffect(ReportBugEffect.Success)
                updateState { ReportBugUiState() }
            },
            onError = { throwable ->
                updateState { it.copy(isLoading = false) }
                logger.e(
                    "ReportBugViewModel",
                    "Error submitting bug report ${throwable.message.toString()}"
                )
                when (throwable) {
                    is DailyLimitExceededException -> sendEffect(ReportBugEffect.LimitReached)
                    else -> {
                        logger.e("ReportBugViewModel", "Error submitting bug report ${throwable.message}")
                        sendEffect(ReportBugEffect.Error(UiText.StringResource(R.string.failed_to_send_report)))
                    }
                }
            }
        )
    }
}

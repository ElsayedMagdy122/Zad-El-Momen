package dev.sayed.mehrabalmomen.presentation.screen.maps

import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.ic_check_circle
import dev.sayed.mehrabalmomen.designsystem.resources.ic_close_circle
import dev.sayed.mehrabalmomen.designsystem.resources.location_saved
import dev.sayed.mehrabalmomen.designsystem.resources.no_internet_connection
import dev.sayed.mehrabalmomen.designsystem.resources.please_connect_to_the_internet_to_continue
import dev.sayed.mehrabalmomen.designsystem.resources.your_location_has_been_saved_successfully
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class MapsViewModel(
    private val settingsRepository: SettingsRepository,
    private val locationRepository: LocationRepository,
    private val analyticsTracker: AnalyticsTracker
) : BaseViewModel<MapsUiState, MapsEffect>(MapsUiState()), MapsInteractionListener {

    init {
        loadUserCurrentLocation()
    }

    fun onScreenOpened() {
        analyticsTracker.logScreen("maps")
    }

    private fun loadUserCurrentLocation() {
        tryToCall(
            block = { locationRepository.getLocation() },
            onSuccess = { location ->
                val locUi = MapsUiState.LocationUi(location.latitude, location.longitude)
                updateState {
                    it.copy(
                        userCurrentLocation = locUi,
                        selectedLocation = locUi,
                        placeName = location.country,
                        addressLine = location.state
                    )
                }
                sendEffect(MapsEffect.MoveCamera(location.latitude, location.longitude))
            },
            onError = {}
        )
    }

    fun onMapStateChanged(state: MapsUiState.MapLoadState) {
        updateState {
            it.copy(mapLoadState = state)
        }
    }

    override fun onDetectLocationClicked() {
        tryToCall(
            block = { locationRepository.getCurrentDeviceLocation() },
            onSuccess = { location ->
                onDetectLocationSuccess(location.latitude, location.longitude)
            },
            onError = {
                sendEffect(
                    MapsEffect.ShowToast(
                        ToastDetails(
                            title = Res.string.no_internet_connection,
                            message = Res.string.please_connect_to_the_internet_to_continue,
                            icon = Res.drawable.ic_close_circle
                        )
                    )
                )
                updateState {
                    it.copy(isSuccessToast = false)
                }
            }
        )
    }

    fun onDetectLocationSuccess(lat: Double, lng: Double) {
        tryToCall(
            block = { locationRepository.getLocation(lat, lng) },
            onSuccess = { loc ->
                updateState {
                    it.copy(
                        selectedLocation = MapsUiState.LocationUi(loc.latitude, loc.longitude),
                        placeName = loc.country,
                        addressLine = loc.state,
                        isBottomSheetVisible = true
                    )
                }
                sendEffect(MapsEffect.MoveCamera(lat, lng))
            },
            onError = {
                sendEffect(
                    MapsEffect.ShowToast(
                        ToastDetails(
                            title = Res.string.no_internet_connection,
                            message = Res.string.please_connect_to_the_internet_to_continue,
                            icon = Res.drawable.ic_close_circle
                        )
                    )
                )
                updateState {
                    it.copy(isSuccessToast = false)
                }
            }
        )
    }

    override fun onMapClicked(lat: Double, lng: Double) {
        val current = screenState.value.selectedLocation
        if (current != null && current.latitude == lat && current.longitude == lng) {
            updateState { it.copy(isBottomSheetVisible = true) }
            return
        }

        tryToCall(
            block = { locationRepository.getLocation(lat, lng) },
            onSuccess = { loc ->
                updateState {
                    it.copy(
                        selectedLocation = MapsUiState.LocationUi(loc.latitude, loc.longitude),
                        placeName = loc.country,
                        addressLine = loc.state,
                        isBottomSheetVisible = true,
                    )
                }
                sendEffect(MapsEffect.MoveCamera(lat, lng))
            },
            onError = {
                sendEffect(
                    MapsEffect.ShowToast(
                        ToastDetails(
                            title = Res.string.no_internet_connection,
                            message = Res.string.please_connect_to_the_internet_to_continue,
                            icon = Res.drawable.ic_close_circle
                        )
                    )
                )
                updateState {
                    it.copy(isSuccessToast = false)
                }
            }
        )
    }

    override fun onConfirmLocation() {
        val loc = screenState.value.selectedLocation ?: return
        tryToCall(
            block = {
                settingsRepository.saveLocation(
                    Location(
                        latitude = loc.latitude,
                        longitude = loc.longitude,
                        country = screenState.value.placeName,
                        state = screenState.value.addressLine
                    )
                )
            },
            onSuccess = {
                updateState { it.copy(isBottomSheetVisible = false, isSuccessToast = true) }
                sendEffect(
                    MapsEffect.ShowToast(
                        ToastDetails(
                            title = Res.string.location_saved,
                            message = Res.string.your_location_has_been_saved_successfully,
                            icon = Res.drawable.ic_check_circle
                        )
                    )
                )
            },
            onError = {}
        )
    }

    override fun onDismissBottomSheet() {
        updateState { it.copy(isBottomSheetVisible = false) }
    }
}

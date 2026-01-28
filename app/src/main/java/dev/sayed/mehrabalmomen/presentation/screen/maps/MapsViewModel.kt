package dev.sayed.mehrabalmomen.presentation.screen.maps

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.repository.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class MapsViewModel(
    private val settingsRepository: SettingsRepository,
    private val locationRepository: LocationRepository,
) : BaseViewModel<MapsUiState, MapsEffect>(MapsUiState()), MapsInteractionListener {
    init {
        loadUserCurrentLocation()
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
            onSuccess = { latLng ->
                onDetectLocationSuccess(latLng.latitude, latLng.longitude)
            },
            onError = {
                sendEffect(
                    MapsEffect.ShowToast(
                        ToastDetails(
                            title = R.string.no_internet_connection,
                            message = R.string.please_connect_to_the_internet_to_continue,
                            icon = R.drawable.ic_close_circle
                        )
                    )
                )
                updateState {
                    it.copy(
                        isSuccessToast = false
                    )
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
                            title = R.string.no_internet_connection,
                            message = R.string.please_connect_to_the_internet_to_continue,
                            icon = R.drawable.ic_close_circle
                        )
                    )
                )
                updateState {
                    it.copy(
                        isSuccessToast = false
                    )
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
                            title = R.string.no_internet_connection,
                            message = R.string.please_connect_to_the_internet_to_continue,
                            icon = R.drawable.ic_close_circle
                        )
                    )
                )
                updateState {
                    it.copy(
                        isSuccessToast = false
                    )
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
                            title = R.string.location_saved,
                            message = R.string.your_location_has_been_saved_successfully,
                            icon = R.drawable.ic_check_circle
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
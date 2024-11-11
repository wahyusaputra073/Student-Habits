package com.wahyusembiring.lecture.screen.addlecture

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.OfficeHour
import com.wahyusembiring.data.model.entity.Lecturer
import com.wahyusembiring.data.repository.LecturerRepository
import com.wahyusembiring.lecture.R
import com.wahyusembiring.ui.util.UIText
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(
    assistedFactory = AddLecturerScreenViewModel.Factory::class,
)
class AddLecturerScreenViewModel @AssistedInject constructor(
    private val lecturerRepository: LecturerRepository,
    private val application: Application,
    @Assisted private val lecturerId: String = "-1",
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(lecturerId: String = "-1"): AddLecturerScreenViewModel
    }

    private val _state = MutableStateFlow(AddLecturerScreenUIState())
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<AddLecturerScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        _state.update {
            it.copy(isEditMode = lecturerId != "-1")
        }
        if (lecturerId != "-1") {
            viewModelScope.launch {
                lecturerRepository.getLecturerById(lecturerId).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _state.update {
                                it.copy(popUps = it.popUps + AddLecturerScreenPopUp.Loading)
                            }
                        }
                        is Result.Error -> {
                            _state.update {
                                it.copy(
                                    popUps = it.popUps
                                        .minus(AddLecturerScreenPopUp.Loading)
                                        .plus(AddLecturerScreenPopUp.Error(UIText.DynamicString(result.throwable.message ?: "Unknown Error")))
                                )
                            }
                        }
                        is Result.Success -> {
                            _state.update {
                                it.copy(popUps = it.popUps.minus(AddLecturerScreenPopUp.Loading))
                            }
                            result.data.collect { lecturer ->
                                _state.update {
                                    it.copy(
                                        profilePictureUri = lecturer?.photo,
                                        name = lecturer?.name ?: it.name,
                                        phoneNumbers = lecturer?.phone ?: it.phoneNumbers,
                                        emails = lecturer?.email ?: it.emails,
                                        addresses = lecturer?.address ?: it.addresses,
                                        officeHours = lecturer?.officeHour ?: it.officeHours,
                                        websites = lecturer?.website ?: it.websites
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun onUIEvent(event: AddLecturerScreenUIEvent) {
        when (event) {
            is AddLecturerScreenUIEvent.OnBackButtonClick -> onBackButtonClick()
            is AddLecturerScreenUIEvent.OnSaveButtonClick -> onSaveButtonClick()
            is AddLecturerScreenUIEvent.OnLecturerNameChange -> onLectureNameChange(event.name)
            is AddLecturerScreenUIEvent.OnSaveConfirmationDialogConfirm -> onSaveConfirmationDialogConfirm()
            is AddLecturerScreenUIEvent.OnProfilePictureSelected -> onProfilePictureSelected(event.uri)
            is AddLecturerScreenUIEvent.OnNewPhoneNumber -> onNewPhoneNumber(event.phoneNumber)
            is AddLecturerScreenUIEvent.OnDeletePhoneNumber -> onDeletePhoneNumber(event.phoneNumber)
            is AddLecturerScreenUIEvent.OnDeleteAddress -> onDeleteAddress(event.address)
            is AddLecturerScreenUIEvent.OnDeleteWebsite -> onDeleteWebsite(event.website)
            is AddLecturerScreenUIEvent.OnDeleteOfficeHour -> onDeleteOfficeHour(event.officeHour)
            is AddLecturerScreenUIEvent.OnDeleteEmail-> onDeleteEmail(event.email)// Penanganan hapus nomor telepon
            is AddLecturerScreenUIEvent.OnNewEmail -> onNewEmail(event.email)
            is AddLecturerScreenUIEvent.OnNewAddress -> onNewAddress(event.address)
            is AddLecturerScreenUIEvent.OnNewOfficeHour -> onNewOfficeHour(event.officeHour)
            is AddLecturerScreenUIEvent.OnNewWebsite -> onNewWebsite(event.website)
            is AddLecturerScreenUIEvent.OnDismissPopUp -> onDismissPopUp(event.popUp)
            AddLecturerScreenUIEvent.OnLectureSavedOkButtonClick -> onLectureSavedOkButtonClick()
        }
    }

    private fun onLectureSavedOkButtonClick() {
        _navigationEvent.trySend(AddLecturerScreenNavigationEvent.NavigateBack)
    }

    private fun onDismissPopUp(popUp: AddLecturerScreenPopUp) {
        _state.update {
            it.copy(popUps = it.popUps.minus(popUp))
        }
    }

    private fun onNewWebsite(website: String) {
        _state.update {
            it.copy(
                websites = it.websites + website
            )
        }
    }

    private fun onNewOfficeHour(officeHour: OfficeHour) {
        _state.update {
            it.copy(officeHours = it.officeHours + officeHour)
        }
    }

    private fun onDeleteOfficeHour(officeHour: OfficeHour) {
        _state.update {
            it.copy(officeHours = it.officeHours - officeHour)
        }
    }


    private fun onNewAddress(address: String) {
        _state.update {
            it.copy(
                addresses = it.addresses + address
            )
        }
    }

    private fun onNewEmail(email: String) {
        _state.update {
            it.copy(
                emails = it.emails + email
            )
        }
    }

    private fun onNewPhoneNumber(phoneNumber: String) {
        _state.update {
            it.copy(
                phoneNumbers = it.phoneNumbers + phoneNumber
            )
        }
    }

    private fun onDeletePhoneNumber(phoneNumber: String) {
        _state.update {
            it.copy(phoneNumbers = it.phoneNumbers - phoneNumber)
        }
    }

    private fun onDeleteAddress(address: String) {
        _state.update {
            it.copy(addresses = it.addresses - address)
        }
    }

    private fun onDeleteEmail(email: String) {
        _state.update {
            it.copy(emails = it.emails - email)
        }
    }

    private fun onDeleteWebsite(website: String) {
        _state.update {
            it.copy(websites = it.websites - website)
        }
    }

    

    private fun onProfilePictureSelected(uri: Uri?) {
        _state.update {
            it.copy(
                profilePictureUri = uri ?: it.profilePictureUri
            )
        }
    }

    private fun onSaveConfirmationDialogConfirm() {
        try {
            val lecturer = Lecturer(
                photo = _state.value.profilePictureUri.also {
                    if (it != null) {
                        application.applicationContext.contentResolver
                            .takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                },
                name = _state.value.name.ifBlank {
                    throw ValidationException(
                        UIText.StringResource(R.string.lecture_name_cannot_be_empty)
                    )
                },
                phone = _state.value.phoneNumbers,
                email = _state.value.emails,
                address = _state.value.addresses,
                officeHour = _state.value.officeHours,
                website = _state.value.websites,
            )
            viewModelScope.launch {
                if (_state.value.isEditMode) {
                    updateLecturer(lecturer.copy(id = lecturerId))
                } else {
                    saveLecturer(lecturer)
                }
            }
        } catch (validationException: ValidationException) {
            _state.update {
                it.copy(popUps = it.popUps + AddLecturerScreenPopUp.Error(validationException.displayMessage))
            }
        } catch (throwable: Throwable) {
            val errorMessage = UIText.DynamicString(throwable.message ?: "Unknown Error")
            _state.update {
                it.copy(popUps = it.popUps + AddLecturerScreenPopUp.Error(errorMessage))
            }
        }
    }

    private suspend fun saveLecturer(lecturer: Lecturer) {
        lecturerRepository.insertLecturer(lecturer).collect { result ->
            when (result) {
                is Result.Loading -> {
                    _state.update {
                        it.copy(popUps = it.popUps + AddLecturerScreenPopUp.Loading)
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            popUps = it.popUps
                                .minus(AddLecturerScreenPopUp.Loading)
                                .plus(
                                    AddLecturerScreenPopUp.Error(
                                        UIText.DynamicString(
                                            result.throwable.message ?: "Unknown Error"
                                        )
                                    )
                                )
                        )
                    }
                }
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            popUps = it.popUps
                                .minus(AddLecturerScreenPopUp.Loading)
                                .plus(AddLecturerScreenPopUp.LecturerSaved)
                        )
                    }
                }
            }
        }
    }

    private suspend fun updateLecturer(lecturer: Lecturer) {
        lecturerRepository.updateLecturer(lecturer).collect { result ->
            when (result) {
                is Result.Loading -> {
                    _state.update {
                        it.copy(popUps = it.popUps + AddLecturerScreenPopUp.Loading)
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            popUps = it.popUps
                                .minus(AddLecturerScreenPopUp.Loading)
                                .plus(
                                    AddLecturerScreenPopUp.Error(
                                        UIText.DynamicString(
                                            result.throwable.message ?: "Unknown Error"
                                        )
                                    )
                                )
                        )
                    }
                }
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            popUps = it.popUps
                                .minus(AddLecturerScreenPopUp.Loading)
                                .plus(AddLecturerScreenPopUp.LecturerSaved)
                        )
                    }
                }
            }
        }
    }

    private fun onLectureNameChange(name: String) {
        _state.update {
            it.copy(name = name)
        }
    }

    private fun onSaveButtonClick() {
        _state.update {
            it.copy(popUps = it.popUps + AddLecturerScreenPopUp.SaveLecturerConfirmationDialog)
        }
    }

    private fun onBackButtonClick() {
        _navigationEvent.trySend(AddLecturerScreenNavigationEvent.NavigateBack)
    }
}
package com.wahyusembiring.lecture.screen.addlecture

import android.net.Uri
import com.wahyusembiring.data.model.OfficeHour
import com.wahyusembiring.ui.util.UIText

data class AddLecturerScreenUIState(
    val isEditMode: Boolean = false,
    val name: String = "",
    val profilePictureUri: Uri? = null,
    val phoneNumbers: List<String> = emptyList(),
    val emails: List<String> = emptyList(),
    val addresses: List<String> = emptyList(),
    val officeHours: List<OfficeHour> = emptyList(),
    val websites: List<String> = emptyList(),
    val popUps: List<AddLecturerScreenPopUp> = emptyList(),
)

sealed class AddLecturerScreenUIEvent {
    data object OnBackButtonClick : AddLecturerScreenUIEvent()
    data class OnLecturerNameChange(val name: String) : AddLecturerScreenUIEvent()
    data object OnSaveButtonClick : AddLecturerScreenUIEvent()
    data class OnProfilePictureSelected(val uri: Uri?) : AddLecturerScreenUIEvent()
    data class OnNewPhoneNumber(val phoneNumber: String) : AddLecturerScreenUIEvent()
    data class OnDeletePhoneNumber(val phoneNumber: String) : AddLecturerScreenUIEvent()
    data class OnDeleteEmail(val email: String) : AddLecturerScreenUIEvent()
    data class OnDeleteAddress(val address: String) : AddLecturerScreenUIEvent()
    data class OnDeleteWebsite(val website: String) : AddLecturerScreenUIEvent()// Tambahan untuk hapus nomor telepon
    data class OnNewEmail(val email: String) : AddLecturerScreenUIEvent()
    data class OnDeleteOfficeHour(val officeHour: OfficeHour) : AddLecturerScreenUIEvent()
    data class OnNewAddress(val address: String) : AddLecturerScreenUIEvent()
    data class OnNewOfficeHour(val officeHour: OfficeHour) : AddLecturerScreenUIEvent()
    data class OnNewWebsite(val website: String) : AddLecturerScreenUIEvent()
    data object OnSaveConfirmationDialogConfirm : AddLecturerScreenUIEvent()
    data class OnDismissPopUp(val popUp: AddLecturerScreenPopUp) : AddLecturerScreenUIEvent()
    data object OnLectureSavedOkButtonClick : AddLecturerScreenUIEvent()
}

sealed class AddLecturerScreenPopUp {
    data object Loading : AddLecturerScreenPopUp()
    data class Error(val message: UIText) : AddLecturerScreenPopUp()
    data object SaveLecturerConfirmationDialog : AddLecturerScreenPopUp()
    data object LecturerSaved : AddLecturerScreenPopUp()
}

sealed class AddLecturerScreenNavigationEvent {
    data object NavigateBack : AddLecturerScreenNavigationEvent()
}
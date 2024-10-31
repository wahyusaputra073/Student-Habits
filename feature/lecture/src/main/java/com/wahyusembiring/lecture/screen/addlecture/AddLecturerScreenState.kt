package com.wahyusembiring.lecture.screen.addlecture

import android.net.Uri
import androidx.navigation.NavController
import com.wahyusembiring.data.model.OfficeHour
import com.wahyusembiring.ui.util.UIText

data class AddLecturerScreenUItate(
    val isEditMode: Boolean = false,
    val showSaveConfirmationDialog: Boolean = false,
    val showLectureSavedDialog: Boolean = false,
    val errorMessage: UIText? = null,
    val name: String = "",
    val profilePictureUri: Uri? = null,
    val phoneNumbers: List<String> = emptyList(),
    val emails: List<String> = emptyList(),
    val addresses: List<String> = emptyList(),
    val officeHours: List<OfficeHour> = emptyList(),
    val websites: List<String> = emptyList()
)

sealed class AddLecturerScreenUIEvent {
    data class OnBackButtonClick(val navController: NavController) : AddLecturerScreenUIEvent()
    data class OnLecturerNameChange(val name: String) : AddLecturerScreenUIEvent()
    data object OnSaveButtonClick : AddLecturerScreenUIEvent()
    data object OnLecturerSavedDialogDismiss : AddLecturerScreenUIEvent()
    data class OnProfilePictureSelected(val uri: Uri?) : AddLecturerScreenUIEvent()
    data class OnNewPhoneNumber(val phoneNumber: String) : AddLecturerScreenUIEvent()
    data class OnDeletePhoneNumber(val phoneNumber: String) : AddLecturerScreenUIEvent() // Tambahan untuk hapus nomor telepon
    data class OnNewEmail(val email: String) : AddLecturerScreenUIEvent()
    data class OnNewAddress(val address: String) : AddLecturerScreenUIEvent()
    data class OnNewOfficeHour(val officeHour: OfficeHour) : AddLecturerScreenUIEvent()
    data class OnNewWebsite(val website: String) : AddLecturerScreenUIEvent()

    // Save Confirmation Dialog
    data object OnSaveConfirmationDialogDismiss : AddLecturerScreenUIEvent()
    data object OnSaveConfirmationDialogConfirm : AddLecturerScreenUIEvent()
    data object OnSaveConfirmationDialogCancel : AddLecturerScreenUIEvent()

    // Error Dialog
    data object OnErrorDialogDismiss : AddLecturerScreenUIEvent()
}
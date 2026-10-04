package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.UcsiRepository
import com.example.security.SecurityManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UcsiCampusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UcsiRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = UcsiRepository(db.ucsiDao())
    }

    val currentUser = repository.currentUserFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val courses = repository.allCoursesFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val bookings = repository.allBookingsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val payments = repository.allPaymentsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val documents = repository.allDocumentRequestsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val activeEmergencyAlerts = repository.activeEmergencyAlertsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val carpools = repository.allCarpoolRoutesFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val transferApplications = repository.transferApplicationsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val internships = repository.allInternshipsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val auditLogs = repository.auditLogsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // UI States
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _sessionToken = MutableStateFlow(SecurityManager.generateSessionToken("10023419"))
    val sessionToken: StateFlow<String> = _sessionToken.asStateFlow()

    // Timezone sync: BST (Dhaka UTC+6) vs MYT (Malaysia UTC+8)
    private val _selectedTimezone = MutableStateFlow("BST") // "BST" or "MYT"
    val selectedTimezone: StateFlow<String> = _selectedTimezone.asStateFlow()

    // Dynamic QR Payload for ID
    private val _dynamicQrPayload = MutableStateFlow("UCSI-ID://10023419/INITIAL")
    val dynamicQrPayload: StateFlow<String> = _dynamicQrPayload.asStateFlow()

    private val _qrSecondsRemaining = MutableStateFlow(30)
    val qrSecondsRemaining: StateFlow<Int> = _qrSecondsRemaining.asStateFlow()

    // Feedback messages (Snackbar/Toast)
    private val _uiNotice = MutableStateFlow<String?>(null)
    val uiNotice: StateFlow<String?> = _uiNotice.asStateFlow()

    // Search state
    private val _globalSearchQuery = MutableStateFlow("")
    val globalSearchQuery: StateFlow<String> = _globalSearchQuery.asStateFlow()

    init {
        // Continuous Dynamic QR ticker for smart campus ID
        viewModelScope.launch {
            while (true) {
                val studentId = currentUser.value?.studentOrStaffId ?: "10023419"
                val (payload, seconds) = SecurityManager.generateDynamicIdPayload(studentId)
                _dynamicQrPayload.value = payload
                _qrSecondsRemaining.value = seconds
                delay(1000)
            }
        }
    }

    fun setTimezone(tz: String) {
        _selectedTimezone.value = tz
    }

    fun setGlobalSearchQuery(q: String) {
        _globalSearchQuery.value = q
    }

    fun clearUiNotice() {
        _uiNotice.value = null
    }

    fun login(idOrEmail: String, pass: String): Boolean {
        if (!SecurityManager.checkRateLimit()) {
            _uiNotice.value = "Too many login attempts. Please wait 1 minute."
            return false
        }
        if (idOrEmail.isBlank() || pass.isBlank()) {
            _uiNotice.value = "Please enter both credentials."
            return false
        }
        SecurityManager.resetRateLimit()
        _isLoggedIn.value = true
        _sessionToken.value = SecurityManager.generateSessionToken(idOrEmail)
        _uiNotice.value = "Authenticated successfully with secure session."
        return true
    }

    fun logout() {
        _isLoggedIn.value = false
        _uiNotice.value = "Securely logged out from session."
    }

    fun switchUserRole(role: String) {
        viewModelScope.launch {
            repository.switchUserRole(role)
            _uiNotice.value = "Switched to $role role successfully."
        }
    }

    fun bookFacility(
        name: String,
        type: String,
        floor: Int,
        date: String,
        timeSlot: String
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val res = repository.bookFacility(
                facilityName = name,
                facilityType = type,
                floor = floor,
                date = date,
                timeSlot = timeSlot,
                studentId = user?.studentOrStaffId ?: "10023419",
                studentName = user?.fullName ?: "Student"
            )
            res.onSuccess {
                _uiNotice.value = "Booking confirmed for $name ($timeSlot)!"
            }.onFailure { err ->
                _uiNotice.value = err.message ?: "Booking failed."
            }
        }
    }

    fun cancelBooking(bookingId: Long) {
        viewModelScope.launch {
            repository.cancelBooking(bookingId, currentUser.value?.fullName ?: "Student")
            _uiNotice.value = "Booking #$bookingId cancelled."
        }
    }

    fun processPayment(title: String, amountBdt: Double, gateway: String) {
        viewModelScope.launch {
            val payment = repository.processPayment(title, amountBdt, gateway)
            _uiNotice.value = "Payment of ৳${String.format("%,.0f", amountBdt)} completed via $gateway. Receipt #${payment.receiptNumber}"
        }
    }

    fun submitDocumentRequest(docType: String, purpose: String) {
        viewModelScope.launch {
            val req = repository.submitDocumentRequest(docType, purpose)
            _uiNotice.value = "Document request submitted. Tracking Code: ${req.trackingCode}"
        }
    }

    fun recordAttendance(courseCode: String, method: String) {
        viewModelScope.launch {
            repository.recordAttendance(courseCode, method)
            _uiNotice.value = "Attendance verified for $courseCode via $method!"
        }
    }

    fun broadcastAlert(title: String, message: String, severity: String) {
        viewModelScope.launch {
            repository.publishEmergencyAlert(title, message, severity)
            _uiNotice.value = "Emergency alert broadcasted across UCSI network."
        }
    }

    fun dismissAlert(id: Long) {
        viewModelScope.launch {
            repository.dismissAlert(id)
            _uiNotice.value = "Alert dismissed."
        }
    }

    fun applyInternship(id: Long, company: String) {
        viewModelScope.launch {
            repository.applyInternship(id)
            _uiNotice.value = "Application submitted to $company!"
        }
    }

    fun publishCarpool(origin: String, departureTime: String, seats: Int, vehicle: String, notes: String) {
        viewModelScope.launch {
            repository.addCarpoolListing(origin, departureTime, seats, vehicle, notes)
            _uiNotice.value = "Carpool route published to verified student network."
        }
    }
}

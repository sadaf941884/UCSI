package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long = 1L,
    val studentOrStaffId: String,
    val fullName: String,
    val email: String,
    val role: String, // "STUDENT", "FACULTY", "ADMIN"
    val program: String,
    val campus: String = "UCSI Bangladesh Branch Campus, Banani, Dhaka",
    val cgpa: Double = 3.84,
    val completedCredits: Int = 84,
    val totalCredits: Int = 120,
    val balanceBdt: Double = 35000.0,
    val balanceMyr: Double = 1272.72,
    val academicStanding: String = "First Class Honours (Dean's List)",
    val mfaEnabled: Boolean = true,
    val biometricsEnabled: Boolean = true
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseCode: String,
    val courseTitle: String,
    val facultyName: String,
    val room: String,
    val floor: Int,
    val dayOfWeek: String,
    val startTimeBst: String,
    val endTimeBst: String,
    val startTimeMyt: String,
    val endTimeMyt: String,
    val credits: Int,
    val attendancePercent: Int,
    val attendedClasses: Int,
    val totalClasses: Int,
    val isOnlineHybrid: Boolean = false,
    val hostCampus: String = "Dhaka Campus"
)

@Entity(tableName = "facility_bookings")
data class FacilityBookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facilityName: String,
    val facilityType: String, // "POD", "LAB", "ROOM"
    val floor: Int,
    val date: String,
    val timeSlot: String,
    val bookedByStudentId: String,
    val bookedByName: String,
    val status: String = "CONFIRMED" // "CONFIRMED", "CANCELLED"
)

@Entity(tableName = "payment_transactions")
data class PaymentTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: String,
    val feeTitle: String,
    val amountBdt: Double,
    val amountMyr: Double,
    val paymentMethod: String, // "bKash", "Nagad", "Rocket", "Visa / Master"
    val date: String,
    val status: String = "PAID", // "PAID", "PENDING"
    val receiptNumber: String
)

@Entity(tableName = "document_requests")
data class DocumentRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val documentType: String,
    val purpose: String,
    val requestDate: String,
    val status: String, // "REQUESTED", "PROCESSING", "READY", "COLLECTED"
    val trackingCode: String,
    val feePaid: Boolean = true
)

@Entity(tableName = "emergency_alerts")
data class EmergencyAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val severity: String, // "CRITICAL", "HIGH", "INFO"
    val timestamp: String,
    val author: String,
    val isActive: Boolean = true
)

@Entity(tableName = "carpool_routes")
data class CarpoolRouteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val driverName: String,
    val driverStudentId: String,
    val origin: String,
    val destination: String = "UCSI Banani Campus",
    val departureTime: String,
    val availableSeats: Int,
    val vehicleInfo: String,
    val routeNotes: String
)

@Entity(tableName = "transfer_applications")
data class TransferApplicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val programName: String,
    val targetCampus: String,
    val status: String, // "SUBMITTED", "UNDER_REVIEW", "DOCUMENTS_REQUIRED", "APPROVED", "COMPLETED"
    val currentStageIndex: Int, // 0 to 5
    val submittedDate: String,
    val targetIntake: String
)

@Entity(tableName = "internships")
data class InternshipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val companyName: String,
    val roleTitle: String,
    val category: String, // "Technology", "MNC", "Banking", "FinTech"
    val stipend: String,
    val location: String,
    val deadline: String,
    val applicationStatus: String = "NOT_APPLIED" // "NOT_APPLIED", "APPLIED", "INTERVIEW_SCHEDULED", "OFFERED"
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: String,
    val actor: String,
    val action: String,
    val details: String,
    val status: String = "SUCCESS"
)

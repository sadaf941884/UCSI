package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UcsiDao {

    // User Operations
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserFlow(userId: Long = 1L): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUser(userId: Long = 1L): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    // Courses & Schedule
    @Query("SELECT * FROM courses ORDER BY startTimeBst ASC")
    fun getAllCoursesFlow(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Query("UPDATE courses SET attendedClasses = attendedClasses + 1, attendancePercent = ((attendedClasses + 1) * 100) / totalClasses WHERE courseCode = :code")
    suspend fun markAttendance(code: String)

    // Facility Bookings
    @Query("SELECT * FROM facility_bookings ORDER BY id DESC")
    fun getAllBookingsFlow(): Flow<List<FacilityBookingEntity>>

    @Query("SELECT * FROM facility_bookings WHERE facilityName = :facilityName AND date = :date AND timeSlot = :timeSlot AND status = 'CONFIRMED' LIMIT 1")
    suspend fun findExistingBooking(facilityName: String, date: String, timeSlot: String): FacilityBookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: FacilityBookingEntity)

    @Query("UPDATE facility_bookings SET status = 'CANCELLED' WHERE id = :bookingId")
    suspend fun cancelBooking(bookingId: Long)

    // Payments
    @Query("SELECT * FROM payment_transactions ORDER BY id DESC")
    fun getAllPaymentsFlow(): Flow<List<PaymentTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentTransactionEntity)

    // Documents
    @Query("SELECT * FROM document_requests ORDER BY id DESC")
    fun getAllDocumentRequestsFlow(): Flow<List<DocumentRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocumentRequest(request: DocumentRequestEntity)

    // Emergency Alerts
    @Query("SELECT * FROM emergency_alerts WHERE isActive = 1 ORDER BY id DESC")
    fun getActiveEmergencyAlertsFlow(): Flow<List<EmergencyAlertEntity>>

    @Query("SELECT * FROM emergency_alerts ORDER BY id DESC")
    fun getAllEmergencyAlertsFlow(): Flow<List<EmergencyAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: EmergencyAlertEntity)

    @Query("UPDATE emergency_alerts SET isActive = 0 WHERE id = :alertId")
    suspend fun dismissAlert(alertId: Long)

    // Carpool Routes
    @Query("SELECT * FROM carpool_routes ORDER BY id DESC")
    fun getAllCarpoolRoutesFlow(): Flow<List<CarpoolRouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarpoolRoute(route: CarpoolRouteEntity)

    // Transfer Applications
    @Query("SELECT * FROM transfer_applications ORDER BY id DESC")
    fun getTransferApplicationsFlow(): Flow<List<TransferApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransferApplication(app: TransferApplicationEntity)

    // Internships / Co-Op
    @Query("SELECT * FROM internships ORDER BY id ASC")
    fun getAllInternshipsFlow(): Flow<List<InternshipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInternships(internships: List<InternshipEntity>)

    @Query("UPDATE internships SET applicationStatus = :status WHERE id = :id")
    suspend fun updateInternshipStatus(id: Long, status: String)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY id DESC LIMIT 50")
    fun getAllAuditLogsFlow(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)
}

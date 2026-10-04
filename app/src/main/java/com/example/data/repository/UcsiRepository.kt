package com.example.data.repository

import com.example.data.local.UcsiDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UcsiRepository(private val dao: UcsiDao) {

    val currentUserFlow: Flow<UserEntity?> = dao.getUserFlow()
    val allCoursesFlow: Flow<List<CourseEntity>> = dao.getAllCoursesFlow()
    val allBookingsFlow: Flow<List<FacilityBookingEntity>> = dao.getAllBookingsFlow()
    val allPaymentsFlow: Flow<List<PaymentTransactionEntity>> = dao.getAllPaymentsFlow()
    val allDocumentRequestsFlow: Flow<List<DocumentRequestEntity>> = dao.getAllDocumentRequestsFlow()
    val activeEmergencyAlertsFlow: Flow<List<EmergencyAlertEntity>> = dao.getActiveEmergencyAlertsFlow()
    val allEmergencyAlertsFlow: Flow<List<EmergencyAlertEntity>> = dao.getAllEmergencyAlertsFlow()
    val allCarpoolRoutesFlow: Flow<List<CarpoolRouteEntity>> = dao.getAllCarpoolRoutesFlow()
    val transferApplicationsFlow: Flow<List<TransferApplicationEntity>> = dao.getTransferApplicationsFlow()
    val allInternshipsFlow: Flow<List<InternshipEntity>> = dao.getAllInternshipsFlow()
    val auditLogsFlow: Flow<List<AuditLogEntity>> = dao.getAllAuditLogsFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val existingUser = dao.getUser(1L)
        if (existingUser == null) {
            // Seed Student Profile
            dao.insertUser(
                UserEntity(
                    id = 1L,
                    studentOrStaffId = "10023419",
                    fullName = "Sarah Rahman",
                    email = "sarah.rahman@ucsiuniversity.edu.my",
                    role = "STUDENT",
                    program = "B.Sc. (Hons) Computing",
                    campus = "UCSI Bangladesh Branch Campus, Banani, Dhaka",
                    cgpa = 3.84,
                    completedCredits = 84,
                    totalCredits = 120,
                    balanceBdt = 35000.0,
                    balanceMyr = 1272.72,
                    academicStanding = "First Class Honours (Dean's List)",
                    mfaEnabled = true,
                    biometricsEnabled = true
                )
            )

            // Seed Courses (Dhaka BST & KL MYT timezones)
            dao.insertCourses(
                listOf(
                    CourseEntity(
                        courseCode = "CS301",
                        courseTitle = "Distributed Systems & Cloud Computing",
                        facultyName = "Assoc. Prof. Dr. Arif Chowdhury",
                        room = "Room 703",
                        floor = 7,
                        dayOfWeek = "Sunday & Tuesday",
                        startTimeBst = "09:00 AM",
                        endTimeBst = "11:00 AM",
                        startTimeMyt = "11:00 AM",
                        endTimeMyt = "01:00 PM",
                        credits = 4,
                        attendancePercent = 92,
                        attendedClasses = 22,
                        totalClasses = 24,
                        isOnlineHybrid = false,
                        hostCampus = "Dhaka Campus (Level 7)"
                    ),
                    CourseEntity(
                        courseCode = "SE204",
                        courseTitle = "Advanced Software Engineering & DevOps",
                        facultyName = "Prof. Kenneth Tan (KL) & Engr. Tanvir Ahmed (Dhaka)",
                        room = "Lab 802",
                        floor = 8,
                        dayOfWeek = "Monday & Wednesday",
                        startTimeBst = "11:30 AM",
                        endTimeBst = "01:30 PM",
                        startTimeMyt = "01:30 PM",
                        endTimeMyt = "03:30 PM",
                        credits = 4,
                        attendancePercent = 88,
                        attendedClasses = 21,
                        totalClasses = 24,
                        isOnlineHybrid = true,
                        hostCampus = "Hybrid Co-Delivery (KL Main / Dhaka)"
                    ),
                    CourseEntity(
                        courseCode = "AI402",
                        courseTitle = "Applied Machine Learning & Neural Nets",
                        facultyName = "Dr. Farhana Yasmin",
                        room = "Room 905",
                        floor = 9,
                        dayOfWeek = "Tuesday & Thursday",
                        startTimeBst = "02:00 PM",
                        endTimeBst = "04:00 PM",
                        startTimeMyt = "04:00 PM",
                        endTimeMyt = "06:00 PM",
                        credits = 3,
                        attendancePercent = 95,
                        attendedClasses = 19,
                        totalClasses = 20,
                        isOnlineHybrid = false,
                        hostCampus = "Dhaka Campus (Level 9)"
                    ),
                    CourseEntity(
                        courseCode = "CY308",
                        courseTitle = "Cybersecurity & Cryptographic Protocols",
                        facultyName = "Dr. Rezaul Karim",
                        room = "Lecture Hall 401",
                        floor = 4,
                        dayOfWeek = "Sunday & Wednesday",
                        startTimeBst = "04:30 PM",
                        endTimeBst = "06:00 PM",
                        startTimeMyt = "06:30 PM",
                        endTimeMyt = "08:00 PM",
                        credits = 3,
                        attendancePercent = 85,
                        attendedClasses = 17,
                        totalClasses = 20,
                        isOnlineHybrid = false,
                        hostCampus = "Dhaka Campus (Level 4)"
                    )
                )
            )

            // Seed Facility Bookings
            dao.insertBooking(
                FacilityBookingEntity(
                    facilityName = "Study Pod 7A",
                    facilityType = "POD",
                    floor = 7,
                    date = getTodayDate(),
                    timeSlot = "02:00 PM - 04:00 PM",
                    bookedByStudentId = "10023419",
                    bookedByName = "Sarah Rahman",
                    status = "CONFIRMED"
                )
            )

            // Seed Initial Payment Transactions
            dao.insertPayment(
                PaymentTransactionEntity(
                    transactionId = "TXN-BK-9842104",
                    feeTitle = "Summer Trimester Tuition Installment 2",
                    amountBdt = 65000.0,
                    amountMyr = 2363.64,
                    paymentMethod = "bKash Merchant",
                    date = "2026-09-15 14:22",
                    status = "PAID",
                    receiptNumber = "REC-UCSI-2026-8819"
                )
            )
            dao.insertPayment(
                PaymentTransactionEntity(
                    transactionId = "TXN-NG-4412998",
                    feeTitle = "Computing Lab & Cloud Infrastructure Fee",
                    amountBdt = 12000.0,
                    amountMyr = 436.36,
                    paymentMethod = "Nagad",
                    date = "2026-08-01 10:15",
                    status = "PAID",
                    receiptNumber = "REC-UCSI-2026-5120"
                )
            )

            // Seed Document Requests
            dao.insertDocumentRequest(
                DocumentRequestEntity(
                    documentType = "Official Academic Transcript",
                    purpose = "UCSI Malaysia 2+1 Credit Transfer Application",
                    requestDate = "2026-09-20",
                    status = "READY",
                    trackingCode = "DOC-UCSI-74910",
                    feePaid = true
                )
            )
            dao.insertDocumentRequest(
                DocumentRequestEntity(
                    documentType = "Provisional No Objection Certificate (NOC)",
                    purpose = "High Commission Visa Processing",
                    requestDate = "2026-09-28",
                    status = "PROCESSING",
                    trackingCode = "DOC-UCSI-82319",
                    feePaid = true
                )
            )

            // Seed Emergency Alert
            dao.insertAlert(
                EmergencyAlertEntity(
                    title = "Monsoon Traffic Alert: Kemal Ataturk Avenue",
                    message = "Heavy rain in Banani area. Waterlogging reported near Road 11 & Kemal Ataturk intersection. University Shuttle 2 delayed by 20 mins. Hybrid attendance mode enabled for today.",
                    severity = "WARNING",
                    timestamp = "10 mins ago",
                    author = "Campus Safety & Security Office",
                    isActive = true
                )
            )

            // Seed Carpool Routes
            dao.insertCarpoolRoute(
                CarpoolRouteEntity(
                    driverName = "Tanvir Hasan (SE Final Year)",
                    driverStudentId = "10021098",
                    origin = "Uttara Sector 11 (Kachabazar)",
                    destination = "UCSI Banani Campus Tower",
                    departureTime = "07:50 AM",
                    availableSeats = 2,
                    vehicleInfo = "Toyota Aqua (White, Metro-Ga 24-9182)",
                    routeNotes = "Via Airport Road, stops at Radisson and Kakoli. Non-smoking, verified UCSI students only."
                )
            )
            dao.insertCarpoolRoute(
                CarpoolRouteEntity(
                    driverName = "Mahir Faysal (BBA Level 3)",
                    driverStudentId = "10022415",
                    origin = "Dhanmondi 27 (Meena Bazar)",
                    destination = "UCSI Banani Campus Tower",
                    departureTime = "08:10 AM",
                    availableSeats = 3,
                    vehicleInfo = "Honda Grace (Silver)",
                    routeNotes = "Via Farmgate & Jahangir Gate flyover. Air-conditioned."
                )
            )

            // Seed Transfer Mobility Application
            dao.insertTransferApplication(
                TransferApplicationEntity(
                    programName = "2+1 Credit Transfer to UCSI Kuala Lumpur Main Campus",
                    targetCampus = "Kuala Lumpur (South Wing, Cheras)",
                    status = "UNDER_REVIEW",
                    currentStageIndex = 2, // 0: Draft, 1: Submitted, 2: Under Review, 3: Documents Required, 4: Approved, 5: Completed
                    submittedDate = "2026-09-12",
                    targetIntake = "January 2027 (Trimester 1)"
                )
            )

            // Seed Internships & Co-Op
            dao.insertInternships(
                listOf(
                    InternshipEntity(
                        companyName = "Brain Station 23",
                        roleTitle = "Associate Cloud & Backend Engineer Co-Op",
                        category = "Technology",
                        stipend = "25,000 BDT / month",
                        location = "Mohakhali DOHS, Dhaka (15 mins from Banani)",
                        deadline = "Oct 25, 2026",
                        applicationStatus = "APPLIED"
                    ),
                    InternshipEntity(
                        companyName = "British American Tobacco (BAT)",
                        roleTitle = "Digital Transformation & Business Analyst Trainee",
                        category = "MNC",
                        stipend = "35,000 BDT / month",
                        location = "Gulshan 1, Dhaka",
                        deadline = "Nov 02, 2026",
                        applicationStatus = "NOT_APPLIED"
                    ),
                    InternshipEntity(
                        companyName = "Standard Chartered Bank",
                        roleTitle = "FinTech Solutions & Cyber Resilience Intern",
                        category = "Banking",
                        stipend = "30,000 BDT / month",
                        location = "Gulshan Avenue, Dhaka",
                        deadline = "Nov 15, 2026",
                        applicationStatus = "NOT_APPLIED"
                    ),
                    InternshipEntity(
                        companyName = "Grameenphone Ltd",
                        roleTitle = "AI & Big Data Co-op Placement",
                        category = "Technology",
                        stipend = "28,000 BDT / month",
                        location = "G-House, Bashundhara R/A, Dhaka",
                        deadline = "Nov 20, 2026",
                        applicationStatus = "NOT_APPLIED"
                    )
                )
            )

            // Seed Audit Logs
            dao.insertAuditLog(
                AuditLogEntity(
                    timestamp = "2026-10-01 10:14:22",
                    actor = "System (Auth Engine)",
                    action = "STUDENT_LOGIN_SUCCESS",
                    details = "User Sarah Rahman (10023419) logged in via MFA token from Banani Campus IP 103.114.20.12",
                    status = "SUCCESS"
                )
            )
            dao.insertAuditLog(
                AuditLogEntity(
                    timestamp = "2026-10-01 14:02:11",
                    actor = "Dr. Kenneth Tan (Registrar)",
                    action = "CREDIT_AUDIT_VERIFIED",
                    details = "Cross-campus MQA/UGC compliance verified for 84 completed credits.",
                    status = "SUCCESS"
                )
            )
        }
    }

    suspend fun bookFacility(
        facilityName: String,
        facilityType: String,
        floor: Int,
        date: String,
        timeSlot: String,
        studentId: String,
        studentName: String
    ): Result<FacilityBookingEntity> {
        val existing = dao.findExistingBooking(facilityName, date, timeSlot)
        if (existing != null) {
            return Result.failure(Exception("Facility '$facilityName' is already booked for $date at $timeSlot."))
        }
        val booking = FacilityBookingEntity(
            facilityName = facilityName,
            facilityType = facilityType,
            floor = floor,
            date = date,
            timeSlot = timeSlot,
            bookedByStudentId = studentId,
            bookedByName = studentName,
            status = "CONFIRMED"
        )
        dao.insertBooking(booking)
        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = getCurrentTimestamp(),
                actor = studentName,
                action = "FACILITY_RESERVED",
                details = "Reserved $facilityName on Floor $floor for $timeSlot",
                status = "SUCCESS"
            )
        )
        return Result.success(booking)
    }

    suspend fun cancelBooking(bookingId: Long, actorName: String) {
        dao.cancelBooking(bookingId)
        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = getCurrentTimestamp(),
                actor = actorName,
                action = "FACILITY_BOOKING_CANCELLED",
                details = "Cancelled booking ID #$bookingId",
                status = "SUCCESS"
            )
        )
    }

    suspend fun processPayment(
        feeTitle: String,
        amountBdt: Double,
        gateway: String
    ): PaymentTransactionEntity {
        val amountMyr = amountBdt / 27.50
        val txnId = "TXN-${gateway.take(2).uppercase()}-${Random().nextInt(899999) + 100000}"
        val receiptNo = "REC-UCSI-${Random().nextInt(8999) + 1000}"

        val payment = PaymentTransactionEntity(
            transactionId = txnId,
            feeTitle = feeTitle,
            amountBdt = amountBdt,
            amountMyr = amountMyr,
            paymentMethod = gateway,
            date = getCurrentTimestamp(),
            status = "PAID",
            receiptNumber = receiptNo
        )
        dao.insertPayment(payment)

        val user = dao.getUser()
        if (user != null) {
            val newBalance = (user.balanceBdt - amountBdt).coerceAtLeast(0.0)
            dao.updateUser(user.copy(balanceBdt = newBalance, balanceMyr = newBalance / 27.50))
        }

        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = getCurrentTimestamp(),
                actor = user?.fullName ?: "Student",
                action = "PAYMENT_SETTLED",
                details = "Settled $amountBdt BDT via $gateway (Txn: $txnId)",
                status = "SUCCESS"
            )
        )
        return payment
    }

    suspend fun submitDocumentRequest(
        docType: String,
        purpose: String
    ): DocumentRequestEntity {
        val tracking = "DOC-UCSI-${Random().nextInt(89999) + 10000}"
        val req = DocumentRequestEntity(
            documentType = docType,
            purpose = purpose,
            requestDate = getTodayDate(),
            status = "REQUESTED",
            trackingCode = tracking,
            feePaid = true
        )
        dao.insertDocumentRequest(req)
        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = getCurrentTimestamp(),
                actor = "Student",
                action = "DOCUMENT_REQUEST_FILED",
                details = "Requested $docType ($tracking)",
                status = "SUCCESS"
            )
        )
        return req
    }

    suspend fun recordAttendance(courseCode: String, verifiedMethod: String) {
        dao.markAttendance(courseCode)
        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = getCurrentTimestamp(),
                actor = "Student",
                action = "ATTENDANCE_CHECK_IN",
                details = "Verified presence for $courseCode via $verifiedMethod",
                status = "SUCCESS"
            )
        )
    }

    suspend fun publishEmergencyAlert(title: String, message: String, severity: String) {
        val alert = EmergencyAlertEntity(
            title = title,
            message = message,
            severity = severity,
            timestamp = "Just now",
            author = "Administrator (Emergency Ops)",
            isActive = true
        )
        dao.insertAlert(alert)
        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = getCurrentTimestamp(),
                actor = "Administrator",
                action = "EMERGENCY_BROADCAST",
                details = "Broadcasted alert: $title [$severity]",
                status = "CRITICAL"
            )
        )
    }

    suspend fun dismissAlert(alertId: Long) {
        dao.dismissAlert(alertId)
    }

    suspend fun applyInternship(internshipId: Long) {
        dao.updateInternshipStatus(internshipId, "APPLIED")
        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = getCurrentTimestamp(),
                actor = "Student",
                action = "INTERNSHIP_APPLICATION",
                details = "Applied to internship ID #$internshipId",
                status = "SUCCESS"
            )
        )
    }

    suspend fun addCarpoolListing(origin: String, departureTime: String, seats: Int, vehicle: String, notes: String) {
        val route = CarpoolRouteEntity(
            driverName = "Sarah Rahman (Verified)",
            driverStudentId = "10023419",
            origin = origin,
            destination = "UCSI Banani Campus Tower",
            departureTime = departureTime,
            availableSeats = seats,
            vehicleInfo = vehicle,
            routeNotes = notes
        )
        dao.insertCarpoolRoute(route)
    }

    suspend fun switchUserRole(newRole: String) {
        val user = dao.getUser() ?: return
        val updated = when (newRole) {
            "STUDENT" -> user.copy(
                studentOrStaffId = "10023419",
                fullName = "Sarah Rahman",
                email = "sarah.rahman@ucsiuniversity.edu.my",
                role = "STUDENT",
                program = "B.Sc. (Hons) Computing",
                academicStanding = "First Class Honours (Dean's List)"
            )
            "FACULTY" -> user.copy(
                studentOrStaffId = "FAC-882",
                fullName = "Dr. Arif Chowdhury",
                email = "arif.chowdhury@ucsiuniversity.edu.my",
                role = "FACULTY",
                program = "Faculty of Computer Science & Engineering",
                academicStanding = "Department Chair"
            )
            "ADMIN" -> user.copy(
                studentOrStaffId = "REG-101",
                fullName = "Syed M. Ahmed",
                email = "s.ahmed@ucsiuniversity.edu.my",
                role = "ADMIN",
                program = "Office of the Campus Registrar & Operations",
                academicStanding = "Head of Campus Administration"
            )
            else -> user
        }
        dao.updateUser(updated)
        dao.insertAuditLog(
            AuditLogEntity(
                timestamp = getCurrentTimestamp(),
                actor = updated.fullName,
                action = "ROLE_SESSION_SWITCH",
                details = "Switched active context to $newRole",
                status = "SUCCESS"
            )
        )
    }

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}

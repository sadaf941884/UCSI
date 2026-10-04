package com.example.security

import java.security.MessageDigest
import java.util.UUID

object SecurityManager {

    private var loginAttempts = 0
    private var lastAttemptTimestamp = 0L

    fun hashPassword(password: String, salt: String = "ucsi_dhaka_salt_v2"): String {
        val input = "$salt:$password"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun generateSessionToken(userId: String): String {
        val raw = "$userId-${System.currentTimeMillis()}-${UUID.randomUUID()}"
        return "UCSI-SEC-" + raw.hashCode().toString(16).uppercase()
    }

    /**
     * Generates a 30-second time-based dynamic OTP hash for high-security student ID verification.
     * Prevents photo/screenshot reuse at physical campus turnstiles and library scanners.
     */
    fun generateDynamicIdPayload(studentId: String): Pair<String, Int> {
        val epochSeconds = System.currentTimeMillis() / 1000
        val window = epochSeconds / 30
        val secondsRemaining = (30 - (epochSeconds % 30)).toInt()
        val raw = "UCSI-BD-CAMPUS-$studentId-$window"
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(raw.toByteArray()).joinToString("").take(16).uppercase()
        return Pair("UCSI-ID://$studentId/$hash", secondsRemaining)
    }

    /**
     * Rate limiter for authentication attempts.
     */
    fun checkRateLimit(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastAttemptTimestamp > 60_000) {
            loginAttempts = 0
        }
        if (loginAttempts >= 5) {
            return false // Blocked due to rate limiting
        }
        loginAttempts++
        lastAttemptTimestamp = now
        return true
    }

    fun resetRateLimit() {
        loginAttempts = 0
    }

    fun validateStudentId(id: String): Boolean {
        // Accepts numeric 8-digit UCSI student ID or staff code e.g. FAC-882, REG-101
        return id.trim().matches(Regex("^[0-9]{8}$")) || id.trim().matches(Regex("^[A-Z]{3}-[0-9]{3}$"))
    }

    fun validateUniversityEmail(email: String): Boolean {
        val cleaned = email.trim().lowercase()
        return android.util.Patterns.EMAIL_ADDRESS.matcher(cleaned).matches() &&
                (cleaned.endsWith("@ucsiuniversity.edu.my") ||
                 cleaned.endsWith("@student.ucsiuniversity.edu.my") ||
                 cleaned.contains("ucsi"))
    }
}

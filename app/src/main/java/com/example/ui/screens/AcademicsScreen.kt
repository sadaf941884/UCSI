package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourseEntity
import com.example.data.model.TransferApplicationEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.*

@Composable
fun AcademicsScreen(
    user: UserEntity?,
    courses: List<CourseEntity>,
    transferApps: List<TransferApplicationEntity>,
    selectedTimezone: String,
    onTimezoneToggle: () -> Unit,
    onMarkAttendanceClick: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Degree Audit", "Campus Mobility (2+1)", "Timetable & Sync")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("academics_screen")
    ) {
        // Academics Sub-Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = UcsiCrimson
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> DegreeAuditTab(user = user)
            1 -> CampusMobilityTab(transferApps = transferApps)
            2 -> TimetableSyncTab(
                courses = courses,
                selectedTimezone = selectedTimezone,
                onTimezoneToggle = onTimezoneToggle,
                onMarkAttendanceClick = onMarkAttendanceClick
            )
        }
    }
}

@Composable
fun DegreeAuditTab(user: UserEntity?) {
    val completed = user?.completedCredits ?: 84
    val total = user?.totalCredits ?: 120
    val progress = completed.toFloat() / total.toFloat()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress Summary Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = UcsiNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CROSS-CAMPUS DEGREE AUDIT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = UcsiGoldLight,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = user?.program ?: "B.Sc. (Hons) Computing",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Surface(
                            color = Color(0xFF1E8E3E).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Active / In Good Standing",
                                color = Color(0xFF81C995),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Degree Progression: ${(progress * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$completed / $total Credits",
                            color = UcsiGoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = UcsiCrimson,
                        trackColor = Color.White.copy(alpha = 0.2f),
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("CUMULATIVE GPA", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("${user?.cgpa ?: 3.84} / 4.00", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("REMAINING", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("${total - completed} Credits", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("ACCREDITATION", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("MQA & UGC Aligned", color = UcsiGoldLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Dual Accreditation Breakdown
        item {
            Text(
                text = "Accreditation & Curriculum Mapping",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        val requirements = listOf(
            AccreditationItem("Core Computing Modules", 60, 60, "Malaysian Qualifications Agency (MQA) Core", true),
            AccreditationItem("General Studies & MPU", 12, 12, "UGC Bangladesh & MQA Requirement", true),
            AccreditationItem("Elective Specializations", 18, 12, "AI & Cloud Stream", false),
            AccreditationItem("Co-Op / Industrial Placement", 12, 0, "6-month Industry Co-Op in Dhaka/KL", false),
            AccreditationItem("Final Year Capstone Project", 18, 0, "Cross-campus joint faculty panel", false)
        )

        items(requirements) { item ->
            AccreditationRequirementCard(item)
        }
    }
}

data class AccreditationItem(
    val title: String,
    val totalCredits: Int,
    val earnedCredits: Int,
    val authorityNote: String,
    val isComplete: Boolean
)

@Composable
fun AccreditationRequirementCard(item: AccreditationItem) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (item.isComplete) StatusSuccess.copy(alpha = 0.15f) else UcsiNavy.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.isComplete) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                    contentDescription = null,
                    tint = if (item.isComplete) StatusSuccess else UcsiNavy,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = item.authorityNote, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = "${item.earnedCredits} / ${item.totalCredits} Credits earned",
                    fontSize = 11.sp,
                    color = if (item.isComplete) StatusSuccess else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (item.isComplete) StatusSuccess.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = if (item.isComplete) "COMPLETED" else "IN PROGRESS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isComplete) StatusSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun CampusMobilityTab(transferApps: List<TransferApplicationEntity>) {
    val stages = listOf("Draft", "Submitted", "Under Review", "Documents Required", "Approved", "Completed")
    val currentApp = transferApps.firstOrNull()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = UcsiCrimson),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "UCSI Cross-Campus Mobility",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Transfer seamlessly from Dhaka Branch Campus to UCSI Kuala Lumpur Main Campus (South Wing, Cheras) or Kuching Campus without credit loss.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Live Application Pipeline Tracker
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Your Transfer Application Tracker",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Target: ${currentApp?.targetCampus ?: "UCSI Kuala Lumpur (South Wing)"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Intake: ${currentApp?.targetIntake ?: "January 2027"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = UcsiCrimson
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Stages
                    stages.forEachIndexed { index, stage ->
                        val isDone = index < (currentApp?.currentStageIndex ?: 2)
                        val isCurrent = index == (currentApp?.currentStageIndex ?: 2)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isDone -> StatusSuccess
                                            isCurrent -> UcsiCrimson
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                } else {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stage,
                                    fontSize = 13.sp,
                                    fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    color = if (isCurrent) UcsiCrimson else MaterialTheme.colorScheme.onSurface
                                )
                                if (isCurrent) {
                                    Text(
                                        text = "Currently under academic committee review at Kuala Lumpur Campus.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Required Documents Checklist
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Required Transfer Documents",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DocumentCheckRow("Dhaka Campus Official Academic Transcript", true)
                    DocumentCheckRow("Passport Copy (Valid min. 18 months)", true)
                    DocumentCheckRow("Letter of Financial Guarantee", true)
                    DocumentCheckRow("Malaysian EMGS Student Visa Clearance", false)
                }
            }
        }
    }
}

@Composable
fun DocumentCheckRow(title: String, verified: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (verified) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (verified) StatusSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TimetableSyncTab(
    courses: List<CourseEntity>,
    selectedTimezone: String,
    onTimezoneToggle: () -> Unit,
    onMarkAttendanceClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Timezone Sync Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = UcsiNavyContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dual-Timezone Synchronization",
                            fontWeight = FontWeight.Bold,
                            color = UcsiOnNavyContainer,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (selectedTimezone == "BST")
                                "Displaying in Dhaka Time (BST, UTC+6). Malaysia is +2 hours ahead."
                            else
                                "Displaying in Kuala Lumpur Time (MYT, UTC+8).",
                            color = UcsiOnNavyContainer.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = onTimezoneToggle,
                        colors = ButtonDefaults.buttonColors(containerColor = UcsiCrimson),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Switch TZ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(courses, key = { it.id }) { course ->
            CourseTimetableCard(
                course = course,
                selectedTimezone = selectedTimezone,
                onMarkAttendanceClick = onMarkAttendanceClick
            )
        }
    }
}

@Composable
fun CourseTimetableCard(
    course: CourseEntity,
    selectedTimezone: String,
    onMarkAttendanceClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = UcsiCrimsonContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = course.courseCode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = UcsiOnCrimsonContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        if (course.isOnlineHybrid) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = UcsiNavyContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "HYBRID (KL/DHAKA)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UcsiOnNavyContainer,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = course.courseTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = course.facultyName,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${course.attendancePercent}%",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = if (course.attendancePercent >= 80) StatusSuccess else StatusError
                    )
                    Text(
                        text = "Attendance",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = UcsiCrimson, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (selectedTimezone == "BST") "${course.startTimeBst} - ${course.endTimeBst} (BST)" else "${course.startTimeMyt} - ${course.endTimeMyt} (MYT)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = UcsiNavy, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${course.room} • Level ${course.floor}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { onMarkAttendanceClick(course.courseCode) },
                    colors = ButtonDefaults.buttonColors(containerColor = UcsiNavy),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Self Check-In", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourseEntity
import com.example.data.model.EmergencyAlertEntity
import com.example.data.model.UserEntity
import com.example.ui.components.EmergencyAlertBanner
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    user: UserEntity?,
    courses: List<CourseEntity>,
    activeAlerts: List<EmergencyAlertEntity>,
    selectedTimezone: String,
    onNavigateToTab: (Int) -> Unit,
    onNavigateToId: () -> Unit,
    onNavigateToCommute: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onMarkAttendanceClick: (String) -> Unit,
    onDismissAlert: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // High-Priority Emergency Alerts
        if (activeAlerts.isNotEmpty()) {
            items(activeAlerts, key = { it.id }) { alert ->
                EmergencyAlertBanner(alert = alert, onDismiss = { onDismissAlert(alert.id) })
            }
        }

        // Student Welcome & Profile Hero Card
        item {
            StudentProfileHeroCard(
                user = user,
                onViewIdClick = onNavigateToId
            )
        }

        // Today's Class Schedule Spotlight
        item {
            TodayClassSpotlight(
                courses = courses,
                selectedTimezone = selectedTimezone,
                onMarkAttendanceClick = onMarkAttendanceClick,
                onViewFullTimetable = { onNavigateToTab(1) }
            )
        }

        // Quick Actions Grid (8 shortcuts)
        item {
            QuickActionsSection(
                onNavigateToTab = onNavigateToTab,
                onNavigateToId = onNavigateToId,
                onNavigateToCommute = onNavigateToCommute,
                onNavigateToEmergency = onNavigateToEmergency
            )
        }

        // Academic & Campus Notices
        item {
            ImportantAnnouncementsSection(
                onExploreMobility = { onNavigateToTab(1) },
                onPayFees = { onNavigateToTab(3) }
            )
        }
    }
}

@Composable
fun StudentProfileHeroCard(
    user: UserEntity?,
    onViewIdClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("student_profile_hero_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = UcsiNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(UcsiNavy, Color(0xFF1E355D), UcsiCrimsonDark)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Avatar with Initials
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(UcsiCrimson)
                                .border(2.dp, UcsiGoldLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user?.fullName?.split(" ")?.mapNotNull { it.firstOrNull() }?.take(2)?.joinToString("") ?: "SR",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }

                        Column {
                            Text(
                                text = "Welcome back,",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = user?.fullName ?: "Sarah Rahman",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "ID: ${user?.studentOrStaffId ?: "10023419"}",
                                color = UcsiGoldLight,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Digital ID Shortcut Button
                    OutlinedButton(
                        onClick = onViewIdClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(Color.White, UcsiGoldLight))
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("hero_view_digital_id_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = UcsiGoldLight
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Smart ID", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(12.dp))

                // Academic Stat Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "PROGRAM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = user?.program ?: "B.Sc. (Hons) Computing",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "CGPA / STANDING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${user?.cgpa ?: 3.84}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = UcsiGoldLight
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "• First Class",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodayClassSpotlight(
    courses: List<CourseEntity>,
    selectedTimezone: String,
    onMarkAttendanceClick: (String) -> Unit,
    onViewFullTimetable: () -> Unit
) {
    val nextClass = courses.firstOrNull() ?: return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = UcsiCrimson,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Today's Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            TextButton(
                onClick = onViewFullTimetable,
                modifier = Modifier.testTag("view_full_timetable_button")
            ) {
                Text(
                    "View All",
                    color = UcsiCrimson,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = UcsiCrimson
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("today_next_class_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = nextClass.courseCode,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = UcsiOnCrimsonContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = StatusSuccess.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "NEXT UP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusSuccess,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = nextClass.courseTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = nextClass.facultyName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Attendance circular progress badge
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${nextClass.attendancePercent}%",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = if (nextClass.attendancePercent >= 80) StatusSuccess else StatusError
                        )
                        Text(
                            text = "Attendance",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Time & Room
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = UcsiCrimson,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (selectedTimezone == "BST") "${nextClass.startTimeBst} - ${nextClass.endTimeBst}" else "${nextClass.startTimeMyt} - ${nextClass.endTimeMyt}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = UcsiNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${nextClass.room} (Floor ${nextClass.floor})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Check-in button
                    Button(
                        onClick = { onMarkAttendanceClick(nextClass.courseCode) },
                        colors = ButtonDefaults.buttonColors(containerColor = UcsiCrimson),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("quick_checkin_button_${nextClass.courseCode}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Check-In", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    onNavigateToTab: (Int) -> Unit,
    onNavigateToId: () -> Unit,
    onNavigateToCommute: () -> Unit,
    onNavigateToEmergency: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Campus Services & Shortcuts",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val actions = listOf(
            QuickActionItem("Smart ID", Icons.Default.Badge, UcsiCrimson) { onNavigateToId() },
            QuickActionItem("Campus Map", Icons.Default.Layers, UcsiNavy) { onNavigateToTab(2) },
            QuickActionItem("Attendance", Icons.Default.QrCodeScanner, StatusSuccess) { onNavigateToId() },
            QuickActionItem("Pay Fees", Icons.Default.AccountBalanceWallet, Color(0xFFD97706)) { onNavigateToTab(3) },
            QuickActionItem("Book Pod", Icons.Default.MeetingRoom, Color(0xFF0284C7)) { onNavigateToTab(2) },
            QuickActionItem("Transfer 2+1", Icons.Default.FlightTakeoff, Color(0xFF7C3AED)) { onNavigateToTab(1) },
            QuickActionItem("Dhaka Commute", Icons.Default.DirectionsCar, Color(0xFF059669)) { onNavigateToCommute() },
            QuickActionItem("SOS / Safety", Icons.Default.Emergency, StatusError) { onNavigateToEmergency() }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            actions.take(4).forEach { item ->
                QuickActionButton(item = item, modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            actions.drop(4).take(4).forEach { item ->
                QuickActionButton(item = item, modifier = Modifier.weight(1f))
            }
        }
    }
}

data class QuickActionItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun QuickActionButton(item: QuickActionItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clickable { item.onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(item.color.copy(alpha = 0.12f))
                .border(1.dp, item.color.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = item.color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ImportantAnnouncementsSection(
    onExploreMobility: () -> Unit,
    onPayFees: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = "Official Campus Bulletins",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Bulletin 1: Dual-Campus Malaysia Mobility
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(UcsiCrimson.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = UcsiCrimson, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UCSI KL Main Campus 2+1 Transfer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Applications for Jan 2027 intake close Nov 30. Dual accreditation (MQA & UGC).",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = onExploreMobility) {
                    Text("Apply", color = UcsiCrimson, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Bulletin 2: Financial clearance
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD97706).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Fall Trimester Fee Clearance",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Pay tuition via bKash, Nagad or Cards before midterm examinations.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = onPayFees) {
                    Text("Pay", color = Color(0xFFD97706), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

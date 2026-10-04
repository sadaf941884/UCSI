package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.UcsiTopAppBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.UcsiCampusViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: UcsiCampusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: UcsiCampusViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    val documents by viewModel.documents.collectAsStateWithLifecycle()
    val activeAlerts by viewModel.activeEmergencyAlerts.collectAsStateWithLifecycle()
    val carpools by viewModel.carpools.collectAsStateWithLifecycle()
    val transferApps by viewModel.transferApplications.collectAsStateWithLifecycle()
    val internships by viewModel.internships.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()

    val selectedTimezone by viewModel.selectedTimezone.collectAsStateWithLifecycle()
    val dynamicQrPayload by viewModel.dynamicQrPayload.collectAsStateWithLifecycle()
    val qrSecondsRemaining by viewModel.qrSecondsRemaining.collectAsStateWithLifecycle()
    val uiNotice by viewModel.uiNotice.collectAsStateWithLifecycle()
    val globalSearchQuery by viewModel.globalSearchQuery.collectAsStateWithLifecycle()
    val sessionToken by viewModel.sessionToken.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Screen State: 0: Home, 1: Academics, 2: Campus Map, 3: Services, 4: Hub / More
    var currentTab by remember { mutableIntStateOf(0) }
    var activeSubScreen by remember { mutableStateOf<String?>(null) } // "ID", "COMMUTE", "CAREER", "ADMIN", "SEARCH", "SECURITY"

    var showRoleSwitcherDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }

    // Show Snackbar when notice changes
    LaunchedEffect(uiNotice) {
        uiNotice?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUiNotice()
        }
    }

    // Handle Android system back gesture smoothly
    BackHandler(enabled = activeSubScreen != null || currentTab != 0) {
        if (activeSubScreen != null) {
            activeSubScreen = null
        } else if (currentTab != 0) {
            currentTab = 0
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (activeSubScreen != "SEARCH") {
                UcsiTopAppBar(
                    currentRole = currentUser?.role ?: "STUDENT",
                    selectedTimezone = selectedTimezone,
                    onTimezoneToggle = {
                        viewModel.setTimezone(if (selectedTimezone == "BST") "MYT" else "BST")
                    },
                    onRoleSwitchClick = { showRoleSwitcherDialog = true },
                    onSearchClick = { activeSubScreen = "SEARCH" },
                    onNotificationClick = {
                        activeSubScreen = "COMMUTE"
                    },
                    hasActiveAlerts = activeAlerts.isNotEmpty()
                )
            }
        },
        bottomBar = {
            if (activeSubScreen == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    val items = listOf(
                        Triple("Home", Icons.Filled.Home, Icons.Outlined.Home),
                        Triple("Academics", Icons.Filled.School, Icons.Outlined.School),
                        Triple("Campus", Icons.Filled.Layers, Icons.Outlined.Layers),
                        Triple("Services", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
                        Triple("More", Icons.Filled.Menu, Icons.Outlined.Menu)
                    )

                    items.forEachIndexed { index, (label, selectedIcon, unselectedIcon) ->
                        val isSelected = currentTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = index },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                    contentDescription = label
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = UcsiCrimson,
                                selectedTextColor = UcsiCrimson,
                                indicatorColor = UcsiCrimsonContainer
                            ),
                            modifier = Modifier.testTag("nav_item_$index")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // Secondary Full-Screen Sub-screens
                activeSubScreen == "SEARCH" -> {
                    SearchScreen(
                        searchQuery = globalSearchQuery,
                        onSearchQueryChange = { viewModel.setGlobalSearchQuery(it) },
                        courses = courses,
                        internships = internships,
                        onCourseClick = {
                            activeSubScreen = null
                            currentTab = 1
                        },
                        onNavigateBack = { activeSubScreen = null }
                    )
                }

                activeSubScreen == "ID" -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SubScreenHeader(title = "Smart Campus Digital ID", onBack = { activeSubScreen = null })
                        StudentIdScreen(
                            user = currentUser,
                            courses = courses,
                            dynamicQrPayload = dynamicQrPayload,
                            qrSecondsRemaining = qrSecondsRemaining,
                            onRecordAttendance = { code, method ->
                                viewModel.recordAttendance(code, method)
                            }
                        )
                    }
                }

                activeSubScreen == "COMMUTE" -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SubScreenHeader(title = "Dhaka Commute & Safety", onBack = { activeSubScreen = null })
                        CommuteScreen(
                            carpools = carpools,
                            onPublishCarpool = { origin, time, seats, vehicle, notes ->
                                viewModel.publishCarpool(origin, time, seats, vehicle, notes)
                            }
                        )
                    }
                }

                activeSubScreen == "CAREER" -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SubScreenHeader(title = "Career & Co-Op Hub", onBack = { activeSubScreen = null })
                        CareerScreen(
                            internships = internships,
                            onApplyInternship = { id, comp ->
                                viewModel.applyInternship(id, comp)
                            }
                        )
                    }
                }

                activeSubScreen == "ADMIN" -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SubScreenHeader(title = "Admin Console & Logs", onBack = { activeSubScreen = null })
                        AdminDashboardScreen(
                            auditLogs = auditLogs,
                            onBroadcastAlert = { title, msg, sev ->
                                viewModel.broadcastAlert(title, msg, sev)
                            }
                        )
                    }
                }

                // Primary Tab Screens
                currentTab == 0 -> {
                    HomeScreen(
                        user = currentUser,
                        courses = courses,
                        activeAlerts = activeAlerts,
                        selectedTimezone = selectedTimezone,
                        onNavigateToTab = { currentTab = it },
                        onNavigateToId = { activeSubScreen = "ID" },
                        onNavigateToCommute = { activeSubScreen = "COMMUTE" },
                        onNavigateToEmergency = { activeSubScreen = "COMMUTE" },
                        onMarkAttendanceClick = { courseCode ->
                            viewModel.recordAttendance(courseCode, "One-Tap Fast Check-In")
                        },
                        onDismissAlert = { alertId ->
                            viewModel.dismissAlert(alertId)
                        }
                    )
                }

                currentTab == 1 -> {
                    AcademicsScreen(
                        user = currentUser,
                        courses = courses,
                        transferApps = transferApps,
                        selectedTimezone = selectedTimezone,
                        onTimezoneToggle = {
                            viewModel.setTimezone(if (selectedTimezone == "BST") "MYT" else "BST")
                        },
                        onMarkAttendanceClick = { courseCode ->
                            viewModel.recordAttendance(courseCode, "Academic Schedule Check-In")
                        }
                    )
                }

                currentTab == 2 -> {
                    CampusMapScreen(
                        bookings = bookings,
                        onBookFacility = { name, type, floor, date, slot ->
                            viewModel.bookFacility(name, type, floor, date, slot)
                        },
                        onCancelBooking = { bookingId ->
                            viewModel.cancelBooking(bookingId)
                        }
                    )
                }

                currentTab == 3 -> {
                    ServicesScreen(
                        user = currentUser,
                        payments = payments,
                        documents = documents,
                        onProcessPayment = { title, amount, gateway ->
                            viewModel.processPayment(title, amount, gateway)
                        },
                        onSubmitDocRequest = { type, purpose ->
                            viewModel.submitDocumentRequest(type, purpose)
                        }
                    )
                }

                currentTab == 4 -> {
                    MoreHubScreen(
                        currentUser = currentUser,
                        onNavigateToId = { activeSubScreen = "ID" },
                        onNavigateToCareer = { activeSubScreen = "CAREER" },
                        onNavigateToCommute = { activeSubScreen = "COMMUTE" },
                        onNavigateToAdmin = { activeSubScreen = "ADMIN" },
                        onOpenSecurity = { showSecurityDialog = true },
                        onSwitchRoleClick = { showRoleSwitcherDialog = true },
                        onLogout = { viewModel.logout() }
                    )
                }
            }
        }
    }

    if (showRoleSwitcherDialog) {
        RoleSwitcherDialog(
            currentUser = currentUser,
            onDismiss = { showRoleSwitcherDialog = false },
            onRoleSelected = { newRole ->
                viewModel.switchUserRole(newRole)
            }
        )
    }

    if (showSecurityDialog) {
        SecuritySettingsDialog(
            user = currentUser,
            sessionToken = sessionToken,
            onDismiss = { showSecurityDialog = false }
        )
    }
}

@Composable
fun SubScreenHeader(title: String, onBack: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MoreHubScreen(
    currentUser: com.example.data.model.UserEntity?,
    onNavigateToId: () -> Unit,
    onNavigateToCareer: () -> Unit,
    onNavigateToCommute: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onOpenSecurity: () -> Unit,
    onSwitchRoleClick: () -> Unit,
    onLogout: () -> Unit
) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("more_hub_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User profile strip
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(UcsiCrimson, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.fullName?.take(2) ?: "SR",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(currentUser?.fullName ?: "Sarah Rahman", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(currentUser?.email ?: "student@ucsiuniversity.edu.my", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Role: ${currentUser?.role ?: "STUDENT"}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = UcsiNavy)
                    }
                    OutlinedButton(
                        onClick = onSwitchRoleClick,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Switch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Campus Ecosystem Modules", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        item {
            HubMenuItem(
                title = "Smart Campus Digital ID",
                subtitle = "Time-sensitive dynamic QR pass & NFC turnstile access",
                icon = Icons.Default.Badge,
                iconColor = UcsiCrimson,
                onClick = onNavigateToId
            )
        }

        item {
            HubMenuItem(
                title = "Career & Co-Op Hub",
                subtitle = "Mandatory industrial placement, top employers & alumni mentors",
                icon = Icons.Default.Work,
                iconColor = Color(0xFF0284C7),
                onClick = onNavigateToCareer
            )
        }

        item {
            HubMenuItem(
                title = "Dhaka Commute & Safety",
                subtitle = "Banani traffic corridor telemetry, student carpools & SOS",
                icon = Icons.Default.DirectionsCar,
                iconColor = Color(0xFF059669),
                onClick = onNavigateToCommute
            )
        }

        item {
            HubMenuItem(
                title = "Security & Encryption Center",
                subtitle = "Active session tokens, 2FA settings, biometrics & cipher info",
                icon = Icons.Default.Security,
                iconColor = Color(0xFF7C3AED),
                onClick = onOpenSecurity
            )
        }

        // Admin Dashboard Link (Highlighted if Admin role)
        item {
            val isAdmin = currentUser?.role == "ADMIN"
            HubMenuItem(
                title = "Administrator Console",
                subtitle = if (isAdmin) "Full access granted: Emergency broadcaster & audit logs" else "Switch to ADMIN role via top bar to manage campus",
                icon = Icons.Default.AdminPanelSettings,
                iconColor = if (isAdmin) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onNavigateToAdmin
            )
        }

        // Institutional info card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("UCSI University Bangladesh Branch Campus", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Banani, Dhaka • Approved by Ministry of Education & UGC Bangladesh", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Cross-campus accreditation partner: UCSI University Malaysia (QS World Top 300)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun HubMenuItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

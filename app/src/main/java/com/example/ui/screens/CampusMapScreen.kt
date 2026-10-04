package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.FacilityBookingEntity
import com.example.ui.theme.*

data class CampusFloor(
    val levelNumber: Int,
    val name: String,
    val category: String,
    val highlights: List<String>,
    val rooms: List<String>
)

val BananiCampusFloors = listOf(
    CampusFloor(0, "Ground Level (G)", "Reception & Security", listOf("Main Turnstiles", "Visitor Lounge", "Admissions Bureau"), listOf("Room G01 (Admissions)", "Security Desk", "Medical First Aid")),
    CampusFloor(1, "Level 1", "Dining & Social Hub", listOf("University Cafeteria", "Student Council Room", "Barista Lounge"), listOf("Cafeteria A", "Cafeteria B", "Student Lounge")),
    CampusFloor(2, "Level 2", "Library & Information Commons", listOf("Digital Library", "Quiet Research Stacks", "Book Checkout"), listOf("Stacks Room 201", "Silent Study 202", "Archive 203")),
    CampusFloor(4, "Level 4", "Auditorium & Lecture Halls", listOf("Central Auditorium", "Audio-Visual Seminar 401", "Moffat Hall"), listOf("Auditorium A", "Room 401", "Room 402", "Room 403")),
    CampusFloor(6, "Level 6", "Discussion Rooms & Pods", listOf("Collaborative Workspaces", "Discussion Suites 6A-6D"), listOf("Suite 6A", "Suite 6B", "Suite 6C", "Suite 6D")),
    CampusFloor(7, "Level 7", "Computer Science & Engineering", listOf("Software Engineering Lab", "Room 703 (Cloud & Dist. Systems)", "Study Pods 7A-7D"), listOf("Room 701", "Room 702", "Room 703", "Study Pod 7A", "Study Pod 7B")),
    CampusFloor(8, "Level 8", "AI, Robotics & IoT Embedded Labs", listOf("Lab 802 (DevOps & AI)", "Hardware Testing Bench", "Robotics Arena"), listOf("Lab 801", "Lab 802", "Lab 803", "Robotics Arena")),
    CampusFloor(10, "Level 10", "Faculty & Administration", listOf("Dean's Office", "Faculty Suites", "Proctorial Board"), listOf("Dean Suite 1001", "Faculty CS 1002", "Faculty BBA 1003")),
    CampusFloor(12, "Level 12", "Executive Board & Conference", listOf("Senate Boardroom", "Executive Meeting 1201"), listOf("Senate Chamber", "Boardroom 1201", "VIP Lounge")),
    CampusFloor(14, "Level 14", "Rooftop Garden & Prayer Hall", listOf("Sky Garden", "Male Prayer Room", "Female Prayer Room", "Observatory"), listOf("Garden Deck", "Prayer Hall M", "Prayer Hall F"))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusMapScreen(
    bookings: List<FacilityBookingEntity>,
    onBookFacility: (name: String, type: String, floor: Int, date: String, slot: String) -> Unit,
    onCancelBooking: (Long) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFloorLevel by remember { mutableIntStateOf(7) }
    var roomSearchQuery by remember { mutableStateOf("") }
    var showBookingDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("campus_map_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = UcsiCrimson
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Indoor Map & Floors", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Elevator & Rush Hour", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Book Pod / Lab", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        when (selectedTab) {
            0 -> IndoorNavigationTab(
                selectedFloorLevel = selectedFloorLevel,
                onSelectFloor = { selectedFloorLevel = it },
                roomSearchQuery = roomSearchQuery,
                onRoomSearchQueryChange = { query ->
                    roomSearchQuery = query
                    // Smart floor auto-detection from room search e.g. "703" -> Floor 7, "802" -> Floor 8
                    val digits = query.filter { it.isDigit() }
                    if (digits.length >= 3) {
                        val floorDigit = digits.dropLast(2).toIntOrNull()
                        if (floorDigit != null && BananiCampusFloors.any { it.levelNumber == floorDigit }) {
                            selectedFloorLevel = floorDigit
                        }
                    }
                }
            )
            1 -> ElevatorTrafficTab()
            2 -> StudyPodBookingTab(
                bookings = bookings,
                onOpenBookingDialog = { showBookingDialog = true },
                onCancelBooking = onCancelBooking
            )
        }
    }

    if (showBookingDialog) {
        BookingModalDialog(
            onDismiss = { showBookingDialog = false },
            onConfirm = { name, type, floor, date, slot ->
                onBookFacility(name, type, floor, date, slot)
                showBookingDialog = false
            }
        )
    }
}

@Composable
fun IndoorNavigationTab(
    selectedFloorLevel: Int,
    onSelectFloor: (Int) -> Unit,
    roomSearchQuery: String,
    onRoomSearchQueryChange: (String) -> Unit
) {
    val currentFloor = BananiCampusFloors.firstOrNull { it.levelNumber == selectedFloorLevel } ?: BananiCampusFloors[5]

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Room Quick Search Bar
        item {
            OutlinedTextField(
                value = roomSearchQuery,
                onValueChange = onRoomSearchQueryChange,
                placeholder = { Text("Search room e.g. 'Room 703' or 'Lab 802'...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = UcsiCrimson) },
                trailingIcon = {
                    if (roomSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { onRoomSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("room_search_textfield"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Vertical Floor Selector Carousel
        item {
            Text(
                text = "Vertical Campus Floors (Banani Tower)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(BananiCampusFloors) { floor ->
                    val isSelected = floor.levelNumber == selectedFloorLevel
                    Surface(
                        onClick = { onSelectFloor(floor.levelNumber) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) UcsiCrimson else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder,
                        modifier = Modifier.testTag("floor_chip_${floor.levelNumber}")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (floor.levelNumber == 0) "Floor G" else "Floor ${floor.levelNumber}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = floor.category.take(12),
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Active Floor Interactive Detail Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentFloor.name,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = UcsiCrimson
                            )
                            Text(
                                text = currentFloor.category,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = UcsiNavyContainer
                        ) {
                            Text(
                                text = "Lift Bank A & B Access",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = UcsiOnNavyContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "KEY FACILITIES & ROOM DIRECTORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // List of Rooms on this floor
                    currentFloor.rooms.forEach { room ->
                        val isHighlighted = roomSearchQuery.isNotBlank() && room.contains(roomSearchQuery, ignoreCase = true)
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isHighlighted) UcsiGoldContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MeetingRoom,
                                        contentDescription = null,
                                        tint = if (isHighlighted) UcsiOnGoldContainer else UcsiNavy,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = room,
                                        fontWeight = if (isHighlighted) FontWeight.Black else FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = if (isHighlighted) UcsiOnGoldContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                if (isHighlighted) {
                                    Text("SEARCH MATCH", fontSize = 10.sp, fontWeight = FontWeight.Black, color = UcsiOnGoldContainer)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Floor Highlights: ${currentFloor.highlights.joinToString(" • ")}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ElevatorTrafficTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = UcsiNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Elevator, contentDescription = null, tint = UcsiGoldLight, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Vertical Transit Telemetry",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Real-time estimated elevator waiting times and rush hour traffic at UCSI Banani Campus Tower.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Elevator Bank A
        item {
            ElevatorStatusCard(
                bankName = "Lift Bank A (Express Low-Rise)",
                serviceRange = "Floors Ground to 7",
                currentFloor = "Floor 4 (Ascending)",
                waitSec = 45,
                congestionLevel = "Normal",
                statusColor = StatusSuccess
            )
        }

        // Elevator Bank B
        item {
            ElevatorStatusCard(
                bankName = "Lift Bank B (High-Rise Priority)",
                serviceRange = "Floors 8 to 14 (Rooftop)",
                currentFloor = "Floor 10 (Descending)",
                waitSec = 160,
                congestionLevel = "Heavy (Between Class Rush)",
                statusColor = StatusError
            )
        }

        // Staircase Optimization Recommendation
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        tint = UcsiCrimson,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Smart Staircase Recommendation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Traveling between Ground and Floor 4? Fire Staircase B takes under 2 minutes and bypasses the 2m 40s elevator queue.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ElevatorStatusCard(
    bankName: String,
    serviceRange: String,
    currentFloor: String,
    waitSec: Int,
    congestionLevel: String,
    statusColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(text = bankName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = serviceRange, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = congestionLevel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "CURRENT POSITION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = currentFloor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "EST. WAIT TIME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${waitSec / 60}m ${waitSec % 60}s", fontSize = 16.sp, fontWeight = FontWeight.Black, color = statusColor)
                }
            }
        }
    }
}

@Composable
fun StudyPodBookingTab(
    bookings: List<FacilityBookingEntity>,
    onOpenBookingDialog: () -> Unit,
    onCancelBooking: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = UcsiCrimsonContainer)
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
                            text = "Reserve Campus Facility",
                            fontWeight = FontWeight.Bold,
                            color = UcsiOnCrimsonContainer,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Book study pods, robotics lab stations & seminar rooms with server-side collision prevention.",
                            color = UcsiOnCrimsonContainer.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = onOpenBookingDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = UcsiCrimson),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("open_facility_booking_button")
                    ) {
                        Text("+ Reserve", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "Your Active Reservations (${bookings.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (bookings.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EventBusy, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No upcoming reservations found", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Tap '+ Reserve' above to schedule a pod or lab station.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(bookings, key = { it.id }) { booking ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = booking.facilityName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (booking.status == "CONFIRMED") StatusSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = booking.status,
                                        color = if (booking.status == "CONFIRMED") StatusSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(text = "Level ${booking.floor} • ${booking.date}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Slot: ${booking.timeSlot}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UcsiCrimson)
                        }

                        if (booking.status == "CONFIRMED") {
                            OutlinedButton(
                                onClick = { onCancelBooking(booking.id) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cancel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingModalDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: String, floor: Int, date: String, slot: String) -> Unit
) {
    val facilities = listOf(
        Triple("Study Pod 7A", "POD", 7),
        Triple("Study Pod 7B", "POD", 7),
        Triple("Robotics Lab Workstation 4", "LAB", 8),
        Triple("Discussion Room 6B", "ROOM", 6),
        Triple("Quiet Research Pod 203", "POD", 2)
    )
    val slots = listOf("09:00 AM - 11:00 AM", "11:30 AM - 01:30 PM", "02:00 PM - 04:00 PM", "04:30 PM - 06:30 PM")

    var selectedFacilityIndex by remember { mutableIntStateOf(0) }
    var selectedSlotIndex by remember { mutableIntStateOf(0) }
    val date = "2026-10-02"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reserve Campus Facility", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Select Facility & Room:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                facilities.forEachIndexed { index, (name, _, floor) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedFacilityIndex = index }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedFacilityIndex == index,
                            onClick = { selectedFacilityIndex = index }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "$name (Floor $floor)", fontSize = 13.sp)
                    }
                }

                HorizontalDivider()
                Text("Select Time Slot:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                slots.forEachIndexed { index, slot ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSlotIndex = index }
                            .padding(vertical = 2.dp)
                    ) {
                        RadioButton(
                            selected = selectedSlotIndex == index,
                            onClick = { selectedSlotIndex = index }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = slot, fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val (facName, facType, facFloor) = facilities[selectedFacilityIndex]
                    val chosenSlot = slots[selectedSlotIndex]
                    onConfirm(facName, facType, facFloor, date, chosenSlot)
                },
                colors = ButtonDefaults.buttonColors(containerColor = UcsiCrimson)
            ) {
                Text("Confirm Booking", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

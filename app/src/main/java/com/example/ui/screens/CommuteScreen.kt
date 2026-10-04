package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CarpoolRouteEntity
import com.example.ui.theme.*

@Composable
fun CommuteScreen(
    carpools: List<CarpoolRouteEntity>,
    onPublishCarpool: (origin: String, time: String, seats: Int, vehicle: String, notes: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCarpoolDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("commute_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = UcsiCrimson
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Dhaka Traffic & Shuttles", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Student Carpool", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Emergency SOS", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        when (selectedTab) {
            0 -> DhakaTrafficTab()
            1 -> StudentCarpoolTab(
                carpools = carpools,
                onOpenPostModal = { showCarpoolDialog = true }
            )
            2 -> EmergencySosTab(
                onCallNumber = { num ->
                    try {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num"))
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                }
            )
        }
    }

    if (showCarpoolDialog) {
        CarpoolPostModal(
            onDismiss = { showCarpoolDialog = false },
            onConfirm = { origin, time, seats, vehicle, notes ->
                onPublishCarpool(origin, time, seats, vehicle, notes)
                showCarpoolDialog = false
            }
        )
    }
}

@Composable
fun DhakaTrafficTab() {
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
                        Icon(Icons.Default.Traffic, contentDescription = null, tint = UcsiGoldLight, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Banani Campus Transit Intel", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Live commute telemetry for main thoroughfares connecting to UCSI University Banani Tower.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Live Corridor Statuses
        item {
            TrafficCorridorCard(
                corridorName = "Kemal Ataturk Avenue",
                subLocation = "Banani Circle to Gulshan-2 Intersection",
                travelTime = "~12 mins",
                flowStatus = "MODERATE FLOW",
                statusColor = StatusWarning
            )
        }
        item {
            TrafficCorridorCard(
                corridorName = "Road 11 Banani",
                subLocation = "Commercial Avenue to Bridge approach",
                travelTime = "~24 mins",
                flowStatus = "HEAVY TRAFFIC (WATERLOGGING)",
                statusColor = StatusError
            )
        }
        item {
            TrafficCorridorCard(
                corridorName = "Airport Road / Mohakhali Flyover",
                subLocation = "Radisson - Kakoli - Banani Entry",
                travelTime = "~14 mins",
                flowStatus = "FREE FLOWING",
                statusColor = StatusSuccess
            )
        }

        // University Shuttle Status
        item {
            Text(
                text = "Official Campus Shuttles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        val shuttles = listOf(
            Triple("Route 1: Uttara Sector 11 ⇄ Banani Tower", "Departs 07:30 AM & 03:30 PM", "ON TIME"),
            Triple("Route 2: Dhanmondi 27 ⇄ Banani Tower", "Departs 07:45 AM & 04:00 PM", "DELAYED (+15m)"),
            Triple("Route 3: Mirpur 10 ⇄ Banani Tower", "Departs 08:00 AM & 04:15 PM", "ON TIME")
        )

        items(shuttles) { (route, timing, status) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(route, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(timing, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (status.contains("DELAYED")) StatusError.copy(alpha = 0.15f) else StatusSuccess.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = status,
                            color = if (status.contains("DELAYED")) StatusError else StatusSuccess,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrafficCorridorCard(
    corridorName: String,
    subLocation: String,
    travelTime: String,
    flowStatus: String,
    statusColor: Color
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(corridorName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(subLocation, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = flowStatus,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = UcsiCrimson, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Est. Travel Time: $travelTime to Banani Gate", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun StudentCarpoolTab(
    carpools: List<CarpoolRouteEntity>,
    onOpenPostModal: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F5132).copy(alpha = 0.15f)),
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
                        Text("Verified Student Carpool", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F5132))
                        Text("Ride with verified UCSI students across Dhaka safely and split fuel.", fontSize = 11.sp)
                    }
                    Button(
                        onClick = onOpenPostModal,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F5132)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("post_carpool_button")
                    ) {
                        Text("+ Offer Ride", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "Available Student Rides (${carpools.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(carpools, key = { it.id }) { ride ->
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
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(ride.driverName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = StatusSuccess.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("VERIFIED ID", color = StatusSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Text("ID: ${ride.driverStudentId}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            color = UcsiCrimsonContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${ride.availableSeats} Seats Left",
                                color = UcsiOnCrimsonContainer,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Route: ${ride.origin}  ➔  ${ride.destination}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = UcsiNavy
                    )
                    Text(
                        text = "Departure: ${ride.departureTime} • Vehicle: ${ride.vehicleInfo}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ride.routeNotes,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { /* Simulated ride reservation */ },
                        colors = ButtonDefaults.buttonColors(containerColor = UcsiCrimson),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Request Seat in Carpool", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencySosTab(onCallNumber: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Emergency, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("UCSI Campus Emergency & Safety SOS", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Instant 24/7 direct helplines for Banani Tower security, medical first aid, student proctorial office, and police.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        val helplines = listOf(
            Triple("Campus Security Control Desk (Ground Floor)", "+880 1711 000 001", "24/7 Physical Security & Lost Items"),
            Triple("Campus Medical & First Aid Station (Level G)", "+880 1711 000 002", "Emergency Doctor & Paramedic on duty"),
            Triple("Proctorial Committee & Student Affairs", "+880 1711 000 003", "Safety, Harassment & Disciplinary Desk"),
            Triple("Bangladesh National Emergency (Police/Ambulance)", "999", "National Emergency Responder Service")
        )

        items(helplines) { (title, phone, subtitle) ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(phone, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.error)
                    }
                    Button(
                        onClick = { onCallNumber(phone) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CarpoolPostModal(
    onDismiss: () -> Unit,
    onConfirm: (origin: String, time: String, seats: Int, vehicle: String, notes: String) -> Unit
) {
    var origin by remember { mutableStateOf("Uttara Sector 7 (Rabindra Sarani)") }
    var time by remember { mutableStateOf("07:45 AM") }
    var seatsText by remember { mutableStateOf("3") }
    var vehicle by remember { mutableStateOf("Toyota Corolla (Silver)") }
    var notes by remember { mutableStateOf("Non-smoking, AC ride. Dropping at Banani Tower main gate.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Offer a Student Carpool", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = origin, onValueChange = { origin = it }, label = { Text("Starting Pickup Point") }, singleLine = true)
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Departure Time") }, singleLine = true)
                OutlinedTextField(value = seatsText, onValueChange = { seatsText = it }, label = { Text("Available Seats") }, singleLine = true)
                OutlinedTextField(value = vehicle, onValueChange = { vehicle = it }, label = { Text("Vehicle Model & Color") }, singleLine = true)
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Route & Passenger Notes") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(origin, time, seatsText.toIntOrNull() ?: 2, vehicle, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = UcsiCrimson)
            ) {
                Text("Publish to Student Board", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

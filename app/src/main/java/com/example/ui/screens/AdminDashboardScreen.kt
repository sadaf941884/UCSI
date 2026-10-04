package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLogEntity
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    auditLogs: List<AuditLogEntity>,
    onBroadcastAlert: (title: String, message: String, severity: String) -> Unit
) {
    var showBroadcastModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Administrative Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
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
                                text = "CAMPUS REGISTRAR & ADMIN CONSOLE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "UCSI Bangladesh Branch Campus",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.error
                        ) {
                            Text(
                                text = "ADMIN PRIVILEGE",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showBroadcastModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_broadcast_alert_button")
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Broadcast High-Priority Campus Alert", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Campus Telemetry Metric Cards
        item {
            Text("Campus Operations Telemetry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricCard("Enrolled Students", "1,480", "Banani Campus", Modifier.weight(1f))
                AdminMetricCard("Facility Occupancy", "84%", "Tower Lifts & Labs", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricCard("Dual-Campus 2+1", "42 Apps", "KL South Wing", Modifier.weight(1f))
                AdminMetricCard("Fees Reconciled", "৳ 4.8M", "bKash / Nagad", Modifier.weight(1f))
            }
        }

        // Security Audit Logs
        item {
            Text("Security & System Audit Trail (${auditLogs.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(auditLogs, key = { it.id }) { log ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = log.action, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = UcsiCrimson)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (log.status == "SUCCESS") StatusSuccess.copy(alpha = 0.15f) else StatusError.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = log.status,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (log.status == "SUCCESS") StatusSuccess else StatusError,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = log.details, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Actor: ${log.actor} • ${log.timestamp}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    if (showBroadcastModal) {
        BroadcastAlertModal(
            onDismiss = { showBroadcastModal = false },
            onConfirm = { title, msg, sev ->
                onBroadcastAlert(title, msg, sev)
                showBroadcastModal = false
            }
        )
    }
}

@Composable
fun AdminMetricCard(title: String, value: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = UcsiNavy)
            Text(subtitle, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = UcsiCrimson)
        }
    }
}

@Composable
fun BroadcastAlertModal(
    onDismiss: () -> Unit,
    onConfirm: (title: String, msg: String, severity: String) -> Unit
) {
    var title by remember { mutableStateOf("Severe Flash Rain: Online Class Transition") }
    var message by remember { mutableStateOf("Waterlogging along Kemal Ataturk Avenue. Evening shift lectures are shifted to online Microsoft Teams co-delivery with KL Campus.") }
    var severity by remember { mutableStateOf("WARNING") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Broadcast High-Priority Alert", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Alert Headline") })
                OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Alert Details") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Severity: ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    listOf("CRITICAL", "WARNING", "INFO").forEach { sev ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 4.dp)) {
                            RadioButton(selected = severity == sev, onClick = { severity = sev })
                            Text(sev, fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, message, severity) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Broadcast to Campus", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.theme.*

@Composable
fun RoleSwitcherDialog(
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onRoleSelected: (String) -> Unit
) {
    val roles = listOf(
        Triple("STUDENT", "Sarah Rahman", "B.Sc. Computing (ID: 10023419) • Full Student Portal"),
        Triple("FACULTY", "Dr. Arif Chowdhury", "Dept. of Computer Science (FAC-882) • Class & Lab Access"),
        Triple("ADMIN", "Syed M. Ahmed", "Registrar & Campus Ops (REG-101) • Admin Dashboard & Alerts")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = UcsiCrimson)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Switch Active Campus Role", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Select an authenticated campus identity to verify role-based access control (RBAC):",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                roles.forEach { (roleKey, name, desc) ->
                    val isSelected = currentUser?.role == roleKey
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) UcsiCrimsonContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onRoleSelected(roleKey)
                                onDismiss()
                            }
                            .testTag("role_option_$roleKey")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(roleKey, fontWeight = FontWeight.Black, fontSize = 12.sp, color = if (isSelected) UcsiOnCrimsonContainer else UcsiNavy)
                                if (isSelected) {
                                    Surface(color = UcsiCrimson, shape = RoundedCornerShape(4.dp)) {
                                        Text("ACTIVE", color = androidx.compose.ui.graphics.Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(desc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun SecuritySettingsDialog(
    user: UserEntity?,
    sessionToken: String,
    onDismiss: () -> Unit
) {
    var mfaEnabled by remember { mutableStateOf(user?.mfaEnabled ?: true) }
    var biometricsEnabled by remember { mutableStateOf(user?.biometricsEnabled ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = UcsiCrimson)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Security & Encryption Center", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "End-to-End Encryption & Session Credentials",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Active Session Token (HMAC SHA-256):", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(sessionToken, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UcsiNavy)
                        Text("Transport: TLS 1.3 / AES-256-GCM at rest", fontSize = 10.sp, color = StatusSuccess, fontWeight = FontWeight.SemiBold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Two-Factor Authentication (2FA)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Mandatory for finance & credit transfer", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = mfaEnabled, onCheckedChange = { mfaEnabled = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Biometric Turnstile Pass", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Fingerprint / Face ID for Banani Gate", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = biometricsEnabled, onCheckedChange = { biometricsEnabled = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = UcsiNavy)
            ) {
                Text("Save Security Preferences")
            }
        }
    )
}

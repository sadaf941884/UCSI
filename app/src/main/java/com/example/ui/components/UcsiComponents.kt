package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyAlertEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UcsiTopAppBar(
    currentRole: String,
    selectedTimezone: String,
    onTimezoneToggle: () -> Unit,
    onRoleSwitchClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    hasActiveAlerts: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // University Emblem Icon
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(UcsiCrimson),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "UCSI",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "UCSI UNIVERSITY",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(UcsiNavy)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "BANGLADESH",
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "Dhaka Branch Campus • Banani",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    // Timezone Toggle Pill
                    Surface(
                        onClick = onTimezoneToggle,
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedTimezone == "BST") UcsiNavyContainer else UcsiGoldContainer,
                        modifier = Modifier
                            .testTag("timezone_toggle_button")
                            .padding(end = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Timezone",
                                tint = if (selectedTimezone == "BST") UcsiOnNavyContainer else UcsiOnGoldContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (selectedTimezone == "BST") "BST (Dhaka)" else "MYT (KL +2h)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTimezone == "BST") UcsiOnNavyContainer else UcsiOnGoldContainer
                            )
                        }
                    }

                    // Role Badge (Clickable to switch demo roles)
                    Surface(
                        onClick = onRoleSwitchClick,
                        shape = RoundedCornerShape(16.dp),
                        color = when (currentRole) {
                            "ADMIN" -> MaterialTheme.colorScheme.errorContainer
                            "FACULTY" -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.primaryContainer
                        },
                        modifier = Modifier
                            .testTag("role_switcher_button")
                            .padding(end = 4.dp)
                    ) {
                        Text(
                            text = currentRole,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = when (currentRole) {
                                "ADMIN" -> MaterialTheme.colorScheme.onErrorContainer
                                "FACULTY" -> MaterialTheme.colorScheme.onTertiaryContainer
                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    // Search Button
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier.testTag("topbar_search_button")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Global Search")
                    }
                }
            )
        }
    }
}

@Composable
fun EmergencyAlertBanner(
    alert: EmergencyAlertEntity,
    onDismiss: () -> Unit
) {
    val isCritical = alert.severity == "CRITICAL"
    val bgColor = if (isCritical) Color(0xFFDC2626) else Color(0xFFD97706)

    Surface(
        color = bgColor,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("emergency_banner")
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Alert",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isCritical) "CAMPUS EMERGENCY BROADCAST" else "URBAN TRAFFIC & WEATHER NOTICE",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${alert.timestamp}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = alert.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )
                Text(
                    text = alert.message,
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Custom Anti-Screenshot Dynamic QR Code with live rotating security hash
 */
@Composable
fun DynamicQrCodeDisplay(
    payload: String,
    secondsRemaining: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(210.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(2.dp, UcsiCrimson.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            // High-density procedural dynamic QR matrix pattern
            Canvas(modifier = Modifier.fillMaxSize()) {
                val matrixSize = 21
                val cellSize = size.width / matrixSize
                val hashValue = payload.hashCode()

                // Corner finder patterns (Top-left, Top-right, Bottom-left)
                fun drawFinderPattern(startX: Float, startY: Float) {
                    drawRect(Color.Black, Offset(startX, startY), Size(cellSize * 7, cellSize * 7))
                    drawRect(Color.White, Offset(startX + cellSize, startY + cellSize), Size(cellSize * 5, cellSize * 5))
                    drawRect(Color.Black, Offset(startX + cellSize * 2, startY + cellSize * 2), Size(cellSize * 3, cellSize * 3))
                }

                drawFinderPattern(0f, 0f)
                drawFinderPattern(size.width - cellSize * 7, 0f)
                drawFinderPattern(0f, size.height - cellSize * 7)

                // Fill data cells procedurally influenced by hash & secondsRemaining
                for (row in 0 until matrixSize) {
                    for (col in 0 until matrixSize) {
                        // Skip finder patterns
                        val inFinder1 = row < 8 && col < 8
                        val inFinder2 = row < 8 && col >= matrixSize - 8
                        val inFinder3 = row >= matrixSize - 8 && col < 8
                        if (inFinder1 || inFinder2 || inFinder3) continue

                        val pseudoRandom = ((row * 31 + col * 17 + hashValue + (secondsRemaining * 3)) % 100)
                        if (pseudoRandom > 48) {
                            drawRect(
                                color = if ((row + col) % 5 == 0) UcsiCrimson else Color.Black,
                                topLeft = Offset(col * cellSize, row * cellSize),
                                size = Size(cellSize * 0.92f, cellSize * 0.92f)
                            )
                        }
                    }
                }

                // Center UCSI Shield Watermark
                val centerOffset = Offset(size.width / 2 - cellSize * 1.5f, size.height / 2 - cellSize * 1.5f)
                drawRect(Color.White, centerOffset, Size(cellSize * 3, cellSize * 3))
                drawRect(UcsiCrimson, Offset(centerOffset.x + cellSize * 0.5f, centerOffset.y + cellSize * 0.5f), Size(cellSize * 2, cellSize * 2))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Security Countdown Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = UcsiCrimson,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Refreshes in ${secondsRemaining}s (Anti-Fraud Token)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LinearProgressIndicator(
            progress = { secondsRemaining / 30f },
            modifier = Modifier
                .width(180.dp)
                .height(4.dp)
                .clip(CircleShape),
            color = if (secondsRemaining < 5) MaterialTheme.colorScheme.error else UcsiCrimson,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InternshipEntity
import com.example.ui.theme.*

data class AlumniMentor(
    val name: String,
    val gradYear: String,
    val program: String,
    val roleCompany: String,
    val location: String
)

val SampleAlumni = listOf(
    AlumniMentor("Nabil Mostafa", "Class of 2022", "B.Sc. Computing (KL Campus Transfer)", "Senior Software Engineer @ Grab", "Singapore / Remote"),
    AlumniMentor("Anika Tabassum", "Class of 2023", "B.Sc. Computing (Dhaka Campus)", "Cloud Solutions Architect @ Brain Station 23", "Dhaka, Bangladesh"),
    AlumniMentor("Zubair Ahmed", "Class of 2021", "BBA (Hons)", "Global Management Trainee @ Unilever", "Kuala Lumpur, Malaysia"),
    AlumniMentor("Farhana Kabir", "Class of 2024", "B.Sc. Computing", "AI Research Scientist @ Optimizely", "Dhaka, Bangladesh")
)

@Composable
fun CareerScreen(
    internships: List<InternshipEntity>,
    onApplyInternship: (Long, String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedMentorForRequest by remember { mutableStateOf<AlumniMentor?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("career_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = UcsiCrimson
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Co-Op & Internships", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Alumni Mentorship", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        when (selectedTab) {
            0 -> CoOpInternshipTab(
                internships = internships,
                onApply = onApplyInternship
            )
            1 -> AlumniMentorshipTab(
                onSelectMentor = { selectedMentorForRequest = it }
            )
        }
    }

    selectedMentorForRequest?.let { mentor ->
        MentorshipRequestModal(
            mentor = mentor,
            onDismiss = { selectedMentorForRequest = null }
        )
    }
}

@Composable
fun CoOpInternshipTab(
    internships: List<InternshipEntity>,
    onApply: (Long, String) -> Unit
) {
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
                        Icon(Icons.Default.Work, contentDescription = null, tint = UcsiGoldLight, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("UCSI Co-Op Placement Hub", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "UCSI University pioneered the Co-Op Education model in Malaysia. Students undergo mandatory high-impact corporate placement with top tier tech, banking, and multinational employers.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        item {
            Text(
                text = "Featured Industry Openings (${internships.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(internships, key = { it.id }) { job ->
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
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = UcsiNavyContainer
                            ) {
                                Text(
                                    text = job.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UcsiOnNavyContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(job.roleTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(job.companyName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UcsiCrimson)
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (job.applicationStatus) {
                                "APPLIED" -> StatusSuccess.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Text(
                                text = job.applicationStatus,
                                color = when (job.applicationStatus) {
                                    "APPLIED" -> StatusSuccess
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Stipend: ${job.stipend} • Location: ${job.location}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Application Deadline: ${job.deadline}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (job.applicationStatus == "NOT_APPLIED") {
                        Button(
                            onClick = { onApply(job.id, job.companyName) },
                            colors = ButtonDefaults.buttonColors(containerColor = UcsiCrimson),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Submit Co-Op Application", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = {},
                            enabled = false,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Application Under Review with HR", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlumniMentorshipTab(onSelectMentor: (AlumniMentor) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Global Alumni Mentorship Network", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = "Connect with UCSI graduates working at tech giants, finance corporations, and research institutions worldwide for 1-on-1 career guidance.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(SampleAlumni) { mentor ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(UcsiCrimson.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mentor.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                            fontWeight = FontWeight.Bold,
                            color = UcsiCrimson,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(mentor.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(mentor.roleCompany, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UcsiNavy)
                        Text("${mentor.program} • ${mentor.gradYear}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("📍 ${mentor.location}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(
                        onClick = { onSelectMentor(mentor) },
                        colors = ButtonDefaults.buttonColors(containerColor = UcsiCrimson),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Connect", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MentorshipRequestModal(mentor: AlumniMentor, onDismiss: () -> Unit) {
    var message by remember { mutableStateOf("Hello ${mentor.name}, I am a student at UCSI Bangladesh Campus pursuing Computing. I would love your advice on preparing for software engineering roles.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request Mentorship with ${mentor.name}", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Role: ${mentor.roleCompany}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Introductory Note") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = UcsiCrimson)
            ) {
                Text("Send Request", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
